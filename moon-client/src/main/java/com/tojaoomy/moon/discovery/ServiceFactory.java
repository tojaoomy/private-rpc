package com.tojaoomy.moon.discovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tojaoomy.moon.client.RpcClient;
import com.tojaoomy.moon.rpc.Request;
import com.tojaoomy.moon.rpc.Response;

@Component
public class ServiceFactory {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${moon.server.address}")
	private String serverAddress;
	
	@Autowired
	private ServiceDiscovery serviceDiscovery;
	
	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass){
		return (T) Proxy.newProxyInstance(
	            interfaceClass.getClassLoader(),
	            new Class<?>[]{interfaceClass},
	            new InvocationHandler() {
	                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	                    Request request = new Request(); // 创建并初始化 RPC 请求
	                    request.setRequestId(UUID.randomUUID().toString());
	                    request.setClassName(method.getDeclaringClass().getName());
	                    request.setMethodName(method.getName());
	                    request.setParameterTypes(method.getParameterTypes());
	                    request.setParameters(args);

	                    if (serviceDiscovery != null) {
	                        serverAddress = serviceDiscovery.router(); // 发现服务
	                    }

	                    String[] array = serverAddress.split(":");
	                    String host = array[0];
	                    int port = Integer.parseInt(array[1]);

	                    RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
	                    Response response = client.invoke(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

	                    if (response.isFail()) {
//	                        throw response.getError();
	                    	logger.error("调用发生异常",response.getError());
	                    } 
	                    return response.getResult();
	                }
	            }
	        );
	}
}
