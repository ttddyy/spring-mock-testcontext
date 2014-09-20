package net.ttddyy.mocktestcontext;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 */
public class ApplicationContextUtils {

	public static ConfigurableBeanFactory getRootBeanFactory(ApplicationContext applicationContext) {

		// first, get root application context
		ApplicationContext rootContext = applicationContext;
		while (rootContext.getParent() != null) {
			rootContext = rootContext.getParent();
		}

		// then, get root beanFactory on root applciation context
		ConfigurableBeanFactory rootBeanFactory = ((ConfigurableApplicationContext) rootContext).getBeanFactory();
		while (rootBeanFactory.getParentBeanFactory() != null) {
			rootBeanFactory = (ConfigurableBeanFactory) rootBeanFactory.getParentBeanFactory();
		}
		return rootBeanFactory;
	}

}
