package net.ttddyy.mocktestcontext;

import net.ttddyy.mocktestcontext.config.WebConfig;
import net.ttddyy.mocktestcontext.controller.MyController;
import net.ttddyy.mocktestcontext.service.BarService;
import net.ttddyy.mocktestcontext.service.FooService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;

/**
 * Unittest for controller with web env.
 *
 * @author Tadaya Tsuyukubo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
		classes = WebConfig.class,
		initializers = MockFeatureApplicationContextInitializer.class  // TODO: make it TestContextBootstrapper
)
public class MyWebTest {

	@Autowired
	private MyController myController;  // should be actual bean

	@Autowired
	private FooService fooService;  // should be mock

	@Autowired
	@Qualifier("barA")
	private BarService barServiceA;  // should be mock different from barServiceB

	@Autowired
	@Qualifier("barB")
	private BarService barServiceB;  // should be mock different from barServiceA


	@Autowired
	@Qualifier("barB")
	private BarService anotherBarServiceB;  // should be same mock as barServiceA

	// TODO: provide some ways to reset mocks everytime such as using TestExecutionListener

	@Test
	public void testFoo() {
		given(fooService.getName()).willReturn("FROM-MOCK");

		assertThat(myController.handleFoo(), Matchers.is("FROM-MOCK"));
	}

	@Test
	public void testBar() {

		given(barServiceA.getName()).willReturn("FROM-MOCK-A");
		given(barServiceB.getName()).willReturn("FROM-MOCK-B");

		assertThat(myController.handleBar(), Matchers.is("FROM-MOCK-A:FROM-MOCK-B"));
	}

	@Test
	public void anotherBar() {
		assertThat(anotherBarServiceB, Matchers.is(sameInstance(barServiceB)));
	}

}
