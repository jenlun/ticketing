package com.lmax.ticketing.main;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.domain.ConcertService;
import com.lmax.ticketing.framework.Dispatcher;
import com.lmax.ticketing.framework.Publisher;
import com.lmax.ticketing.io.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConcertServiceMain
{
    public static final int SERVER_PORT = 50001;
    public static final int CLIENT_PORT = 50002;

    public static final boolean USE_UDP = true; //TODO: inject handler instead

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException
    {
        Executor executor = Executors.newCachedThreadPool();
        WaitStrategy waitStrategy = new BlockingWaitStrategy();

        // Out bound Event Handling...
        Disruptor<Message> outboundDisruptor = new Disruptor<Message>(Message.FACTORY, 1024, executor, ProducerType.SINGLE, waitStrategy);
        
        UdpEventHandler udpEventHandler = new UdpEventHandler("localhost", CLIENT_PORT);
        RabbitEventHandler rabbitEventHandler = new RabbitEventHandler("localhost", "response");

        outboundDisruptor.handleEventsWith(USE_UDP ? udpEventHandler : rabbitEventHandler);
        outboundDisruptor.start();

        // In bound Event Handling
        Disruptor<Message> inboundDisruptor = new Disruptor<Message>(Message.FACTORY, 1024, executor, ProducerType.MULTI, waitStrategy);

        Journaller journaller = new Journaller(new File("/tmp"));

        Publisher publisher = new Publisher(outboundDisruptor);
        ConcertService concertService = new ConcertService(publisher);
        Dispatcher dispatcher = new Dispatcher(concertService);


        // replay previous events from journal
        // is this the right place?
        // dispatcher.replay();

        inboundDisruptor.handleEventsWith(journaller).then(dispatcher);
        RingBuffer<Message> inboundBuffer = inboundDisruptor.start();

        if(USE_UDP)
        {
            UdpDataSource udpDataSource = new UdpDataSource(inboundBuffer, SERVER_PORT);
            udpDataSource.bind();
            udpDataSource.run();
        }
        else
        {
            RabbitDataSource rabbitDataSource = new RabbitDataSource(inboundBuffer, "localhost", "order");
            rabbitDataSource.run();
        }




    }
}
