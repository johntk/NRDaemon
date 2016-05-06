package com.ibm.nrdaemon.model;

import com.ibm.perf.utils.MathUtils;
import com.ibm.perf.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

/** This class is used to format the joda Instants into strings for the New Relic requests
 *  I'm using Joda-Time Instant and New Relic seem to be able to handle the string representation
 *  of Instant in the request, this class may be redundant and may be removed */

/** Jav8 time is a better solution than this it's based on joda time, I have used it in other modules,
 * this should be refactored to use it  */
public final class TimestampUtils {
    /** TODO: Documentation */
    public static final long MILLIS_PER_SECOND = 1000;

    /** TODO: Documentation */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    /** TODO: Documentation */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    /** TODO: Documentation */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    /** TODO: Documentation */
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;

    private TimestampUtils() { }


    private static final DateTimeFormatter FORMAT_TIMESTAMP = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public static String format(Instant timestamp) {
        return FORMAT_TIMESTAMP.print(timestamp);
    }


    /**  Formats the string to 2015-12-17T16:22:40+00:00*/
    public static Instant parseTimestamp(String value) {
        return parseTimestamp(value, new Instant(0L));
    }

    public static Instant parseTimestamp(String value, Instant defaultValue) {
        String cleanValue = StringUtils.clean(value).toUpperCase();

        if (cleanValue.isEmpty()) {
            return defaultValue;
        }

        try {
            return new Instant(format(DateTime.parse(value).toInstant()));
        } catch (NumberFormatException e) {
            /* Fail silently */
        }

        return defaultValue;
    }

    /**  These 2 methods are from old code, not sure of purpose yet, mey remove*/
    public static Instant earliestPeriod(Timestamp timestamp, int granularity) {
        return new Instant(MathUtils.modulusFloor(timestamp.getTime(), MILLIS_PER_MINUTE * granularity));
    }

    public static Instant latestPeriod(Timestamp timestamp, int granularity) {
        return new Instant(MathUtils.modulusCeil(timestamp.getTime(), MILLIS_PER_MINUTE * granularity));
    }
}
