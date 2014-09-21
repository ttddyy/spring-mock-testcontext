package net.ttddyy.mocktestcontext;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ApplicationContextInitializer to enable auto mock feature.
 *
 * @author Tadaya Tsuyukubo
 */
// TODO: make this feature TestContextBootstrapper
public class MockFeatureApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		// create mock-beanfactory and set it to the top of appContext and beanFactory
		MockBeanFactory mockBeanFactory = new MockBeanFactory(applicationContext.getBeanFactory());
		ConfigurableBeanFactory rootBeanFactory = ApplicationContextUtils.getRootBeanFactory(applicationContext);
		rootBeanFactory.setParentBeanFactory(mockBeanFactory);  // place mock-beanfactory to the top
	}

}
