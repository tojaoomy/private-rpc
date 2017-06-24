package com.tojaoomy.moon.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class RpcHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);
	
	private final Map<String, Object> handlerMap;
	
	public RpcHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Request msg) throws Exception {
		Response response = new Response();
		response.setRequestId(msg.getRequestId());
		try {
			Object result = handler(msg);
			response.setResult(result);
		} catch (Exception e) {
			LOGGER.error("方法调用异常", e);
			response.setError(e);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private Object handler(Request request) throws InvocationTargetException{
		String className = request.getClassName();
		Object bean = handlerMap.get(className);
		Class<?> beanClass = bean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		
		FastClass serviceFastClass = FastClass.create(beanClass);
		FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return serviceFastMethod.invoke(bean, parameters);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("server execution exception", cause);
		ctx.close();
	}

}
