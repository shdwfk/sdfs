package org.sdfs.io.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

import org.sdfs.io.request.IRequest;
import org.sdfs.io.response.IResponse;
import org.sdfs.io.rpc.RpcMessageDecoder;
import org.sdfs.io.rpc.RpcMessageEncoder;


public class RequestRpcClient implements Closeable {
	private static final long DEFAULT_CALL_TIMEOUT_MILLIS = 60 * 1000L;
	private static final int DEFAULT_MAX_REQUEST_BUFFER_SIZE = 32;
	private static final long DEFAUL_CLEAN_CALL_FUTURE_INTERVAL_MILLIS = 10 * 1000L;

	private long callTimeOutMillis = DEFAULT_CALL_TIMEOUT_MILLIS;
	private int maxRequestBufferSize = DEFAULT_MAX_REQUEST_BUFFER_SIZE;
	private long cleanCallFutureIntervalMills = DEFAUL_CLEAN_CALL_FUTURE_INTERVAL_MILLIS;

	private RpcClientHandler rpcClientHandler;
	private ChannelFuture channelFuture;

	private static final int EVENT_GROUP_THREADS = 5;

	public RequestRpcClient setCallTimeOutMillis(long callTimeOutMillis) {
		this.callTimeOutMillis = callTimeOutMillis;
		return this;
	}

	public RequestRpcClient setMaxRequestBufferSize(int maxRequestBufferSize) {
		this.maxRequestBufferSize = maxRequestBufferSize;
		return this;
	}

	public RequestRpcClient setCleanCallFutureIntervalMills(long cleanCallFutureIntervalMills) {
		this.cleanCallFutureIntervalMills = cleanCallFutureIntervalMills;
		return this;
	}

	public RequestRpcClient connect(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup(EVENT_GROUP_THREADS);
        rpcClientHandler = new RpcClientHandler()
        		.setCallTimeOutMillis(callTimeOutMillis)
        		.setCleanCallFutureIntervalMills(cleanCallFutureIntervalMills)
        		.setMaxRequestBufferSize(maxRequestBufferSize);

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
        	@Override
        	public void initChannel(SocketChannel ch) throws Exception {
        		ch.pipeline().addLast(new RpcMessageEncoder<IRequest>(),
        				new RpcMessageDecoder<IResponse>(), rpcClientHandler);
        	}
        });

        // Start the client.
        channelFuture = b.connect(host, port).sync();
        return this;
	}

	public void join() throws InterruptedException {
		channelFuture.channel().closeFuture().sync();
	}

	@Override
	public void close() throws IOException {
		channelFuture.channel().close();
		try {
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
		}
		channelFuture.channel().eventLoop().shutdownGracefully();
	}

	public Future<IResponse> rpcCall(IRequest request) {
		return rpcClientHandler.addNewRequest(request);
	}
}
