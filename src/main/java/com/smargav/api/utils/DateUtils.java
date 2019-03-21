package com.smargav.api.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    public final static String DATE_FORMAT = "dd-MM-yyyy";

    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
            .forPattern(DATE_FORMAT);

    public final static DateTimeFormatter REVERSE_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
            .forPattern("dd-MM-yyyy HH:mm");

    public static String getFormattedDate(long date) {
        if (date == -1) {
            return "";
        }

        LocalDate dt = new LocalDate(date);

        return dt.toString(DATE_FORMATTER);
    }

    public static String getFormattedDateReverse(long date) {
        if (date == -1) {
            return "";
        }

        LocalDate dt = new LocalDate(date);

        return dt.toString(REVERSE_DATE_FORMATTER);
    }

    public static String getFormattedDateTime(long date) {
        if (date == -1) {
            return "";
        }
        DateTime dt = new DateTime(date);

        return dt.toString(DATE_TIME_FORMATTER);
    }

}
