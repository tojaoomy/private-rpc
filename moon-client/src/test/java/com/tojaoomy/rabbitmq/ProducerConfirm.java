package com.tojaoomy.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ProducerConfirm extends Endpoint {

	public static void main(String[] args) {  
        String exchangeName = "confirmExchange";  
        String queueName = "confirmQueue";  
        String routingKey = "confirmRoutingKey";  
        String bindingKey = "confirmRoutingKey";  
        int count = 100;  
          
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("localhost");  
          
        //创建生产者  
        SenderBatch batch = new SenderBatch(factory, count, exchangeName, queueName,routingKey,bindingKey);  
        SenderSingle single = new SenderSingle(factory, count, exchangeName, queueName,routingKey,bindingKey);  
        boolean flag = true;
        if(flag){
        	batch.run();
        }else{
        	single.run();
        }
    }  
}  
  
class SenderBatch  
{  
    private ConnectionFactory factory;  
    private int count;  
    private String exchangeName;  
    private String  queueName;  
    private String routingKey;  
    private String bindingKey;  
      
    public SenderBatch(ConnectionFactory factory,int count,String exchangeName,String queueName,String routingKey,String bindingKey) {  
        this.factory = factory;  
        this.count = count;  
        this.exchangeName = exchangeName;  
        this.queueName = queueName;  
        this.routingKey = routingKey;  
        this.bindingKey = bindingKey;  
    }  
      
    public void run() {  
        Channel channel = null;  
        try {  
            Connection connection = factory.newConnection();  
            channel = connection.createChannel();  
            //创建exchange  
            channel.exchangeDeclare(exchangeName, "direct", true, false, null);  
            //创建队列  
            channel.queueDeclare(queueName, true, false, false, null);  
            //绑定exchange和queue  
            channel.queueBind(queueName, exchangeName, bindingKey);  
            channel.confirmSelect();  
            //发送持久化消息  
            for(int i = 0;i < count;i++)  
            {  
                //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,  
                //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话  
                //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键  
                channel.basicPublish(exchangeName, routingKey,MessageProperties.PERSISTENT_BASIC, ("第"+(i+1)+"条消息").getBytes());  
                System.out.println("第"+(i+1)+"条消息");
            }  
            long start = System.currentTimeMillis();  
            channel.waitForConfirmsOrDie();  
            channel.addConfirmListener(new ConfirmListener() {
				
				public void handleNack(long deliveryTag, boolean multiple) throws IOException {
					System.out.println("handleNack");
				}
				
				public void handleAck(long deliveryTag, boolean multiple) throws IOException {
					System.out.println("handleAck");
				}
			});
            System.out.println("执行waitForConfirmsOrDie耗费时间: "+(System.currentTimeMillis()-start)+"ms");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  

class SenderSingle{
	private ConnectionFactory factory;  
    private int count;  
    private String exchangeName;  
    private String  queueName;  
    private String routingKey;  
    private String bindingKey;  
      
    public SenderSingle(ConnectionFactory factory,int count,String exchangeName,String queueName,String routingKey,String bindingKey) {  
        this.factory = factory;  
        this.count = count;  
        this.exchangeName = exchangeName;  
        this.queueName = queueName;  
        this.routingKey = routingKey;  
        this.bindingKey = bindingKey;  
    }  
      
    public void run() {  
        Channel channel = null;  
        try {  
            Connection connection = factory.newConnection();  
            channel = connection.createChannel();  
            //创建exchange  
            channel.exchangeDeclare(exchangeName, "direct", true, false, null);  
            //创建队列  
            channel.queueDeclare(queueName, true, false, false, null);  
            //绑定exchange和queue  
            channel.queueBind(queueName, exchangeName, bindingKey);  
            channel.confirmSelect();  
            //发送持久化消息  
            for(int i = 0;i < count;i++)  
            {  
                //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,  
                //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话  
                //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键  
                channel.basicPublish(exchangeName, routingKey,MessageProperties.PERSISTENT_BASIC, ("第"+(i+1)+"条消息").getBytes());  
                
                //waitForConfirms()与listener只能有一起起作用
               /* if(channel.waitForConfirms())  
                {  
                    System.out.println("发送成功");  
                }*/  
            }  
            
            channel.addConfirmListener(new ConfirmListener() {
				
				public void handleNack(long deliveryTag, boolean multiple) throws IOException {
					System.out.println("handleNack");
				}
				
				public void handleAck(long deliveryTag, boolean multiple) throws IOException {
					System.out.println("handleAck " + deliveryTag + " " + multiple);
				}
			});
            
            final long start = System.currentTimeMillis();  
            System.out.println("执行waitForConfirmsOrDie耗费时间: "+(System.currentTimeMillis()-start)+"ms");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
