package com.lmax.ticketing.web.json;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.PriceUpdated;
import com.lmax.ticketing.api.SectionUpdated;
import net.minidev.json.JSONObject;

public class PriceUpdatedToJson {
    public JSONObject toJson(PriceUpdated priceUpdated) {
        JSONObject json = new JSONObject();

        json.put("concertId", priceUpdated.concertId.get());
        json.put("sectionId", priceUpdated.sectionId.get());
        json.put("version", priceUpdated.version.get());
        json.put("price", priceUpdated.price.get());
        json.put("type", EventType.PRICE_UPDATED.name());

        return json;
    }
}
