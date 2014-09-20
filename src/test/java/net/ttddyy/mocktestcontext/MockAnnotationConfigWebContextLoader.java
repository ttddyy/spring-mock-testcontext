package net.ttddyy.mocktestcontext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * @author Tadaya Tsuyukubo
 *
 * TODO: make this TestContextBootstrapper
 */
public class MockAnnotationConfigWebContextLoader extends AnnotationConfigWebContextLoader {

	@Override
	protected void customizeContext(GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {

//		MockBeanFactory mockBeanFactory = new MockBeanFactory(context.getBeanFactory());
//		GenericApplicationContext mockContext = new GenericApplicationContext(mockBeanFactory);
//		mockContext.refresh();

		// create mock-context
		MockBeanFactory mockBeanFactory = new MockBeanFactory(context.getBeanFactory());
		GenericApplicationContext mockContext = new GenericApplicationContext(mockBeanFactory);
		mockContext.refresh();

		ApplicationContext rootContext = context;
		while (rootContext.getParent() != null) {
			rootContext = rootContext.getParent();
		}
		((ConfigurableApplicationContext) rootContext).setParent(mockContext);

	}

}
