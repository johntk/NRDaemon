package com.ibm.nrdaemon.model;

import org.joda.time.Instant;

import java.sql.Timestamp;

public class DateRange {
    private final Instant from;
    private final Instant to;

    public DateRange(String from, String to) {
        this.from = TimestampUtils.parseTimestamp(from);
        this.to = TimestampUtils.parseTimestamp(to);

//        System.out.println("From " + from);
//        System.out.println("To " + to);
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
