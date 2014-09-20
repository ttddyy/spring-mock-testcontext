package net.ttddyy.mocktestcontext;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * ContextLoader for mock context
 *
 * @author Tadaya Tsuyukubo
 */
// TODO: make this TestContextBootstrapper
public class MockAnnotationConfigContextLoader extends AnnotationConfigContextLoader {

	@Override
	protected void prepareContext(GenericApplicationContext context) {
		// create mock-context and set it to the top of appContext and beanFactory
		MockBeanFactory mockBeanFactory = new MockBeanFactory(context.getBeanFactory());
		ConfigurableBeanFactory rootBeanFactory = ApplicationContextUtils.getRootBeanFactory(context);
		rootBeanFactory.setParentBeanFactory(mockBeanFactory);
	}
}
