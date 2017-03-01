package com.handy.portal.library.util;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilsTest {
    @Test
    public void shouldBeTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        assertEquals("Tomorrow", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBeYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        assertEquals("Yesterday", DateTimeUtils.dayDifferenceInWords(yesterday.getTime()));
    }

    @Test
    public void shouldBeToday() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 0);
        assertEquals("Today", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBe3DaysFromNow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 3);
        assertEquals("3 days from now", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBe3DaysAgo() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, -3);
        assertEquals("3 days ago", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void formatDateToNumberTimeUnit_shouldBe20M() {
        Calendar fewMinutesAgo = Calendar.getInstance();
        fewMinutesAgo.add(Calendar.MINUTE, -20);
        assertEquals("20m", DateTimeUtils.formatDateToNumberTimeUnit(fewMinutesAgo.getTime()));
    }

    @Test
    public void formatDateToNumberTimeUnit_shouldBe2H() {
        Calendar fewHoursAgo = Calendar.getInstance();
        fewHoursAgo.add(Calendar.HOUR, -2);
        assertEquals("2h", DateTimeUtils.formatDateToNumberTimeUnit(fewHoursAgo.getTime()));
    }

    @Test
    public void formatDateToNumberTimeUnit_shouldBe3D() {
        Calendar fewDaysAgo = Calendar.getInstance();
        fewDaysAgo.add(Calendar.DATE, -3);
        assertEquals("3d", DateTimeUtils.formatDateToNumberTimeUnit(fewDaysAgo.getTime()));
    }

    @Test
    public void formatDateToNumberTimeUnit_shouldBe4W() {
        Calendar fewWeeksAgo = Calendar.getInstance();
        fewWeeksAgo.add(Calendar.DATE, -4 * 7);
        assertEquals("4w", DateTimeUtils.formatDateToNumberTimeUnit(fewWeeksAgo.getTime()));
    }
}
