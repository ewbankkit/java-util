/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class CalendarAndDateUtilUnitTest {
    private final DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void zeroOutTimePartTest1() throws ParseException {
        Calendar calendar = CalendarUtil.zeroOutTimePart(CalendarUtil.dateTimeStringToCalendar("2010-10-21 22:08:01"));

        assertEquals("Zero out time part", "2010-10-21", CalendarUtil.calendarToString(calendar));
    }

    @Test
    public void zeroOutTimePartTest2() throws Exception {
        Calendar calendar = CalendarUtil.zeroOutTimePart(CalendarUtil.dateTimeStringToCalendar("2010-10-21 22:08:01"));

        assertEquals("Zero out time part", "2010-10-21 00:00:00", CalendarUtil.calendarToDateTimeString(calendar));
    }

    @Test
    public void zeroOutTimePartTest3() throws Exception {
        Calendar calendar = CalendarUtil.zeroOutTimePart(CalendarUtil.dateTimeStringToCalendar("2010-10-21 10:08:01"));

        assertEquals("Zero out time part", "2010-10-21", CalendarUtil.calendarToString(calendar));
    }

    @Test
    public void zeroOutTimePartTest4() throws ParseException {
        Calendar calendar = CalendarUtil.zeroOutTimePart(CalendarUtil.dateTimeStringToCalendar("2010-10-21 10:08:01"));

        assertEquals("Zero out time part", "2010-10-21 00:00:00", CalendarUtil.calendarToDateTimeString(calendar));
    }

    @Test
    public void isSameDayTest1() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-20");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-22");

        assertFalse("Is same day", CalendarUtil.isSameDay(startCalendar, endCalendar));
    }

    @Test
    public void isSameDayTest2() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-21");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-21");

        assertTrue("Is same day", CalendarUtil.isSameDay(startCalendar, endCalendar));
    }

    @Test
    public void isSameDayTest3() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-21");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2009-06-21");

        assertFalse("Is same day", CalendarUtil.isSameDay(startCalendar, endCalendar));
    }

    @Test
    public void calendarsBetweenTwoDaysTest1() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-20");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar[] calendarsBetween = CalendarUtil.getCalendarsBetweenTwoDays(startCalendar, endCalendar);

        assertEquals("Calendars between array length", 3, calendarsBetween.length);
        assertEquals("Calendars between [0]", "2010-06-20", CalendarUtil.calendarToString(calendarsBetween[0]));
        assertEquals("Calendars between [1]", "2010-06-21", CalendarUtil.calendarToString(calendarsBetween[1]));
        assertEquals("Calendars between [2]", "2010-06-22", CalendarUtil.calendarToString(calendarsBetween[2]));
    }

    @Test
    public void calendarsBetweenTwoDaysTest2() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-20");
        Calendar[] calendarsBetween = CalendarUtil.getCalendarsBetweenTwoDays(startCalendar, endCalendar);

        assertEquals("Calendars between array length", 0, calendarsBetween.length);
    }

    @Test
    public void calendarsBetweenTwoDaysTest3() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-21");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-21");
        Calendar[] calendarsBetween = CalendarUtil.getCalendarsBetweenTwoDays(startCalendar, endCalendar);

        assertEquals("Calendars between array length", 1, calendarsBetween.length);
        assertEquals("Calendars between [0]", "2010-06-21", CalendarUtil.calendarToString(calendarsBetween[0]));
    }

    @Test
    public void getDaysBetweenTest1() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-20");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        int daysBetween = CalendarUtil.getDaysBetween(startCalendar, endCalendar);

        assertEquals("Days between", 3, daysBetween);
        // AMPM-181
        // Ensure Calendar parameters aren't modified.
        assertEquals("2010-06-20", CalendarUtil.calendarToString(startCalendar));
        assertEquals("2010-06-22", CalendarUtil.calendarToString(endCalendar));
    }

    @Test
    public void getDaysBetweenTest2() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        int daysBetween = CalendarUtil.getDaysBetween(startCalendar, endCalendar);

        assertEquals("Days between", 1, daysBetween);
    }

    @Test
    public void getDaysBetweenTest3() throws ParseException {
        Calendar startCalendar = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar endCalendar = CalendarUtil.stringToCalendar("2010-06-20");
        int daysBetween = CalendarUtil.getDaysBetween(startCalendar, endCalendar);

        assertEquals("Days between", 3, daysBetween);
    }

    @Test
    public void getDaysBetweenTest4() throws ParseException {
        Date startDate = DateUtil.stringToDate("2010-06-20");
        Date endDate = DateUtil.stringToDate("2010-06-22");
        int daysBetween = DateUtil.getDaysBetween(startDate, endDate);

        assertEquals("Days between", 3, daysBetween);
    }

    @Test
    public void getDaysBetweenTest5() throws ParseException {
        Date startDate = DateUtil.stringToDate("2010-06-22");
        Date endDate = DateUtil.stringToDate("2010-06-22");
        int daysBetween = DateUtil.getDaysBetween(startDate, endDate);

        assertEquals("Days between", 1, daysBetween);
    }

    @Test
    public void getDaysBetweenTest6() throws ParseException {
        Date startDate = DateUtil.stringToDate("2010-06-22");
        Date endDate = DateUtil.stringToDate("2010-06-20");
        int daysBetween = DateUtil.getDaysBetween(startDate, endDate);

        assertEquals("Days between", 3, daysBetween);
    }

    @Test
    public void getDaysBetweenTest7() throws ParseException {
        Date startDate = DateUtil.stringToDate("2011-09-30");
        Date endDate = DateUtil.stringToDate("2011-10-30");
        int daysBetween = DateUtil.getDaysBetween(startDate, endDate);

        assertEquals("Days between", 31, daysBetween);
    }

    @Test
    public void getDaysBetweenTest8() throws ParseException {
        Date startDate = DateUtil.stringToDate("2011-08-30");
        Date endDate = DateUtil.stringToDate("2011-09-30");
        int daysBetween = DateUtil.getDaysBetween(startDate, endDate);

        assertEquals("Days between", 32, daysBetween);
    }

    @Test
    public void getNearestEndOfCycleTest1() throws ParseException {
        Calendar from = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar expirationDate = CalendarUtil.stringToCalendar("2010-08-21");
        Calendar nearestEndOfCycle = CalendarUtil.getNearestEndOfCycle(from, expirationDate);

        assertEquals("Nearest end of cycle", "2010-07-21", CalendarUtil.calendarToString(nearestEndOfCycle));
    }

    @Test
    public void getNearestEndOfCycleTest2() throws ParseException {
        Calendar from = CalendarUtil.stringToCalendar("2010-06-20");
        Calendar expirationDate = CalendarUtil.stringToCalendar("2010-08-21");
        Calendar nearestEndOfCycle = CalendarUtil.getNearestEndOfCycle(from, expirationDate);

        assertEquals("Nearest end of cycle", "2010-06-21", CalendarUtil.calendarToString(nearestEndOfCycle));
    }

    @Test
    public void getNearestEndOfCycleTest3() throws ParseException {
        Calendar from = CalendarUtil.stringToCalendar("2010-06-22");
        Calendar expirationDate = CalendarUtil.stringToCalendar("2010-07-21");
        Calendar nearestEndOfCycle = CalendarUtil.getNearestEndOfCycle(from, expirationDate);

        assertEquals("Nearest end of cycle", "2010-07-21", CalendarUtil.calendarToString(nearestEndOfCycle));
    }

    @Test
    public void getNearestEndOfCycleTest4() throws ParseException {
        Calendar from = CalendarUtil.stringToCalendar("2011-10-10");
        Calendar expirationDate = CalendarUtil.stringToCalendar("2011-10-30");
        Calendar nearestEndOfCycle = CalendarUtil.getNearestEndOfCycle(from, expirationDate);

        assertEquals("Nearest end of cycle", "2011-10-30", CalendarUtil.calendarToString(nearestEndOfCycle));
    }

    @Test
    public void getNearestEndOfCycleTest5() throws ParseException {
        Date from = DateUtil.stringToDate("2011-10-10");
        Date expirationDate = DateUtil.stringToDate("2011-10-30");
        Date nearestEndOfCycle = DateUtil.getNearestEndOfCycle(from, expirationDate);

        assertEquals("Nearest end of cycle", "2011-10-30", DateUtil.dateToString(nearestEndOfCycle));
    }

    @Test
    public void advanceToEndOfDayTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-06-22 11:43:23");
        Calendar endOfDay = CalendarUtil.advanceToEndOfDay(calendar);

        assertEquals("Advance to end of day", "2010-06-22 23:59:59", CalendarUtil.calendarToDateTimeString(endOfDay));
    }

    @Test
    public void getEndOfPreviousWeekTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43"); // A Sunday.
        Calendar endOfPreviousWeek = CalendarUtil.getEndOfPreviousWeek(calendar);

        assertEquals("Get end of previous week", "2010-10-17 00:00:00", CalendarUtil.calendarToDateTimeString(endOfPreviousWeek));
    }

    @Test
    public void getEndOfPreviousWeekTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-16 19:22:43"); // A Saturday.
        Calendar endOfPreviousWeek = CalendarUtil.getEndOfPreviousWeek(calendar);

        assertEquals("Get end of previous week", "2010-10-10 00:00:00", CalendarUtil.calendarToDateTimeString(endOfPreviousWeek));
    }

    @Test
    public void getStartOfPreviousWeekTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43"); // A Sunday.
        Calendar startOfPreviousWeek = CalendarUtil.getStartOfPreviousWeek(calendar);

        assertEquals("Get start of previous week", "2010-10-10 00:00:00", CalendarUtil.calendarToDateTimeString(startOfPreviousWeek));
    }

    @Test
    public void getStartOfPreviousWeekTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-16 19:22:43"); // A Saturday.
        Calendar startOfPreviousWeek = CalendarUtil.getStartOfPreviousWeek(calendar);

        assertEquals("Get start of previous week", "2010-10-03 00:00:00", CalendarUtil.calendarToDateTimeString(startOfPreviousWeek));
    }

    @Test
    public void getEndOfPreviousMonthTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43");
        Calendar endOfPreviousMonth = CalendarUtil.getEndOfPreviousMonth(calendar);

        assertEquals("Get end of previous month", "2010-10-01 00:00:00", CalendarUtil.calendarToDateTimeString(endOfPreviousMonth));
    }

    @Test
    public void getEndOfPreviousMonthTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 19:22:43");
        Calendar endOfPreviousMonth = CalendarUtil.getEndOfPreviousMonth(calendar);

        assertEquals("Get end of previous month", "2010-10-01 00:00:00", CalendarUtil.calendarToDateTimeString(endOfPreviousMonth));
    }

    @Test
    public void getStartOfPreviousMonthTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43");
        Calendar startOfPreviousMonth = CalendarUtil.getStartOfPreviousMonth(calendar);

        assertEquals("Get start of previous month", "2010-09-01 00:00:00", CalendarUtil.calendarToDateTimeString(startOfPreviousMonth));
    }

    @Test
    public void getStartOfPreviousMonthTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 19:22:43");
        Calendar startOfPreviousMonth = CalendarUtil.getStartOfPreviousMonth(calendar);

        assertEquals("Get start of previous month", "2010-09-01 00:00:00", CalendarUtil.calendarToDateTimeString(startOfPreviousMonth));
    }

    @Test
    public void getStartOfCurrentMonthTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43");
        Calendar startOfCurrentMonth = CalendarUtil.getStartOfCurrentMonth(calendar);

        assertEquals("Get start of current month", "2010-10-01 00:00:00", CalendarUtil.calendarToDateTimeString(startOfCurrentMonth));
    }

    @Test
    public void getStartOfCurrentMonthTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 19:22:43");
        Calendar startOfCurrentMonth = CalendarUtil.getStartOfCurrentMonth(calendar);

        assertEquals("Get start of current month", "2010-10-01 00:00:00", CalendarUtil.calendarToDateTimeString(startOfCurrentMonth));
    }

    @Test
    public void getStartOfCurrentMonthTest3() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 00:00:00");
        Calendar startOfCurrentMonth = CalendarUtil.getStartOfCurrentMonth(calendar);

        assertEquals("Get start of current month", "2010-10-01 00:00:00", CalendarUtil.calendarToDateTimeString(startOfCurrentMonth));
    }

    @Test
    public void getEndOfCurrentMonthTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-17 19:22:43");
        Calendar endOfCurrent = CalendarUtil.getEndOfCurrentMonth(calendar);

        assertEquals("Get end of current month", "2010-10-31 00:00:00", CalendarUtil.calendarToDateTimeString(endOfCurrent));
    }

    @Test
    public void getEndOfCurrentMonthTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 19:22:43");
        Calendar endOfCurrent = CalendarUtil.getEndOfCurrentMonth(calendar);

        assertEquals("Get end of current month", "2010-10-31 00:00:00", CalendarUtil.calendarToDateTimeString(endOfCurrent));
    }

    @Test
    public void getEndOfCurrentMonthTest3() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-10-01 00:00:00");
        Calendar endOfCurrent = CalendarUtil.getEndOfCurrentMonth(calendar);

        assertEquals("Get end of current month", "2010-10-31 00:00:00", CalendarUtil.calendarToDateTimeString(endOfCurrent));
    }

    @Test
    public void getEndOfCurrentMonthTest4() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2010-02-17 19:22:43");
        Calendar endOfCurrent = CalendarUtil.getEndOfCurrentMonth(calendar);

        assertEquals("Get end of current month", "2010-02-28 00:00:00", CalendarUtil.calendarToDateTimeString(endOfCurrent));
    }

    @Test
    public void convertToTimeZoneTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2012-03-08 20:41:00");
        Calendar converted = CalendarUtil.convertToTimeZone(calendar, TimeZone.getDefault());

        assertEquals("Convert to time zone", "2012-03-08 20:41:00", CalendarUtil.calendarToDateTimeString(converted));
    }

    @Test
    public void convertToTimeZoneTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2012-03-08 20:41:00");
        Calendar converted = CalendarUtil.convertToTimeZone(calendar, TimeZone.getTimeZone("America/Chicago"));

        assertEquals("Convert to time zone", "2012-03-08 20:41:00", CalendarUtil.calendarToDateTimeString(converted));
    }

    @Test
    public void shiftFromTimeZoneTest1() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2012-03-09 09:46:00");
        Calendar shifted = CalendarUtil.shiftFromTimeZone(calendar, TimeZone.getDefault());

        assertEquals("Shift from time zone", "2012-03-09 09:46:00", CalendarUtil.calendarToDateTimeString(shifted));
    }

    @Test
    public void shiftFromTimeZoneTest2() throws ParseException {
        Calendar calendar = CalendarUtil.dateTimeStringToCalendar("2012-03-09 09:46:00");
        Calendar shifted = CalendarUtil.shiftFromTimeZone(calendar, TimeZone.getTimeZone("America/Chicago"));

        assertEquals("Shift from time zone", "2012-03-09 08:46:00", CalendarUtil.calendarToDateTimeString(shifted));
    }

    @Test
    public void shiftFromTimeZoneTest3() throws ParseException {
        Date date = DateUtil.dateTimeStringToDate("2012-03-09 09:46:00");
        Date shifted = DateUtil.shiftFromTimeZone(date, TimeZone.getTimeZone("America/Los_Angeles"));

        assertEquals("Shift from time zone", "2012-03-09 06:46:00", DateUtil.dateToDateTimeString(shifted));
    }

    @Test
    public void testMySqlWeek_yearDoesntStartOnSunday_firstWeekBoundaries() throws ParseException {
        assertEquals(0, mysqlWeek("2010-01-01"));
        assertEquals(0, mysqlWeek("2010-01-02"));
        assertEquals(1, mysqlWeek("2010-01-03"));
    }

    @Test
    public void testMySqlWeek_yearDoesntStartOnSunday_midYearBoundaries() throws ParseException {
        assertEquals(21, mysqlWeek("2010-05-29"));
        assertEquals(22, mysqlWeek("2010-05-30"));
        assertEquals(22, mysqlWeek("2010-06-05"));
        assertEquals(23, mysqlWeek("2010-06-06"));
    }

    @Test
    public void testMySqlWeek_yearDoesntStartOnSunday_lastWeekBoundaries() throws ParseException {
        assertEquals(51, mysqlWeek("2010-12-25"));
        assertEquals(52, mysqlWeek("2010-12-26"));
        assertEquals(52, mysqlWeek("2010-12-31"));
    }

    @Test
    public void testMySqlWeek_yearStartsOnSunday_firstWeekBoundaries() throws ParseException {
        assertEquals(1, mysqlWeek("2006-01-01"));
        assertEquals(1, mysqlWeek("2006-01-07"));
        assertEquals(2, mysqlWeek("2006-01-08"));
    }

    @Test
    public void testMySqlWeek_yearStartsOnSunday_midYearBoundaries() throws ParseException {
        assertEquals(22, mysqlWeek("2006-06-03"));
        assertEquals(23, mysqlWeek("2006-06-04"));
        assertEquals(23, mysqlWeek("2006-06-10"));
        assertEquals(24, mysqlWeek("2006-06-11"));
    }

    @Test
    public void testMySqlWeek_yearStartsOnSunday_lastWeekBoundaries() throws ParseException {
        assertEquals(52, mysqlWeek("2006-12-30"));
        assertEquals(53, mysqlWeek("2006-12-31"));
    }

    private int mysqlWeek(final String dateString) throws ParseException {
        final Date date = dateParser.parse(dateString);
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        return DateUtil.mysqlWeek(dateParser.parse(dateString));
    }
}
