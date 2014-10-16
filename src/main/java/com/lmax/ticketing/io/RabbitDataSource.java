package com.lmax.ticketing.io;

import com.lmax.disruptor.RingBuffer;
import com.lmax.ticketing.api.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

/**
 * Created by jelu on 15/10/14.
 */
public class RabbitDataSource implements Runnable {
    private final RingBuffer<Message> ringBuffer;

    private static final String EXCHANGE_NAME = "ticket_orders";
    private final String host;
    private final String routingKey;

    public RabbitDataSource(RingBuffer<Message> inboundBuffer, String host, String routingKey) {
        this.ringBuffer = inboundBuffer;
        this.host = host;
        this.routingKey = routingKey;
    }

    @Override
    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            QueueingConsumer consumer = new QueueingConsumer(channel);

            channel.basicConsume(queueName, true, consumer);


            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                String routingKey = delivery.getEnvelope().getRoutingKey();

                long sequence = ringBuffer.next();
                Message message = ringBuffer.get(sequence);

                message.getByteBuffer().clear();
                message.getByteBuffer().put(delivery.getBody());

                ringBuffer.publish(sequence);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
