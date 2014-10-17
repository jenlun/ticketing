package com.lmax.ticketing.api;

import javolution.io.Struct;


public class PriceUpdate extends Struct
{
    public final Signed64 concertId = new Signed64();
    public final Signed64 sectionId = new Signed64();
    public final Float32  price = new Float32();
    public final Signed64 requestId = new Signed64();

    public static EventType type()
    {
        return EventType.PRICE_UPDATE;
    }
}
