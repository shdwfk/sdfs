package org.sdfs.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

abstract class AbstractGuice {
	private static Injector injectorInstance;

	static void init(AbstractModule module) {
		injectorInstance = Guice.createInjector(module);
	}

	public static void reset(AbstractModule module) {
		injectorInstance = Guice.createInjector(module);
	}

	public static Injector getInjector() {
		return injectorInstance;
	}
}
