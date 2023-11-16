package com.team25.neety;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {
    public static String floatToPriceString(float f) {
        return String.format("$%,.2f", f);
    }

    public static Date getDateFromString(String dateString) {
        // TODO: Perhaps add locale here?

        if (dateString == null) throw new NullPointerException("Empty dateString");

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);

        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromDate(Date date) {
        // TODO: Perhaps add locale here?

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
        return df.format(date);
    }

    // Private constructor because you should never instantiate this class
    private Helpers() {}
}
