package com.ibm.nrdaemon.model;

import org.joda.time.Instant;

/** This class is used to to store the daterange from and to values in the properties file,
 * it is accessed in the MakeRequest class and used in the New Relic request, it is also used in the PollThread
 * Class in the worker thread to manage the requests range*/
public class DateRange {
    private final Instant from;
    private final Instant to;

    public DateRange(String from, String to) {
        this.from = TimestampUtils.parseTimestamp(from);
        this.to = TimestampUtils.parseTimestamp(to);
    }

    public DateRange(long from, long to) {
        this.from = new Instant(from);
        this.to = new Instant(to);
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("{ \"from\": \"%s\", \"to\": \"%s\" }", TimestampUtils.format(from), TimestampUtils.format(to));
    }
}
