package com.kenesis.transcoder.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueListener {
    Logger log = LoggerFactory.getLogger(QueueListener.class);

    public void invoke(String uri, String queue, int maxthread ) throws Exception{
        log.info("QueueListener has been started");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        ExecutorService es = Executors.newFixedThreadPool(maxthread);
        Connection conn = factory.newConnection(es);

        // Thread 당 다른 Channel 을 사용하기 위해서 Thread수 만큼 별도의 채널을 생성하낟.
        for(int i=0;i<maxthread;i++){
            Channel channel = conn.createChannel();
            channel.basicQos(1);

            if(queue.equals("encode"))
            {
                channel.basicConsume(queue,true ,new EncodeConsumer(channel));
            }
            else if(queue.equals("delete"))
            {
                channel.basicConsume(queue,true ,new DeleteConsumer(channel));
            }
            else
            {
                throw new Exception();
            }
        }

        log.info("Invoke "+maxthread+" thread and wait for listening");
    } //invoke
}
