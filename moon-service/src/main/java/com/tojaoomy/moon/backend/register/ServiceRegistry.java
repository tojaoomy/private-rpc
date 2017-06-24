package com.tojaoomy.moon.backend.register;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tojaoomy.moon.rpc.Constant;

@Component
public class ServiceRegistry implements Constant, InitializingBean, DisposableBean{
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${moon.registry.server}")
	private String registryAddress;
	
	@Value("${moon.zk.session.timeout}")
	private int sessionTimeout;
	
	@Value("${moon.zk.connection.timeout}")
	private int connectionTimeout;

	private CuratorFramework client;
	
	public void afterPropertiesSet() throws Exception {
		client = CuratorFrameworkFactory.builder()
			.connectString(registryAddress)
			.sessionTimeoutMs(sessionTimeout)
			.connectionTimeoutMs(connectionTimeout)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.defaultData(null)
			.build();
	}
	
	public void register(String data){
		try {
			client.start();
			if(client.checkExists().forPath(ZK_DATA_PATH) != null){
				client.delete().deletingChildrenIfNeeded().forPath(ZK_DATA_PATH);
			}
				
			
			client.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.EPHEMERAL)
			.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
			.forPath(ZK_DATA_PATH, data.getBytes());
			logger.info("create path {} , data {} success ", ZK_DATA_PATH, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() throws Exception {
		if(client != null){
			CloseableUtils.closeQuietly(client);
		}
	}
	
}
