package com.tojaoomy.moon.backend.server;

import java.net.InetAddress;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.tojaoomy.moon.annotation.Service;
import com.tojaoomy.moon.backend.register.ServiceRegistry;
import com.tojaoomy.moon.rpc.Request;
import com.tojaoomy.moon.rpc.Response;
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

@Component
public class RpcServer implements ApplicationContextAware, InitializingBean{
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	/**
	 * 服务启动的ip和端口号
	 */
	@Value("${moon.server.address}")
	private String serverAddress;
	
	@Autowired
	private ServiceRegistry serviceRegistry;
	
	private Map<String, Object> handlerMap = Maps.newConcurrentMap();
	
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline()
								.addLast(new RpcDecoder(Request.class))//将 RPC 请求进行解码（为了处理请求）
								.addLast(new RpcEncoder(Response.class))// 将 RPC 响应进行编码（为了返回响应）
								.addLast(new RpcHandler(handlerMap));// 处理 RPC 请求
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.info("server : [{}] started up on port : {}",InetAddress.getLocalHost().getHostAddress() , port);
			serviceRegistry.register(serverAddress);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Service.class);
		if(MapUtils.isNotEmpty(serviceBeanMap)){
			for(Object bean : serviceBeanMap.values()){
				String interfaceName = bean.getClass().getAnnotation(Service.class).value().getName();
				handlerMap.put(interfaceName, bean);
			}
		}
	}

}
