package org.sdfs.guice;

import org.sdfs.blockserver.BlockServerImpl;
import org.sdfs.blockserver.IBlockServer;
import org.sdfs.superblock.ISuperBlock;
import org.sdfs.superblock.SuperBlockImpl;

import com.google.inject.AbstractModule;

public class BlockServerGuice extends AbstractGuice {

	static class BlockServerGuiceMudule extends AbstractModule {

		@Override
		protected void configure() {
			bind(ISuperBlock.class).to(SuperBlockImpl.class);
			bind(IBlockServer.class).to(BlockServerImpl.class);
		}
	}

	static {
		init(new BlockServerGuiceMudule());
	}
}
