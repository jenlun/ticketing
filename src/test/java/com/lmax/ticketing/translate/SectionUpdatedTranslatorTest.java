package com.lmax.ticketing.translate;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.SectionUpdated;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SectionUpdatedTranslatorTest
{

    @Test
    public void shouldTranslate()
    {
        SectionUpdatedTranslator sectionUpdatedTranslator = new SectionUpdatedTranslator();
        
        sectionUpdatedTranslator.set(1234, 5678, 90);

        Message message = new Message();
        sectionUpdatedTranslator.translateTo(message, 0);

        assertThat(message.type.get(), is((Enum) EventType.SECTION_UPDATED));
        SectionUpdated sectionUpdated = message.event.asSectionUpdated;
        
        assertThat(sectionUpdated.concertId.get(), is(1234L));
        assertThat(sectionUpdated.sectionId.get(), is(5678L));
        assertThat(sectionUpdated.seatsAvailable.get(), is(90));
    }

}
