package com.lmax.ticketing.io;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by jelu on 15/10/14.
 */
public class RabbitEventHandler implements EventHandler<Message> {

    private static final String EXCHANGE_NAME = "ticket_orders";
    private final String routingKey;
    private Channel channel;

    public RabbitEventHandler(String host, String routingKey) {
        this.routingKey = routingKey;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onEvent(Message message, long sequence, boolean endOfBatch) throws Exception {

        //String routingKey = event

        ByteBuffer messageBuffer = message.getByteBuffer();
        messageBuffer.position(0);
        byte[] bytes = new byte[message.getSize()];
        messageBuffer.get(bytes, 0, bytes.length);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, bytes);

        messageBuffer.clear();

    }
}
