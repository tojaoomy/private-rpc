package com.tojaoomy.rabbitmq;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;
import com.tojaoomy.moon.rpc.SerializationUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Producer extends Endpoint {

	public Producer(String queueName){
		super(queueName);
	}
	
	public void sendMsg(Serializable object) throws IOException{
		sendByMandatory(object);
		channel.confirmSelect();
	}
	
	private void sendByMandatory(Serializable object) throws IOException{
		channel.basicPublish("", queueName, true/**开启mandatory */, MessageProperties.PERSISTENT_BASIC, SerializationUtil.serialize(object));
		channel.addReturnListener(new ReturnListener() {
			public void handleReturn(int replyCode,
		            String replyText,
		            String exchange,
		            String routingKey,
		            AMQP.BasicProperties properties,
		            byte[] body)
					throws IOException {
				System.out.println("mandatory return : " + SerializationUtil.deserialize(body, TransferMessage.class));
			}
		});
		/*channel.confirmSelect();
		channel.addConfirmListener(new ConfirmListener() {
			
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				
			}
			
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				
			}
		});*/
	}
	
	
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		Producer producer = new Producer("queue");
		for(int i = 0 ; i < 10; i++){
			TransferMessage message = new TransferMessage("id=" + i, new Date());
			producer.sendMsg(message);
			System.out.println("send msg : " + i);
		}
		producer.close();
		System.out.println("over");
		new CountDownLatch(1).await();
	}
}
