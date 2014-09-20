package net.ttddyy.mocktestcontext;

import net.ttddyy.mocktestcontext.controller.MyController;
import net.ttddyy.mocktestcontext.service.BarService;
import net.ttddyy.mocktestcontext.service.FooService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;

/**
 * @author Tadaya Tsuyukubo
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		MyTest.MyTestConfig.class
},
		loader = MockAnnotationConfigContextLoader.class
)
public class MyTest {

	@Configuration
	@ComponentScan(basePackageClasses = MyController.class)
	static class MyTestConfig {
	}

	@Autowired
	private MyController myController;

	@Autowired
	private FooService fooService;

	@Autowired
	@Qualifier("barA")
	private BarService barServiceA;

	@Autowired
	@Qualifier("barB")
	private BarService barServiceB;

	@Autowired
	@Qualifier("barB")
	private BarService anotherBarServiceB;

	// TODO: provide some ways to reset mocks everytime such as using TestExecutionListener

	@Test
	public void testFoo() {
		given(fooService.getName()).willReturn("FROM-MOCK");

		assertThat(myController.handleFoo(), is("FROM-MOCK"));
	}

	@Test
	public void testBar() {

		given(barServiceA.getName()).willReturn("FROM-MOCK-A");
		given(barServiceB.getName()).willReturn("FROM-MOCK-B");

		assertThat(myController.handleBar(), is("FROM-MOCK-A:FROM-MOCK-B"));
	}

	@Test
	public void anotherBar() {
		assertThat(anotherBarServiceB, is(sameInstance(barServiceB)));
	}


}
