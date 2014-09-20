package net.ttddyy.mocktestcontext.config;

import net.ttddyy.mocktestcontext.service.BarService;
import net.ttddyy.mocktestcontext.service.BarServiceImpl;
import net.ttddyy.mocktestcontext.service.FooService;
import net.ttddyy.mocktestcontext.service.FooServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Service layer bean configuration.
 *
 * @author Tadaya Tsuyukubo
 */
@Configuration
public class ServiceConfig {

	@Bean
	public FooService fooService() {
		return new FooServiceImpl();
	}

	@Bean
	public BarService barServiceA() {
		return new BarServiceImpl("barA");
	}

	@Bean
	public BarService barServiceB() {
		return new BarServiceImpl("barB");
	}

}
