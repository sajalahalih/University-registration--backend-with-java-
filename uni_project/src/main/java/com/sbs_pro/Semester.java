package com.sbs_pro;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Semester {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    static ArrayList<Course> courses;

    public Semester() {
    }

    public Semester(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        courses = new ArrayList<>();
    }

    public long getSessionPeriod(LocalDate startDate, LocalDate endDate) {
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        return months;
    }

    // Optional used for name which might be unset
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    // Optional used for startDate
    public Optional<LocalDate> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public synchronized void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Optional used for endDate
    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public synchronized void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public static void addCourse(Course course) {
        Semester.courses.add(course);
    }

    // adjust lists
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Semester semester = (Semester) o;
        return Objects.equals(name, semester.name) &&
                Objects.equals(startDate, semester.startDate) &&
                Objects.equals(endDate, semester.endDate);
    }

  
    @Override
    public String toString() {
        return "Semester{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
