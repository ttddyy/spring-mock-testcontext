package net.ttddyy.mocktestcontext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;

/**
 * BeanFactory to manage mock beans.
 *
 * @author Tadaya Tsuyukubo
 */
// TODO: expose mocking info to beanFactory, so that user can inject those info
public class MockBeanFactory extends DefaultListableBeanFactory {

	private ListableBeanFactory originalBeanFactory;

	private MockInfoManager mockInfoManager = new MockInfoManager();

	private int counter = 0;  // used for generating unique name

	public MockBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.originalBeanFactory = (ListableBeanFactory) beanFactory;
	}


	// child context's getBeanNamesForType() calls this method via BeanFactoryUtils.beanNamesForTypeIncludingAncestors()
	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {

		// when original context contains the bean, such as test target
		String[] result = originalBeanFactory.getBeanNamesForType(type);
		if (result.length > 0) {
			return new String[]{};  // original beanFactory already contains target. so return empty for this mock beanFactory
		}

		String name = "mock-" + counter++;  // create unique name
		mockInfoManager.createMockInfo(name, type);

		return new String[]{name};  // always return single new unique name for mock candidates
	}


	// child beanFactory's findAutowireCandidates() calls this method in doResolveDependency()
	@Override
	protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {

		MockInfo mockInfo = mockInfoManager.getByBeanName(beanName);
		if (mockInfo == null) {
			return false;  // not scope of this context. may be resolved in child context
		}

		String qualifierName = getQualifierName(descriptor);
		if (StringUtils.hasText(qualifierName)) {
			mockInfo.setQualifierName(qualifierName);
		}
		return true;

	}


	// copy from QualifierAnnotationAutowireCandidateResolver
	final static Set<Class<? extends Annotation>> qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();

	static {
		qualifierTypes.add(Qualifier.class);
		try {
			qualifierTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.inject.Qualifier", MockBeanFactory.class.getClassLoader()));
		} catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}

	/**
	 * Checks whether the given annotation type is a recognized qualifier type.
	 */
	protected boolean isQualifier(Class<? extends Annotation> annotationType) {
		for (Class<? extends Annotation> qualifierType : qualifierTypes) {
			if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
				return true;
			}
		}
		return false;
	}


	private String getQualifierName(DependencyDescriptor descriptor) {
		Annotation[] annotationsToSearch = descriptor.getAnnotations();  // field or method annotations
		if (ObjectUtils.isEmpty(annotationsToSearch)) {
			return null;
		}

		for (Annotation annotation : annotationsToSearch) {

			Class<? extends Annotation> type = annotation.annotationType();
			if (isQualifier(type) && hasValidQualifierName(annotation)) {
				return (String) AnnotationUtils.getValue(annotation);
			}

			// check meta annotation
			for (Annotation metaAnn : type.getAnnotations()) {
				if (isQualifier(metaAnn.annotationType()) && hasValidQualifierName(metaAnn)) {
					return (String) AnnotationUtils.getValue(metaAnn);
				}
			}
		}

		return null;
	}

	private boolean hasValidQualifierName(Annotation annotation) {
		Object value = AnnotationUtils.getValue(annotation);
		return value != null && value instanceof String && StringUtils.hasText((String) value);
	}


	@Override
	protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {

		MockInfo mockInfo = mockInfoManager.getByBeanName(name);
		if (mockInfo == null) {  // in case for non-mock object
			return super.doGetBean(name, requiredType, args, typeCheckOnly);
		}

		T result;
		try {
			result = super.doGetBean(name, requiredType, args, typeCheckOnly);
		} catch (BeansException e) {

			// TODO: better impl and think about more corner cases

			String qualifierName = mockInfo.getQualifierName();
			if (StringUtils.isEmpty(qualifierName)) {
				// when qualifier doesn't exist but that bean has already mapped to the same type, then return already mapped bean

				List<MockInfo> candidates = mockInfoManager.getByTypeExceptMe(mockInfo);
				if (!CollectionUtils.isEmpty(candidates)) {
					for (MockInfo candidate : candidates) {
						if (StringUtils.isEmpty(candidate.getQualifierName())) {
							mockInfoManager.remove(mockInfo);  // existing match found, remove current entry
							// same type but has no qualifier. return that bean
							// TODO: current impl only grows alias because it always has new beanname.
							T bean = super.doGetBean(candidate.getBeanName(), null, null, false);
							super.registerAlias(candidate.getBeanName(), name);
							return bean;
						}
					}
				}
			} else {
				// qualifier exists, and same type(class) and same qualifier name has already mapped, then return the mapped bean
				MockInfo typeAndQualifierMatched = mockInfoManager.getByTypeAndQualifierNameExceptMe(mockInfo);
				if (typeAndQualifierMatched != null) {
					mockInfoManager.remove(mockInfo);  // existing match found, remove current entry
					// return matched bean
					// TODO: current impl only grows alias because it always has new beanname.
					T bean = super.doGetBean(typeAndQualifierMatched.getBeanName(), null, null, false);
					super.registerAlias(typeAndQualifierMatched.getBeanName(), name);
					return bean;
				}

			}

			// create and register the mock as singleton to this BeanFactory
			// TODO: currently hardcoded to mockito mock generation. change to strategy for user to select mocking style
			result = (T) mock(mockInfo.getType());
			this.registerSingleton(name, result);
		}
		return result;
	}

}
