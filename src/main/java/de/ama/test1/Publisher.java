package de.ama.test1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class Publisher {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://ama:modrow@localhost");
        factory.setConnectionTimeout(300000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("q1", true, false , false, null);

        for (int i = 0; i < 5000; i++) {
            String message = "message"+i;
            channel.basicPublish("","q1", null, message.getBytes());
            System.out.println("message = " + message);
            Thread.sleep(5000);
        }

    }
}
