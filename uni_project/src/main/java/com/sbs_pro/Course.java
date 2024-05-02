package com.sbs_pro;

import java.util.ArrayList;
import java.util.Optional;

public class Course {
    private String name;
    private int numberOfCredits;
    private Faculty faculty;
    private ArrayList<MeetingSession> meetingTimes;
    private ArrayList<Course> prerequisites;
    private double Grade = 0;
    private ArrayList<Semester> courseOfferSemester;

    
    public Course() {
    }

    public Course(String name, int numberOfCredits, Faculty faculty, ArrayList<MeetingSession> meetingSessions,
            ArrayList<Course> prerequisites2, ArrayList<Semester> courseOfferSemester) {
        this.name = name;
        this.numberOfCredits = numberOfCredits;
        this.faculty = faculty;
        this.meetingTimes = meetingSessions;
        this.prerequisites = prerequisites2;
        this.courseOfferSemester = courseOfferSemester;
        for (Semester sem : courseOfferSemester) {

            sem.addCourse(this);
        }
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public int getNumberOfCredits() {
        return numberOfCredits;
    }

    public synchronized void setNumberOfCredits(int numberOfCredits) {
        this.numberOfCredits = numberOfCredits;
    }

    // Modified to return Optional<Faculty>
    public Optional<Faculty> getFaculty() {
        return Optional.ofNullable(faculty);
    }

    public synchronized void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public ArrayList<MeetingSession> getMeetingTimes() {
        return meetingTimes;
    }

    public synchronized void setMeetingTimes(ArrayList<MeetingSession> meetingTimes) {
        this.meetingTimes = meetingTimes;
    }

    // Modified to return Optional<ArrayList<Course>>
    public ArrayList<Course> getPrerequisites() {
        if (this.prerequisites == null) {
            return new ArrayList<>(); // Return an empty list if prerequisites are null
        }
        return this.prerequisites;
    }

    public synchronized void setPrerequisites(ArrayList<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public double getGrade() {
        return Grade;
    }

    public synchronized void setGrade(double grade) {
        Grade = grade;
    }

    public ArrayList<Semester> getCourseOfferSemester() {
        return courseOfferSemester;
    }

    public synchronized void setCourseOfferSemester(ArrayList<Semester> courseOfferSemester) {
        this.courseOfferSemester = courseOfferSemester;
    }
}