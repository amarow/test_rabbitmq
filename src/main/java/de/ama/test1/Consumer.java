package de.ama.test1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://ama:modrow@localhost");
        factory.setConnectionTimeout(300000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("q1", true, false , false, null);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume("q1", false, consumer);

        while (true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            try {
                if (delivery != null){
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println("message received = " + message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            } catch (Exception e) {
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), true);
            }
        }
    }
}
