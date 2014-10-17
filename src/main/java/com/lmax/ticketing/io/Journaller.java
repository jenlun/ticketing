package com.lmax.ticketing.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.Message;

import net.openhft.chronicle.*;
import net.openhft.chronicle.tools.ChronicleTools;


public class Journaller implements EventHandler<Message> {
    private final File directory;
    private FileChannel file = null;
    private final ByteBuffer[] buffers = new ByteBuffer[2];

    private IndexedChronicle chronicle = null;
    private ExcerptAppender appender = null;

    public Journaller(File directory) {
        this.directory = directory;
        buffers[0] = ByteBuffer.allocate(4);
    }

    @Override
    public void onEvent(Message event, long sequence, boolean endOfBatch) throws Exception {

        if (null == appender) {

            String basePath = this.directory.getPath() + "/SimpleChronicle";

            chronicle = new IndexedChronicle(basePath);

            chronicle = new IndexedChronicle(basePath);
            // write one object
            appender = chronicle.createAppender();

        }

        int size = event.getSize();

        buffers[0].clear();
        buffers[0].putInt(size).flip();
        buffers[1] = event.getByteBuffer();
        buffers[1].clear().limit(size);

        appender.startExcerpt(1000); // an upper limit to how much space in bytes this message should need.
        appender.write(buffers[0]);

        while (buffers[1].hasRemaining()) {
            appender.write(buffers[1]);
        }
        appender.finish();

        buffers[1].clear();
        buffers[1] = null;
    }
}
