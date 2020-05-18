package com.sangeeth.gitbot.util;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author dtsangeeth
 * @created 16 / 04 / 2020
 * @project GitComponentManager
 */
public class Converters {

    public static long convertDateTimeToEpoch (String datetime){

        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = dtf.parseDateTime(datetime);

        return dateTime.getMillis()/1000;
    }

    public static String convertEpochToDateTime (long time){

        long tt = time * 1000;

        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dd = new Date(tt);
        String dtimes = df.format(dd);


        return dtimes;
    }
}
