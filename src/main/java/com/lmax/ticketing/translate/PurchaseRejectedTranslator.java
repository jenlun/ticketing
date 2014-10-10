package com.lmax.ticketing.translate;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.*;

public class PurchaseRejectedTranslator implements EventTranslator<Message>
{
    private RejectionReason rejectionReason;
    private TicketPurchase ticketPurchase;

    @Override
    public void translateTo(Message message, long sequence)
    {
        message.type.set(EventType.ALLOCATION_REJECTED);
        
        AllocationRejected allocationRejected = message.event.asAllocationRejected;
        allocationRejected.accountId.set(ticketPurchase.accountId.get());
        allocationRejected.requestId.set(ticketPurchase.requestId.get());
        allocationRejected.reason.set(rejectionReason);
    }

    public void set(RejectionReason rejectionReason, TicketPurchase ticketPurchase)
    {
        this.rejectionReason = rejectionReason;
        this.ticketPurchase = ticketPurchase;
    }
}
