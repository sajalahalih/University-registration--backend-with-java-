package com.sbs_pro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StudentTest {

    private Student student;
    private Faculty faculty;
    Registration ra = new Registration();
    MeetingSession session;
    Semester semester;
    private ArrayList<Student> students;

    @Before
    public void setUp() {
        students = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("students.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true; // To skip the header line
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",\\s*");
                if (data.length >= 8) {
                    String studentID = data[0].trim();
                    String name = data[1].trim();
                    String contactDetails = data[2].trim();
                    String facultyName = data[3].trim();
                    Faculty faculty = new Faculty(facultyName, facultyName, "");
                    Student newStudent = new Student(studentID, name, "", contactDetails, faculty);
                    students.add(newStudent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!students.isEmpty()) {
            student = students.get(0);
        } else {
            Faculty defaultFaculty = new Faculty("Default", "Default Faculty", "");
            student = new Student("DefaultID", "Default Student", "", "default@example.com", defaultFaculty);
        }
    }

    @Test
    public void testSetName() {
        student.setName("Jane Doe");
        Assert.assertEquals("Jane Doe", student.getName());
    }

    @Test
    public void testSetContactDetails() {
        student.setContactDetails("jane@example.com");
        Assert.assertEquals("jane@example.com", student.getContactDetails());
    }

    @Test
    public void testSetCUM() {
        session = new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(1));
        semester = new Semester("Spring2023", LocalDate.parse("2020-02-20"), LocalDate.parse("2020-06-20"));
        ArrayList<MeetingSession> arrSession = new ArrayList<>();
        arrSession.add(session);

        ArrayList<Semester> arrSemester = new ArrayList<>();
        arrSemester.add(semester);

        // Ensure that courseOfferSemester is not null
        Course course = new Course("Calculus", 4, faculty, arrSession, new ArrayList<>(), arrSemester);
        Course course1 = new Course("Calculus", 4, faculty, arrSession, new ArrayList<>(), arrSemester);

        ra.assignStudentToCourse(student, course);
        ra.assignStudentToCourse(student, course1);

        student.setGrade(course, "A");
        student.setGrade(course1, "B+");
        student.recalculateCUM();
        Assert.assertEquals(3.75, student.getCUM(), 0.001);
    }

}