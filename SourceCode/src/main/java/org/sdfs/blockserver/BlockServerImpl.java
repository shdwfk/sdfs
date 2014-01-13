package org.sdfs.blockserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.guice.BlockServerGuice;
import org.sdfs.superblock.ISuperBlock;

/**
 * IBlockServer的实现类
 * @author wangfk
 *
 */
public class BlockServerImpl implements IBlockServer {
	private Map<Long, ISuperBlock> superBlocks = new HashMap<Long, ISuperBlock>();
	private ReentrantReadWriteLock superBlocksRWLock = new ReentrantReadWriteLock();

	private String blockDir;

	@Override
	public void init(String blockDir) throws SdfsException {
		this.blockDir = blockDir;
		// TODO init
	}

	@Override
	public ISuperBlock getSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.readLock().lock();
		try {
			checkBlockExisting(blockId, true);
			return superBlocks.get(blockId);
		} finally {
			superBlocksRWLock.readLock().unlock();
		}
	}

	@Override
	public List<SuperBlockInfo> getAllSuperBlockInfos() {
		ArrayList<SuperBlockInfo> result = new ArrayList<SuperBlockInfo>();
		superBlocksRWLock.readLock().lock();
		try {
			for (ISuperBlock superBlock : superBlocks.values()) {
				result.add(superBlock.getBlockInfo());
			}
		} finally {
			superBlocksRWLock.readLock().unlock();
		}
		return result;
	}

	@Override
	public void removeSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.writeLock().lock();
		try {
			checkBlockExisting(blockId, true);
			superBlocks.remove(blockId);
		} finally {
			superBlocksRWLock.writeLock().unlock();
		}
	}

	@Override
	public void addNewSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.writeLock().lock();
		try {
			checkBlockExisting(blockId, false);
			ISuperBlock superBlock =
					BlockServerGuice.getInjector().getInstance(ISuperBlock.class);
			superBlock.init(blockDir, blockId);
			superBlocks.put(blockId, superBlock);
		} finally {
			superBlocksRWLock.writeLock().unlock();
		}
	}

	@Override
	public long getAvailableSize() {
		superBlocksRWLock.readLock().lock();
		try {
			long result = 0;
			for (ISuperBlock superBlock : superBlocks.values()) {
				result += superBlock.getAvailableSize();
			}
			return result;
		} finally {
			superBlocksRWLock.readLock().unlock();
		}
	}

	@Override
	public long getTotalSize() {
		superBlocksRWLock.readLock().lock();
		try {
			long result = 0;
			for (ISuperBlock superBlock : superBlocks.values()) {
				result += superBlock.getTotalSize();
			}
			return result;
		} finally {
			superBlocksRWLock.readLock().unlock();
		}
	}

	@Override
	public void execCommand(ISdfsCommand sdfsCommand) throws SdfsException {
		// TODO Auto-generated method stub
	}

	private void checkBlockExisting(long blockId, boolean expectedExisting) throws SdfsException {
		if (superBlocks.containsKey(blockId) ^ expectedExisting) {
			throw new SdfsException(
					expectedExisting ? "The block does not exist, blockId=" + blockId
							: "The block already exists, blockId=" + blockId);
		}
	}
}
