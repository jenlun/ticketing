package com.lmax.ticketing.framework;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.PriceUpdate;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;
import com.lmax.ticketing.domain.Concert;
import com.lmax.ticketing.domain.ConcertServiceListener;
import com.lmax.ticketing.translate.*;

public class Publisher implements ConcertServiceListener
{
    private final Disruptor<Message> disruptor;
    private final PurchaseApprovedTranslator purchaseApprovedTranslator = new PurchaseApprovedTranslator();
    private final ConcertAvailableTranslator concertAvailableTranslator = new ConcertAvailableTranslator();
    private final PurchaseRejectedTranslator purchaseRejectedTranslator = new PurchaseRejectedTranslator();
    private final SectionUpdatedTranslator   sectionUpdatedTranslator   = new SectionUpdatedTranslator();
    private final PriceUpdatedTranslator priceUpdatedTranslator     = new PriceUpdatedTranslator();

    public Publisher(Disruptor<Message> disruptor)
    {
        this.disruptor = disruptor;
    }
    
    @Override
    public void onConcertAvailable(Concert concert)
    {
        concertAvailableTranslator.set(concert);
        disruptor.publishEvent(concertAvailableTranslator);
    }
    
    @Override
    public void onPurchaseApproved(TicketPurchase ticketPurchase)
    {
        purchaseApprovedTranslator.set(ticketPurchase);
        disruptor.publishEvent(purchaseApprovedTranslator);
    }
    
    @Override
    public void onPurchaseRejected(RejectionReason rejectionReason, TicketPurchase ticketPurchase)
    {
        purchaseRejectedTranslator.set(rejectionReason, ticketPurchase);
        disruptor.publishEvent(purchaseRejectedTranslator);
    }
    
    @Override
    public void onSectionUpdated(long concertId, long sectionId, int seatsAvailable)
    {
        sectionUpdatedTranslator.set(concertId, sectionId, seatsAvailable);
        disruptor.publishEvent(sectionUpdatedTranslator);
    }

    @Override
    public void onPriceUpdated(long concertId, long sectionId, float price) {
        priceUpdatedTranslator.set(concertId, sectionId, price);
        disruptor.publishEvent(priceUpdatedTranslator);
    }

}
