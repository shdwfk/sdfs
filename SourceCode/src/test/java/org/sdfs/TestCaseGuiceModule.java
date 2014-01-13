package org.sdfs;

import org.junit.Ignore;
import org.sdfs.blockmaster.BlockMasterImpl;
import org.sdfs.blockmaster.IBlockMaster;
import org.sdfs.blockserver.BlockServerImpl;
import org.sdfs.blockserver.IBlockServer;
import org.sdfs.nameserver.INameServer;
import org.sdfs.nameserver.NameServerImpl;
import org.sdfs.superblock.ISuperBlock;
import org.sdfs.superblock.MockSuperBlock;

import com.google.inject.AbstractModule;

/**
 * 用于TestCase的Guice Module，描述Guice的注入映射关系
 * @author wangfk
 *
 */
@Ignore //通过Annotation表明这个类不是单元测试
public class TestCaseGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ISuperBlock.class).to(MockSuperBlock.class);
		bind(IBlockServer.class).to(BlockServerImpl.class).asEagerSingleton();
		bind(IBlockMaster.class).to(BlockMasterImpl.class).asEagerSingleton();
		bind(INameServer.class).to(NameServerImpl.class).asEagerSingleton();
	}
}
