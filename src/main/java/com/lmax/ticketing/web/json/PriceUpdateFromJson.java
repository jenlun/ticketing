package com.lmax.ticketing.web.json;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.PriceUpdate;
import net.minidev.json.JSONObject;

/**
 * Created by jelu on 2014-10-17.
 */
public class PriceUpdateFromJson implements EventTranslator<Message> {
    private final JSONObject object;

    public PriceUpdateFromJson(JSONObject object) {

        this.object = object;
    }

    @Override
    public void translateTo(Message message, long sequence)
    {
        message.type.set(EventType.PRICE_UPDATE);

        Number concertId = (Number) object.get("concertId");
        Number sectionId = (Number) object.get("sectionId");
        float price = Float.parseFloat((String) object.get("price"));
        Number requestId = (Number) object.get("requestId");

        PriceUpdate priceUpdate = message.event.asPriceUpdate;

        priceUpdate.concertId.set(concertId.longValue());
        priceUpdate.sectionId.set(sectionId.longValue());
        priceUpdate.price.set(price);
        priceUpdate.requestId.set(requestId.longValue());
    }
}
