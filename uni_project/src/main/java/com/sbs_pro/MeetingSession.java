package com.sbs_pro;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

public class MeetingSession {

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private Duration duration;

    public MeetingSession(DayOfWeek dayOfWeek, LocalTime startTime, Duration duration) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Getters and setters with Optional
    public Optional<DayOfWeek> getDayOfWeek() {
        return Optional.ofNullable(dayOfWeek);
    }

    public synchronized void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Optional<LocalTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public synchronized void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public synchronized void setDuration(Duration duration) {
        this.duration = duration;
    }
}