/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.DateUtil.dateTimeStringToDate;
import static com.netsol.adagent.util.DateUtil.dateToDateTimeString;
import static com.netsol.adagent.util.DateUtil.dateToString;
import static com.netsol.adagent.util.DateUtil.stringToDate;
import static com.netsol.adagent.util.beans.BaseData.lessThan;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

public final class CalendarUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:16 CalendarUtil.java NSI";

    /**
     * Constructor.
     */
    private CalendarUtil() {}

    /**
     * Return a calendar representing the end of the specified day.
     */
    public static Calendar advanceToEndOfDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.clear();
        endOfDay.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        endOfDay.add(Calendar.DATE, 1);
        endOfDay.add(Calendar.SECOND, -1);

        return endOfDay;
    }

    public static java.util.Date calendarToDate(Calendar calendar) {
        return (calendar == null) ? null : calendar.getTime();
    }

    /**
     * Returns a Calendar formatted yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static String calendarToDateTimeString(Calendar calendar) {
        return dateToDateTimeString(calendarToDate(calendar));
    }

    /**
     * Convert a Calendar to a java.sql.Date.
     */
    public static java.sql.Date calendarToSqlDate(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Returns a Calendar formatted yyyy-MM-dd.
     */
    public static String calendarToString(Calendar calendar) {
        return dateToString(calendarToDate(calendar));
    }

    public static String calendarToString(Calendar calendar, DateFormat format) {
        return dateToString(calendarToDate(calendar), format);
    }

    /**
     * Convert a Calendar to the specified time zone.
     */
    public static Calendar convertToTimeZone(Calendar calendar, TimeZone toTimeZone) {
        if ((calendar == null) || (toTimeZone == null)) {
            return null;
        }

        Calendar convertedCalendar = Calendar.getInstance(toTimeZone);
        convertedCalendar.setTime(calendar.getTime());
        return convertedCalendar;
    }

    /**
     * Convert a Calendar to the UTC time zone.
     */
    public static Calendar convertToUTC(Calendar calendar) {
        return convertToTimeZone(calendar, TimeZone.getTimeZone("UTC"));
    }

    /**
     * Get a Calendar from a String formatted yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static Calendar dateTimeStringToCalendar(String string) throws ParseException {
        return dateToCalendar(dateTimeStringToDate(string));
    }

    /**
     * Convert a java.util.Date to a Calendar.
     */
    public static Calendar dateToCalendar(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * Returns an array of Calendars for each day between the given Calendars (inclusive on both ends). Returns null if either parameter
     * is null. The start calendar will be returned if endCal is before.
     *
     * @param cal a Calendar object (for a day in the past).
     */
    public static Calendar[] getCalendarsBetweenTwoDays(Calendar startCal, Calendar endCal)  {
        if (startCal == null || endCal == null) {
            return null;
        }

        Collection<Calendar> list = new ArrayList<Calendar>();
        if (startCal.before(endCal) || isSameDay(startCal, endCal)) {
            Calendar cal = (Calendar) startCal.clone();
            list.add((Calendar)cal.clone());
            while (cal.before(endCal) && !isSameDay(endCal, cal)) {
                cal.add(Calendar.DATE, 1);
                list.add((Calendar)cal.clone());
            }
        }
        return list.toArray(new Calendar[list.size()]);
    }

    /**
     * Returns an array of Calendars for each day from the given Calendar until today (inclusive). Returns
     * an empty array if the given Calendar is after today. Returns null if the parameter is null.
     *
     * @param cal a Calendar object (for a day in the past).
     */
    public static Calendar[] getCalendarsUpToToday(Calendar cal)  {
        return getCalendarsBetweenTwoDays(cal, Calendar.getInstance());
    }

    /**
     * Return the number of days between two calendars (inclusive on both ends).
     */
    public static int getDaysBetween(Calendar c1, Calendar c2) {
        Calendar earlier = null;
        Calendar later = null;

        if (lessThan(c1, c2)) {
            earlier = c1;
            later = c2;
        }
        else {
            earlier = c2;
            later = c1;
        }
        // AMPM-181
        // Ensure Calendar parameters aren't modified.
        earlier = (Calendar)earlier.clone();
        later = (Calendar)later.clone();

        int daysBetween = 1;
        int x = 0;
        int y = 0;
        while ((x = earlier.get(Calendar.YEAR)) != (y = later.get(Calendar.YEAR))) {
            int days = 365 * (y - x);
            daysBetween += days;
            earlier.add(Calendar.DAY_OF_YEAR, days);
        }
        while ((x = earlier.get(Calendar.DAY_OF_YEAR)) != (y = later.get(Calendar.DAY_OF_YEAR))) {
            int days = (y - x);
            daysBetween += days;
            earlier.add(Calendar.DAY_OF_YEAR, days);
        }

        return daysBetween;
    }

    /**
     * Return a calendar representing the end of the current month (last day of month at midnight) for the specified date.
     */
    public static Calendar getEndOfCurrentMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar endOfCurrentMonth = zeroOutTimePart(calendar);
        endOfCurrentMonth.set(Calendar.DAY_OF_MONTH, endOfCurrentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        return endOfCurrentMonth;
    }

    /**
     * Return a calendar representing the end of the previous month (first day of month at midnight) for the specified date.
     */
    public static Calendar getEndOfPreviousMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar endOfPreviousMonth = zeroOutTimePart(calendar);
        endOfPreviousMonth.set(Calendar.DAY_OF_MONTH, 1);

        return endOfPreviousMonth;
    }

    /**
     * Return a calendar representing the end of the previous week (Sunday morning at midnight) for the specified date.
     */
    public static Calendar getEndOfPreviousWeek(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar endOfPreviousWeek = zeroOutTimePart(calendar);
        endOfPreviousWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return endOfPreviousWeek;
    }

    /**
     * Return the nearest end-of-cycle date from today given an expiration date.
     */
    public static Calendar getNearestEndOfCycle(Calendar expirationDate) {
        return getNearestEndOfCycle(Calendar.getInstance(), expirationDate);
    }

    /**
     * Return the nearest end-of-cycle date from the specified date given an expiration date.
     */
    public static Calendar getNearestEndOfCycle(Calendar from, Calendar expirationDate) {
        if ((from == null) || (expirationDate == null)) {
            return null;
        }

        Calendar oneMonthFrom = (Calendar)from.clone();
        oneMonthFrom.add(Calendar.MONTH, 1);
        Calendar calendar = (Calendar)expirationDate.clone();
        while (calendar.compareTo(oneMonthFrom) > 0) {
            calendar.add(Calendar.MONTH, -1);
        }

        return calendar;
    }

    /**
     * Return a calendar representing the start of the current month for the specified date.
     */
    public static Calendar getStartOfCurrentMonth(Calendar calendar) {
        return getEndOfPreviousMonth(calendar);
    }

    /**
     * Return a calendar representing the start of the previous month for the specified date.
     * The start of the previous month is exactly 1 month prior to the end of the previous month.
     */
    public static Calendar getStartOfPreviousMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar startOfPreviousMonth = getEndOfPreviousMonth(calendar);
        startOfPreviousMonth.add(Calendar.MONTH, -1);

        return startOfPreviousMonth;
    }

    /**
     * Return a calendar representing the start of the previous week for the specified date.
     * The start of the previous week is exactly 7 days prior to the end of the previous week.
     */
    public static Calendar getStartOfPreviousWeek(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar startOfPreviousWeek = getEndOfPreviousWeek(calendar);
        startOfPreviousWeek.add(Calendar.DATE, -7);

        return startOfPreviousWeek;
    }

    /**
     * Returns true if the two Calendars represent the same day (ignores time information).
     */
    public static boolean isSameDay(Calendar c1, Calendar c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
            c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static Calendar shiftFromTimeZone(Calendar calendar, TimeZone fromTimeZone) {
        if ((calendar == null) || (fromTimeZone == null)) {
            return null;
        }

        long date = calendar.getTimeInMillis();
        TimeZone timeZone = calendar.getTimeZone();
        int millisToSubtract = timeZone.getOffset(date) - fromTimeZone.getOffset(date);
        Calendar shiftedCalendar = Calendar.getInstance(timeZone);
        shiftedCalendar.setTimeInMillis(date - millisToSubtract);
        return shiftedCalendar;
    }

    /**
     * Get a Calendar from a String formatted yyyy-MM-dd.
     */
    public static Calendar stringToCalendar(String string) throws ParseException {
        return dateToCalendar(stringToDate(string));
    }

    public static Calendar stringToCalendar(String string, DateFormat format) throws ParseException {
        return dateToCalendar(stringToDate(string, format));
    }

    /**
     * Convert a time in milliseconds to a Calendar.
     */
    public static Calendar timeToCalendar(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    /**
     * Return a Calendar with the time part zeroed out.
     */
    public static Calendar zeroOutTimePart(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        Calendar calendarwithTimePartZeroedOut = (Calendar)calendar.clone();
        calendarwithTimePartZeroedOut.set(Calendar.HOUR_OF_DAY, 0);
        calendarwithTimePartZeroedOut.set(Calendar.MINUTE, 0);
        calendarwithTimePartZeroedOut.set(Calendar.SECOND, 0);
        calendarwithTimePartZeroedOut.set(Calendar.MILLISECOND, 0);

        return calendarwithTimePartZeroedOut;
    }
}
