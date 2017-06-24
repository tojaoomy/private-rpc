package com.tojaoomy.rabbitmq.sub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publisher {

	private final static String EXCHANGE_NAME = "rmq";

	public static void main(String[] args) throws IOException, TimeoutException {
		/**
		 * 创建连接连接到MabbitMQ
		 */
		ConnectionFactory factory = new ConnectionFactory();
		// 设置MabbitMQ所在主机ip或者主机名
		factory.setHost("127.0.0.1");
		// 创建一个连接
		Connection connection = factory.newConnection();
		// 创建一个频道
		Channel channel = connection.createChannel();
		// 指定转发——广播
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

		for (int i = 0; i < 300; i++) {
			// 发送的消息
			String message = "Hello World!";
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
		}

		// 关闭频道和连接
		channel.close();
		connection.close();
	}
}
