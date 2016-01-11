package com.handy.portal.util;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DateTimeUtilsTest
{
    @Test
    public void shouldBeTomorrow()
    {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        assertEquals("Tomorrow", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBeYesterday()
    {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, -1);
        assertEquals("Yesterday", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBeToday()
    {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 0);
        assertEquals("Today", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBe3DaysFromNow()
    {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 3);
        assertEquals("3 days from now", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }

    @Test
    public void shouldBe3DaysAgo()
    {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, -3);
        assertEquals("3 days ago", DateTimeUtils.dayDifferenceInWords(tomorrow.getTime()));
    }
}
