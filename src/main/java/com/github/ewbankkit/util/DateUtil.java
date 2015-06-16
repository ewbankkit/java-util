/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static com.github.ewbankkit.util.CalendarUtil.calendarToDate;
import static com.github.ewbankkit.util.CalendarUtil.dateToCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:40 DateUtil.java NSI";

    private static final DateFormat DATE_FORMAT = new ThreadSafeDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    private static final DateFormat DATE_TIME_FORMAT = new ThreadSafeDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    private static final Locale DEFAULT_LOCALE = Locale.US;

    /**
     * Constructor.
     */
    private DateUtil() {}

    /**
     * Get a Date from a String formatted yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static java.util.Date dateTimeStringToDate(String string) throws ParseException {
        return DateUtil.stringToDate(string, DATE_TIME_FORMAT);
    }

    /**
     * Returns a Date formatted yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static String dateToDateTimeString(java.util.Date date) {
        return dateToString(date, DATE_TIME_FORMAT);
    }

    /**
     * Returns a Date formatted yyyy-MM-dd.
     */
    public static String dateToString(java.util.Date date) {
        return dateToString(date, DATE_FORMAT);
    }

    public static String dateToString(java.util.Date date, DateFormat format) {
        if ((date == null) || (format == null)) {
            return null;
        }
        return format.format(date);
    }

    /**
     * Return the nearest end-of-cycle date from today given an expiration date.
     */
    public static java.util.Date getNearestEndOfCycle(java.util.Date expirationDate) {
        return calendarToDate(CalendarUtil.getNearestEndOfCycle(dateToCalendar(expirationDate)));
    }

    /**
     * Return the nearest end-of-cycle date from the specified date given an expiration date.
     */
    public static java.util.Date getNearestEndOfCycle(java.util.Date from, java.util.Date expirationDate) {
        return calendarToDate(CalendarUtil.getNearestEndOfCycle(dateToCalendar(from), dateToCalendar(expirationDate)));
    }

    /**
     * Return the number of days between two dates (inclusive on both ends).
     */
    public static int getDaysBetween(java.util.Date d1, java.util.Date d2) {
        return CalendarUtil.getDaysBetween(dateToCalendar(d1), dateToCalendar(d2));
    }

    public static long getSeconds(java.util.Date date) {
        return (date == null) ? 0L : TimeUnit.MILLISECONDS.toSeconds(date.getTime());
    }

    /**
     * Calculates the week of the year the same way MySQLl does on mode '0'.
     *
     * For more info, refer to:
     * http://dev.mysql.com/doc/refman/5.1/en/date-and-time-functions.html#function_week
     *
     * @param date The date for which to calculate the week.
     * @return The week of the year.
     */
    public static int mysqlWeek(final java.util.Date date) {
        final int result;
        final Calendar firstSundayOfYear = firstSunday(firstDayOfYear(date).getTime());
        final Calendar lastSundayForDate = lastSunday(date);
        int weeks = (lastSundayForDate.get(Calendar.DAY_OF_YEAR) - firstSundayOfYear.get(Calendar.DAY_OF_YEAR)) / 7;

        if (date.before(firstSundayOfYear.getTime())) {
            result = 0;
        }
        else {
            result = weeks + 1;
        }

        return result;
    }

    public static java.util.Date shiftFromTimeZone(java.util.Date date, TimeZone zone) {
        return calendarToDate(CalendarUtil.shiftFromTimeZone(dateToCalendar(date), zone));
    }

    /**
     * Get a Date from a String formatted yyyy-MM-dd.
     */
    public static java.util.Date stringToDate(String string) throws ParseException {
        return stringToDate(string, DATE_FORMAT);
    }

    public static java.util.Date stringToDate(String string, DateFormat format) throws ParseException {
        if ((string == null) || (format == null)) {
            return null;
        }
        return format.parse(string);
    }

    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return toSqlTimestamp(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(long time) {
        return new java.sql.Timestamp(time);
    }

    private static Calendar firstDayOfYear(final java.util.Date date) {
        final Calendar c = Calendar.getInstance(DEFAULT_LOCALE);
        c.setTime(date);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c;
    }

    private static Calendar firstSunday(final java.util.Date date) {
        final Calendar calendar = Calendar.getInstance(DEFAULT_LOCALE);
        calendar.setTime(date);
        final int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        final int daysTillSunday = (weekday == Calendar.SUNDAY) ? 0 : Calendar.SATURDAY - weekday + 1;
        final Calendar firstSunday = calendar;
        firstSunday.add(Calendar.DAY_OF_YEAR, daysTillSunday);
        return firstSunday;
    }

    private static Calendar lastSunday(final java.util.Date date) {
        final Calendar c = Calendar.getInstance(DEFAULT_LOCALE);
        c.setTime(date);
        final int daysAfterSunday = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        c.add(Calendar.DAY_OF_YEAR, -1 * daysAfterSunday);
        return c;
    }
}
