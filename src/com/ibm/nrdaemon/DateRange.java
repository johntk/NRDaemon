package com.ibm.nrdaemon;

import java.sql.Timestamp;

/**
 * Created by Cloud on 06/11/2015.
 */
public class DateRange {


    private final String from;
    private final String to;

    public DateRange(String from, String to) {
        this.from = from;
        this.to = to;
    }

//    public DateRange(long from, long to) {
//        this.from = new Timestamp(from);
//        this.to = new Timestamp(to);
//    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("{ \"from\": \"%s\", \"to\": \"%s\" }", from, to);
    }


//    private final Timestamp from;
//    private final Timestamp to;
//
//    public DateRange(String from, String to) {
//        this.from = TimestampUtils.parseTimestamp(from);
//        this.to = TimestampUtils.parseTimestamp(to);
//    }
//
//    public DateRange(long from, long to) {
//        this.from = new Timestamp(from);
//        this.to = new Timestamp(to);
//    }
//
//    public Timestamp getFrom() {
//        return from;
//    }
//
//    public Timestamp getTo() {
//        return to;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("{ \"from\": \"%s\", \"to\": \"%s\" }", TimestampUtils.format(from), TimestampUtils.format(to));
//    }

}
