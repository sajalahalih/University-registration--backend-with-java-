package com.sbs_pro;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeetingSessionTest {

    private MeetingSession session;

    @Before
    public void setUp() {
        session = new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(9, 0), Duration.ofHours(2));
    }

    @Test
    public void testGetDayOfWeek() {
        Assert.assertTrue(session.getDayOfWeek().isPresent());
        Assert.assertEquals(DayOfWeek.MONDAY, session.getDayOfWeek().get());
    }

    @Test
    public void testSetDayOfWeek() {
        session.setDayOfWeek(DayOfWeek.WEDNESDAY);
        Assert.assertTrue(session.getDayOfWeek().isPresent());
        Assert.assertEquals(DayOfWeek.WEDNESDAY, session.getDayOfWeek().get());
    }

    @Test
    public void testGetStartTime() {
        Assert.assertTrue(session.getStartTime().isPresent());
        Assert.assertEquals(LocalTime.of(9, 0), session.getStartTime().get());
    }

    @Test
    public void testSetStartTime() {
        LocalTime newStartTime = LocalTime.of(10, 30);
        session.setStartTime(newStartTime);
        Assert.assertTrue(session.getStartTime().isPresent());
        Assert.assertEquals(newStartTime, session.getStartTime().get());
    }

    @Test
    public void testGetDuration() {
        Assert.assertTrue(session.getDuration().isPresent());
        Assert.assertEquals(Duration.ofHours(2), session.getDuration().get());
    }

    @Test
    public void testSetDuration() {
        Duration newDuration = Duration.ofMinutes(90);
        session.setDuration(newDuration);
        Assert.assertTrue(session.getDuration().isPresent());
        Assert.assertEquals(newDuration, session.getDuration().get());
    }

}