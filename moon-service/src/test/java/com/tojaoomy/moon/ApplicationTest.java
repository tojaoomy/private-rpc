package com.tojaoomy.moon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import com.tojaoomy.moon.rpc.RpcDecoder;
import com.tojaoomy.moon.rpc.RpcEncoder;
import com.tojaoomy.moon.rpc.RpcHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {
   
	@Test
	public void test() throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline()
								.addLast(new RpcDecoder())
								.addLast(new RpcEncoder())
								.addLast(new RpcHandler(null));
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			String serverAddress = "127.0.0.1:16800";
			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			System.out.println("start up");
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	@Test
	public void addressTest() throws UnknownHostException{
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}
	
	@Test
	public void testZk() throws Exception{
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString("localhost:2181")
				.sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3))
				.defaultData(null)
				.build();
			
			client.start();
			
			List<String> forPath = client.getChildren().forPath("/registry");
			byte[] data = client.getData().watched().forPath("/registry/data");
			System.out.println(new String(data, "UTF-8"));
	}
}
