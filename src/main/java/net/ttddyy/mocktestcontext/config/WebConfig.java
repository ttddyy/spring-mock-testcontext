package net.ttddyy.mocktestcontext.config;

import net.ttddyy.mocktestcontext.controller.MyController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Web layer bean configuration.
 *
 * @author Tadaya Tsuyukubo
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = MyController.class)
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
//		registry.enableContentNegotiation(new MappingJackson2JsonView());
//		registry.jsp();
	}
}
