package com.team25.neety;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Helpers {
    public static String floatToPriceString(float f) {
        return String.format(Constants.locale, "$%,.2f", f);
    }

    public static float priceStringToFloat(String s) {
        try {
            return NumberFormat.getInstance(Constants.locale).parse(s).floatValue();
        } catch (Exception e) {
            return (float)0.00;
        }
    }

    public static Date getDateFromString(String dateString) {
        // TODO: Perhaps add locale here?

        if (dateString == null) throw new NullPointerException("Empty dateString");

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.locale);

        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromDate(Date date) {
        // TODO: Perhaps add locale here?

        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Constants.locale);
        return df.format(date);
    }

    public static String getPrintableTags(List<String> tagList){

        String result = String.join(", ", tagList);

        return result;
    }

    public static List<String> convertStringToTags(String tagsString) {
        if (tagsString == null || tagsString.equals("No tags")) {
            return new ArrayList<>(); // Return an empty list for "No tags" or null
        }

        // Assuming the delimiter is ", "
        String[] tagsArray = tagsString.split(", ");
        return Arrays.asList(tagsArray);
    }

    // Private constructor because you should never instantiate this class
    private Helpers() {}
}
