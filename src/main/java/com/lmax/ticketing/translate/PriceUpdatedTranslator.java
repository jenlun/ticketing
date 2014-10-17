package com.lmax.ticketing.translate;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.PriceUpdated;
import com.lmax.ticketing.api.SectionUpdated;

public class PriceUpdatedTranslator implements EventTranslator<Message>
{
    private long concertId;
    private long sectionId;
    private float price;

    @Override
    public void translateTo(Message message, long sequence)
    {
        message.type.set(EventType.PRICE_UPDATED);

        PriceUpdated priceUpdated = message.event.asPriceUpdated;

        priceUpdated.concertId.set(concertId);
        priceUpdated.sectionId.set(sectionId);
        priceUpdated.version.set(sequence);
        priceUpdated.price.set(price);
    }

    public void set(long concertId, long sectionId, float price)
    {
        this.concertId = concertId;
        this.sectionId = sectionId;
        this.price = price;
    }
}
