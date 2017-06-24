package com.tojaoomy.moon.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tojaoomy.moon.rpc.Request;
import com.tojaoomy.moon.rpc.Response;
import com.tojaoomy.moon.rpc.RpcDecoder;
import com.tojaoomy.moon.rpc.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<Response> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String host;
	
	private int port;
	
	private Response response;
	
	private final Object lock = new Object();

	public RpcClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public Response invoke(Request request) throws InterruptedException{
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new RpcEncoder(Request.class)) // 将 RPC 请求进行编码（为了发送请求）
                            .addLast(new RpcDecoder(Response.class)) // 将 RPC 响应进行解码（为了处理响应）
                            .addLast(RpcClient.this); // 使用 RpcClient 发送 RPC 请求
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            synchronized (lock) {
            	lock.wait(); // 未收到响应，使线程等待
            }

            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Response msg) throws Exception {
		this.response = msg;
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("接受请求异常", cause);
		ctx.close();
	}

}
