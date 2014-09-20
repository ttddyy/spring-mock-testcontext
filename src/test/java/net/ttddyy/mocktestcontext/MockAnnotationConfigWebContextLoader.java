package net.ttddyy.mocktestcontext;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
// TODO: make this TestContextBootstrapper
public class MockAnnotationConfigWebContextLoader extends AnnotationConfigWebContextLoader {

	@Override
	protected void customizeContext(GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {

		// create mock-context and set it to the top of appContext and beanFactory
		MockBeanFactory mockBeanFactory = new MockBeanFactory(context.getBeanFactory());
		ConfigurableBeanFactory rootBeanFactory = ApplicationContextUtils.getRootBeanFactory(context);
		rootBeanFactory.setParentBeanFactory(mockBeanFactory);

	}

}
