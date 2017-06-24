package com.tojaoomy.rabbitmq;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.tojaoomy.moon.rpc.SerializationUtil;

public class QueueConsumer extends Endpoint implements Runnable, Consumer {

	public void handleConsumeOk(String consumerTag) {
		
	}

	public void handleCancelOk(String consumerTag) {
		System.out.println("Comsumer " + consumerTag + " registered");
	}

	public void handleCancel(String consumerTag) throws IOException {
		
	}

	public void handleDelivery(String consumerTag, Envelope envelope,
            AMQP.BasicProperties properties, byte[] data) throws IOException {
		System.out.println(JSONObject.toJSONString(SerializationUtil.deserialize(data, TransferMessage.class)));
		channel.basicAck(envelope.getDeliveryTag(), false);  
	}

	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		
	}

	public void handleRecoverOk(String consumerTag) {
		
	}

	public QueueConsumer(String queueName) {
		super(queueName);
	}
	
	public void run() {
		try {
			channel.basicConsume(queueName, false, this);
			channel.basicQos(40);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		QueueConsumer consumer = new QueueConsumer("queue");
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();
		System.out.println("consumer over");
	}

}
