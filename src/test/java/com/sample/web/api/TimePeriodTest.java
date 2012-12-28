package com.sample.web.api;

import com.sample.web.api.TimePeriod;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author: Iain Porter
 */
public class TimePeriodTest {

    @Test
    public void startDateIsBeforeEndDate() {
        TimePeriod timePeriod = new TimePeriod();
        DateTime today = new DateTime();
        timePeriod.setStartDate(today.toDate());
        timePeriod.setEndDate(today.plusDays(1).toDate());
        assertThat(timePeriod.validate(), is(true));
    }

    @Test
    public void startDateIsAfterEndDate() {
        TimePeriod timePeriod = new TimePeriod();
        DateTime today = new DateTime();
        timePeriod.setStartDate(today.toDate());
        timePeriod.setEndDate(today.minusDays(1).toDate());
        assertThat(timePeriod.validate(), is(false));
    }
}
