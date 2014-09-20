package net.ttddyy.mocktestcontext.controller;

import net.ttddyy.mocktestcontext.service.BarService;
import net.ttddyy.mocktestcontext.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample controller.
 *
 * @author Tadaya Tsuyukubo
 */
@RestController
public class MyController {

	// TODO: autowire, name specified bean
	@Autowired
	FooService fooService;

	@Autowired
	@Qualifier("barA")
	BarService barServiceA;

	@Autowired
	@Qualifier("barB")
	BarService barServiceB;

	@RequestMapping("/foo")
	public String handleFoo() {
		return fooService.getName();
	}

	@RequestMapping("/bar")
	public String handleBar() {
		return barServiceA.getName() + ":" + barServiceB.getName();
	}
}
