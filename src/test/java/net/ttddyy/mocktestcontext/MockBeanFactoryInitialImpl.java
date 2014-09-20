package net.ttddyy.mocktestcontext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.*;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class MockBeanFactoryInitialImpl extends DefaultListableBeanFactory {

	private Map<String, Object> mockMap = new ConcurrentHashMap<String, Object>();
	private MultiValueMap<Class<?>, String> mockBeanNamesByType = new LinkedMultiValueMap<Class<?>, String>();
	private Map<String, Class<?>> typeByBeanName = new HashMap<String, Class<?>>();

	private ListableBeanFactory originalBeanFactory;

	public MockBeanFactoryInitialImpl(AutowireCapableBeanFactory beanFactory) {
		this.originalBeanFactory = (ListableBeanFactory) beanFactory;
	}

	@Override
	protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {
		T result;
		try {
			result = super.doGetBean(name, requiredType, args, typeCheckOnly);
		} catch (BeansException e) {

			Class<? extends T> classToMock = (Class<? extends T>) typeByBeanName.get(name);
			result = mock(classToMock);  // TODO: clean up
			this.registerSingleton(name, result);  // register the mock as singleton to this BeanFactory

//			result = (T) mockMap.get(name);
//			if (result == null) {
//				Class<? extends T> classToMock = (Class<? extends T>) typeByBeanName.get(name);
//				result = mock(classToMock);  // TODO: clean up
////			this.registerSingleton(name, result);  // register the mock as singleton to this BeanFactory
//				mockMap.put(name, result);
//			}

//			result = (T) mockMap.get(name);
		}
		return result;
//		return mock(requiredType);
	}

//	@Override
//	public boolean containsLocalBean(String name) {
//		boolean contains = super.containsLocalBean(name);
//		if (contains) {
//			return true;
//		}
//		contains = mockMap.containsKey(name);
//		return contains;
//	}

	private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<Class<?>, String[]>(64);

	private int counter = 0;

	// BeanFactoryUtils.beanNamesForTypeIncludingAncestors() calls this.
	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		String[] result = super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		if (result.length > 0) {
			// TODO: when this.registerSinglton() is used for mocked beans, this will contain bean names for
			// existing same type beans. Will be a problem for qualifier(same bean, different name)
			List<String> mockBeanNames = mockBeanNamesByType.get(type);
			if (CollectionUtils.isEmpty(mockBeanNames)) {
				return result;
			}
			// check qualifier
			boolean hasQualifier = false;
			for (String mockBeanName : mockBeanNames) {
				String qualifierName = qualifierByBeanName.get(mockBeanName);


				if (qualifierByBeanName.containsKey(mockBeanName)) {
					hasQualifier = true;
					break;
				}
			}
			if (!hasQualifier) {
				return result;
			}
		}

		// when original context contains the bean, such as test target
		result = originalBeanFactory.getBeanNamesForType(type);
		if (result.length > 0) {
			return new String[]{};  // original context already contains target. so return empty for this mock context
		}


		// need to exclude calls from
		// TODO: think about this
		if (type.isAssignableFrom(BeanDefinitionRegistryPostProcessor.class) ||
				type.isAssignableFrom(BeanFactoryPostProcessor.class) ||
				type.isAssignableFrom(BeanPostProcessor.class) ||
				type.isAssignableFrom(ApplicationListener.class) ||
				type.isAssignableFrom(LoadTimeWeaverAware.class) ||
				type.isAssignableFrom(Lifecycle.class)) {
			return result;
		}

		// for web context
		// need to exclude calls from spring-mvc framework triggered by BeanFactoryUtils.beanNamesForTypeIncludingAncestors()
		// TODO: think about this
		if (type.isAssignableFrom(MappedInterceptor.class)) {
			return result;
		}

//		Object mock = mock(type);
		// TODO: think about same type different qualifier
		String name = "mock-" + counter++;
//		mockMap.put(name, mock);
		mockBeanNamesByType.add(type, name);  // register new beanName to the bean type(class)
		typeByBeanName.put(name, type);

		List<String> candidateNames = mockBeanNamesByType.get(type);  // new name always come last
		return candidateNames.toArray(new String[candidateNames.size()]);

//		this.registerSingleton(name, mock);  // register the mock as singleton to this BeanFactory
//
//		return new String[]{name};
	}

//	private static class MockAutowireCandidateResolver extends SimpleAutowireCandidateResolver {
//		@Override
//		public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
////			descriptor.getAnnotations();
////			RootBeanDefinition bd = (RootBeanDefinition)bdHolder.getBeanDefinition();
////			bd.addQualifier(new AutowireCandidateQualifier(bd.getTargetType(), ""));
////			return super.isAutowireCandidate(bdHolder, descriptor);
//			return true;
//		}
//	}
//
//	private AutowireCandidateResolver autowireCandidateResolver = new MockAutowireCandidateResolver();
//
//	@Override
//	public AutowireCandidateResolver getAutowireCandidateResolver() {
//		return autowireCandidateResolver;
//	}
//

	private Map<String, String> qualifierByBeanName = new HashMap<String, String>();

	@Override
	protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
//		return super.isAutowireCandidate(beanName, descriptor, resolver);
//		return isAutowireCandidate(beanName, descriptor, resolver);
//	}
//
//	@Override
//	protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {

		String qualifierName = getQualifierName(descriptor);
		if (StringUtils.isEmpty(qualifierName)) {
			return true;  // no qualifier provided, always match
		}


		String name = qualifierByBeanName.get(beanName);
		if (name == null) {
			qualifierByBeanName.put(beanName, qualifierName);
			return true;  // beanName is not asociated to qualifier, associate it
		}

		if (qualifierName.equals(name)) {
			return true;  // already associated with this qualifier
		} else {
			return false;  //  has associated to different qualifier
		}

	}


	// copy from QualifierAnnotationAutowireCandidateResolver
	final static Set<Class<? extends Annotation>> qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();

	static {
		qualifierTypes.add(Qualifier.class);
		try {
			qualifierTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.inject.Qualifier", MockBeanFactoryInitialImpl.class.getClassLoader()));
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


}
