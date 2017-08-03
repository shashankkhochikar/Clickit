package com.clickitproduct.commonutil;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateFormatter {

    public static String formatDateCalendarToString(String pattern, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(calendar.getTime());
    }

    public static Calendar formatDateStringToCalendar(String pattern, String calendarStrDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        java.util.Date temporaryDate = null;
        Calendar outputCalendarDateObject;

        try {
            if(calendarStrDate!=null)
            temporaryDate = sdf.parse(calendarStrDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

            outputCalendarDateObject = Calendar.getInstance();
           if(temporaryDate!=null)
            outputCalendarDateObject.setTime(temporaryDate);

        return outputCalendarDateObject;
    }
}
