package org.sdfs.guice;

import org.sdfs.blockserver.BlockServerImpl;
import org.sdfs.blockserver.IBlockServer;
import org.sdfs.superblock.ISuperBlock;
import org.sdfs.superblock.SuperBlockImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BlockServerGuice {

	static class BlockServerGuiceMudule extends AbstractModule {

		@Override
		protected void configure() {
			bind(ISuperBlock.class).to(SuperBlockImpl.class);
			bind(IBlockServer.class).to(BlockServerImpl.class);
		}
	}

	private static Injector injectorInstance;
	synchronized static void init(AbstractModule module) {
		injectorInstance = Guice.createInjector(module);
	}

	public synchronized static void reset(AbstractModule module) {
		injectorInstance = Guice.createInjector(module);
	}

	public synchronized static Injector getInjector() {
		if (injectorInstance == null) {
			init(new BlockServerGuiceMudule());
		}
		return injectorInstance;
	}

}
