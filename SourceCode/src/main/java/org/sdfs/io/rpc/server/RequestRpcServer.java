package org.sdfs.io.rpc.server;

import org.sdfs.io.rpc.server.interfaces.IRequestExecutor;
import org.sdfs.io.rpc.server.interfaces.IRequestHandler;
import org.sdfs.io.rpc.server.interfaces.IRequestInvokeAdaptor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RequestRpcServer {
	private static final int NIO_PARENT_EVENT_LOOKUP_GROUP_CONCURRENCY = 5;
	private int messageHandleConcurrency;
	private IRequestExecutor requestExecutor;

	/**
	 * @param requestExecuteConcurrency
	 *  request execute concurrency
	 * @param messageHandleConcurrency
	 *  request message receive and send concurrency
	 * @param requestInvokeAdaptor
	 *  request invoker
	 * 
	 */
	public RequestRpcServer(int requestExecuteConcurrency,
			int messageHandleConcurrency,
			IRequestInvokeAdaptor requestInvokeAdaptor) {
		this(   messageHandleConcurrency, 
				new DefaultRequestExecutor(requestExecuteConcurrency),
				requestInvokeAdaptor);
	}

	public RequestRpcServer(
			int messageHandleConcurrency, 
			IRequestExecutor requestExecutor,
			IRequestInvokeAdaptor requestInvokeAdaptor
			) {
		this.messageHandleConcurrency = messageHandleConcurrency;
		requestExecutor.setRequestInvokeAdaptor(requestInvokeAdaptor);
		this.requestExecutor = requestExecutor;
	}

	/**
	 * This method will not returned until the rpc service stopped
	 * @param port
	 * @throws InterruptedException
	 */
	public void startServer(int port) throws InterruptedException {
		EventLoopGroup bossGroup =
				new NioEventLoopGroup(NIO_PARENT_EVENT_LOOKUP_GROUP_CONCURRENCY);
        EventLoopGroup workerGroup = new NioEventLoopGroup(messageHandleConcurrency);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 IRequestHandler requestHandler = new DefaultRequestHandler();
                	 requestHandler.setRequestExecutor(requestExecutor);
                     ch.pipeline().addLast(new RequestDecoder(),
                    		 new ResponseEncoder(), requestHandler);
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
    
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
    
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}

	public Thread asyncStartRequestRpcServer(final int port) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					startServer(port);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
		thread.start();
		return thread;
	}

}
