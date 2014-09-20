package net.ttddyy.mocktestcontext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * @author Tadaya Tsuyukubo
 *         <p/>
 *         TODO: make this TestContextBootstrapper
 */
public class MockAnnotationConfigContextLoader extends AnnotationConfigContextLoader {

	@Override
	protected void prepareContext(GenericApplicationContext context) {

		// create mock-context
		MockBeanFactory mockBeanFactory = new MockBeanFactory(context.getBeanFactory());
		GenericApplicationContext mockApplicationContext = new GenericApplicationContext(mockBeanFactory);
		mockApplicationContext.refresh();

		// set mock-context to the parent of root-context
		ApplicationContext rootContext = context;
		while (rootContext.getParent() != null) {
			rootContext = rootContext.getParent();
		}
		((ConfigurableApplicationContext) rootContext).setParent(mockApplicationContext);

	}

}
