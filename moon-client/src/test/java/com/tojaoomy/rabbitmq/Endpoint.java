package com.tojaoomy.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Endpoint {

	protected Channel channel;
	
	protected Connection connection;
	
	protected String queueName;
	
	public void init() throws IOException, TimeoutException{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		connection = factory.newConnection();
		
		channel = connection.createChannel();
		
		//创建exchange  
        channel.exchangeDeclare("queue-ex", "direct", true, false, null);  
		//Exclusive：排他队列，如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除
		channel.queueDeclare(queueName, false, false, false, null);
		
		//绑定exchange和queue  
//        channel.queueBind(queueName, "queue-ex", "*");  
	}
	
	public void close() throws IOException, TimeoutException{
		this.channel.close();
		this.connection.close();
	}

	public Endpoint(String queueName) {
		super();
		this.queueName = queueName;
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
}
