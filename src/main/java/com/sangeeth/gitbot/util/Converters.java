package com.sangeeth.gitbot.util;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

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
}
