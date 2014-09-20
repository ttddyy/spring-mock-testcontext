package net.ttddyy.mocktestcontext.service;

/**
 * @author Tadaya Tsuyukubo
 */
public class BarServiceImpl implements BarService {

	private String name;

	public BarServiceImpl(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
