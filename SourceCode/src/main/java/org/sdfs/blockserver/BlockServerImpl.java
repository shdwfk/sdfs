package org.sdfs.blockserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.sdfs.commons.ISdfsCommand;
import org.sdfs.commons.SuperBlockInfo;
import org.sdfs.exceptions.SdfsException;
import org.sdfs.superblock.ISuperBlock;
import org.sdfs.superblock.MockSuperBlock;

/**
 * IBlockServer的实现类
 * @author wangfk
 *
 */
public class BlockServerImpl implements IBlockServer {
	private Map<Long, ISuperBlock> superBlocks = new HashMap<Long, ISuperBlock>();
	private ReentrantReadWriteLock superBlocksRWLock = new ReentrantReadWriteLock();

	public ISuperBlock getSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.readLock().lock();
		try {
			checkBlockExisting(blockId, true);
			return superBlocks.get(blockId);
		} finally {
			superBlocksRWLock.readLock().unlock();
		}
	}

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

	public void removeSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.writeLock().lock();
		try {
			checkBlockExisting(blockId, true);
			superBlocks.remove(blockId);
		} finally {
			superBlocksRWLock.writeLock().unlock();
		}
	}

	public void addNewSuperBlock(long blockId) throws SdfsException {
		superBlocksRWLock.writeLock().lock();
		try {
			checkBlockExisting(blockId, false);
			superBlocks.put(blockId, new MockSuperBlock());
		} finally {
			superBlocksRWLock.writeLock().unlock();
		}
	}

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

	public void execCommand(ISdfsCommand sdfsCommand) throws SdfsException {
		// TODO Auto-generated method stub
	}

	public String getNextFileUploadId(long blockId, long version) {
		//TODO
		throw new RuntimeException();
	}

	private void checkBlockExisting(long blockId, boolean expectedExisting) throws SdfsException {
		if (superBlocks.containsKey(blockId) ^ expectedExisting) {
			throw new SdfsException(
					expectedExisting ? "The block does not exist, blockId=" + blockId
							: "The block already exists, blockId=" + blockId);
		}
	}
}
