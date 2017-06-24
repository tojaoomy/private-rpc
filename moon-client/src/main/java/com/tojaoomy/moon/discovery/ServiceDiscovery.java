package com.tojaoomy.moon.discovery;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tojaoomy.moon.rpc.Constant;


@Component
public class ServiceDiscovery implements Constant, InitializingBean, DisposableBean{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${moon.registry.server}")
	private String registryAddress;
	
	@Value("${moon.zk.session.timeout}")
	private int sessionTimeout;
	
	@Value("${moon.zk.connection.timeout}")
	private int connectionTimeout;
	
	private volatile List<String> routerList = Lists.newArrayList();

	private CuratorFramework client;
	
	public void afterPropertiesSet() throws Exception {
		client = CuratorFrameworkFactory.builder()
			.connectString(registryAddress)
			.sessionTimeoutMs(sessionTimeout)
			.connectionTimeoutMs(connectionTimeout)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.defaultData(null)
			.build();
		
		client.start();
		
		byte[] data = client.getData().watched().forPath(ZK_DATA_PATH);
		routerList.add(new String(data, "UTF-8"));
		logger.info("路由地址" + JSON.toJSONString(routerList));
	}
	
	public String router(){
		String data = null;
        int size = routerList.size();
        if (size > 0) {
            if (size == 1) {
                data = routerList.get(0);
                logger.info("using only data: {}", data);
            } else {
                data = routerList.get(ThreadLocalRandom.current().nextInt(size));
                logger.info("using random data: {}", data);
            }
        }
        return data;
	}

	public void destroy() throws Exception {
		CloseableUtils.closeQuietly(client);
	}
	
	
}
