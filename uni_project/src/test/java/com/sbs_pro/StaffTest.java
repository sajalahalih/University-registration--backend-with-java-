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

public class StaffTest {

    private Staff staff;
    private ArrayList<Staff> staffList;
    private Course course;
    private Student student;
    private Registration ra;
    private MeetingSession session;
    private Semester semester;

    @Before
    public void setUp() {
        staffList = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("stuffs.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the header line
                }
                String[] data = line.split(",\\s*");
                if (data.length >= 6) {
                    String staffID = data[0].trim();
                    String name = data[1].trim();
                    String contactDetails = data[2].trim();
                    String role = data[3].trim();
                    Staff staff = new Staff(staffID, name, contactDetails);
                    staff.setRole(role);
                    staffList.add(staff);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!staffList.isEmpty()) {
            staff = staffList.get(0);
            System.out.println("Testing with Staff: " + staff.getName() + ", " +
                    staff.getContactDetails());
        } else {
            staff = new Staff("ST001", "John Doe", "johndoe@example.com");
            staff.setRole("staff");
        }
        Faculty faculty = new Faculty("F001", "FacultyName", "faculty@example.com");
        ra = new Registration();
        session = new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0),
                Duration.ofHours(1));
        semester = new Semester("Spring2023", LocalDate.parse("2020-02-20"),
                LocalDate.parse("2020-06-20"));
        ArrayList<MeetingSession> arrSession = new ArrayList<>();
        arrSession.add(session);
        ArrayList<Semester> arrSemester = new ArrayList<>();
        arrSemester.add(semester);
        ArrayList<Course> arrCourse = new ArrayList<>();
        course = new Course("Calculus", 4, faculty, arrSession, arrCourse,
                arrSemester);
        student = new Student("S001", "John Doe", "Software Engineering",
                "john@example.com", faculty);
        ra.students.add(student);
    }

    @Test
    public void trry() {
        if (!ra.students.isEmpty()) {
            ra.assignStudentToCourse(ra.students.get(0), course);
            Assert.assertEquals(course, ra.students.get(0).getCourses().get(0));
        } else {
            Assert.fail("Students list is empty.");
        }
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("John Doe", staff.getName());
    }

    @Test
    public void testSetName() {
        staff.setName("Jane Doe");
        Assert.assertEquals("Jane Doe", staff.getName());
    }

    @Test
    public void testGetContactDetails() {
        Assert.assertEquals("johndoe@example.com", staff.getContactDetails());
    }

    @Test
    public void testSetContactDetails() {
        staff.setContactDetails("janedoe@example.com");
        Assert.assertEquals("janedoe@example.com", staff.getContactDetails());
    }

    @Test
    public void testGetRole() {
        Assert.assertEquals("staff", staff.getRole());
    }

    @Test
    public void testSetRole() {
        staff.setRole("administrator");
        Assert.assertEquals("administrator", staff.getRole());
    }

    @Test
    public void testGetStaffCourses() {
        ArrayList<Course> arr = new ArrayList<>();
        arr.add(course);
        staff.setStaffCourses(arr);
        Assert.assertTrue(staff.getStaffCourses().isPresent());
        Assert.assertFalse(staff.getStaffCourses().get().isEmpty());
        Assert.assertEquals(course, staff.getStaffCourses().get().get(0));
    }

    // @Test
    // public void testGetStudents() {
    // staff.setStudents(Arrays.asList(student));
    // Assert.assertTrue(staff.getStudents().isPresent());
    // Assert.assertFalse(staff.getStudents().get().isEmpty());
    // Assert.assertEquals(student, staff.getStudents().get().get(0));

    // }

    @Test
    public void testEnterGrade() {
        // Setting up a course and a student to test the enterGrade method
        course.setNumberOfCredits(4);
        // student.addCourse(course);
        staff.enterGrade(ra.students.get(0), course, "B+");

        Assert.assertEquals(3.5, ra.students.get(0).getGrades().get(course), 0.0);
    }

}