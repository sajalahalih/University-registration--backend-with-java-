package com.sbs_pro;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class MinTest {

    /**
     *
     */

    private Student student;
    private Faculty faculty;
    Registration ra = new Registration();
    MeetingSession session;
    Semester semester;
    ArrayList<MeetingSession> arrSession = new ArrayList<>();
    ArrayList<Course> arrPre = new ArrayList<>();
    ArrayList<Semester> arrSemester = new ArrayList<>();
    Course course;
    Course course1;
    Staff staff;
    private Registration registration;

    @Before
    public void setUp() {
        staff = new Staff("ST001", "John Doe", "johndoe@example.com");
        // Assuming Faculty's second constructor parameter is a list of courses, we pass
        // an empty list.

        ra.createFaculty("F001", "FacultyName", "faculty@example.com");
        Faculty faculty = ra.faculties.get(0);

        ra.createStudent("S001", "John Doe", "s", "john@example.com", faculty);

        // Retrieve the created student from the students list
        student = ra.students.get(0);

        session = new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(1));
        arrSession.add(session);

        semester = new Semester("Spring2023", LocalDate.parse("2020-02-20"), LocalDate.parse("2020-06-20"));
        arrSemester.add(semester);

        ra.createCourse("Math", 4, faculty, arrSession, new ArrayList<>(), arrSemester);
        course1 = ra.courses.get(0);
        arrPre.add(course1);

        ra.createCourse("Calculus", 4, faculty, arrSession, arrPre, arrSemester);

        course = ra.courses.get(1);

        registration = new Registration();

        ArrayList<Semester> arrsem = new ArrayList<>();
        Semester sem1 = new Semester("Spring2023",
                LocalDate.parse("2023-01-01"), LocalDate.parse("2023-05-01"));
        arrsem.add(sem1);
        registration.semesters.add(sem1);

        // Create sample data for testing
        Faculty faculty2 = new Faculty("F001", "FacultyName", "faculty@example.com");
        registration.createFaculty("F001", "FacultyName", "faculty@example.com");

        // Student student3 = new Student("S001", "John Doe", "Major",
        // "john@example.com", faculty2);
        // Course course3 = new Course("Calculus", 4, faculty2,
        // new ArrayList<>(Arrays.asList(
        // new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0),
        // Duration.ofHours(1)),
        // new MeetingSession(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0),
        // Duration.ofHours(1))
        // )),
        // new ArrayList<>(),arrsem);

        registration.createStudent("S001", "John Doe", "Major", "john@example.com", faculty2);
        Student student3 = registration.students.get(0);

        registration.createCourse("Calculus", 4, faculty2,
                new ArrayList<>(Arrays.asList(
                        new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(1)),
                        new MeetingSession(DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), Duration.ofHours(1)))),
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList(new Semester("Spring2023",
                        LocalDate.parse("2023-01-01"), LocalDate.parse("2023-05-01")))));
        Course course3 = registration.courses.get(0);
        registration.assignStudentToCourse(student3, course3);
        registration.enterGrade(student3, course3, "B+");

    }

    @Test
    public void rr() {
        ra.assignStudentToCourse(student, course1);
        boolean d = ra.assignStudentToCourse(student, course);

        // Assign the course to the student

        assertEquals(false, d);

    }

    @Test
    public void testcreateCourse() {
        ra.createCourse("Srwe141", 3, faculty, arrSession, arrPre, arrSemester);
        assertEquals(ra.courses.size(), 3);

    }

    @Test
    public void testcreateStudent() {
        ra.createStudent("s002", "sara Doe", "s", "john@example.com", faculty);
        assertEquals(ra.students.get(1).getName(), "sara Doe");

    }

    @Test
    public void testcreateFaculity() {
        ra.createFaculty("F001", "FacultyName", "faculty@example.com");
        assertEquals(ra.faculties.get(1).getFacultyID(), "F001");

    }

    @Test
    public void testenterGrade() {
        ra.assignStudentToCourse(student, course);
        ra.enterGrade(student, course, "A");
        assertEquals(student.getGrades().get(course), new Double(4));

    }

    @Test
    public void yy() {

        assertEquals(course.getPrerequisites().size(), 1);

    }

    @Test
    public void testassignStudentToCourse() {

        // assertEquals( ra.assignStudentToCourse(student, course),false);
        ra.assignStudentToCourse(student, course);
        System.out.println(student.getName() + course.getMeetingTimes());

        boolean result = ra.assignStudentToCourse(student, course);
        System.out.println(student.getName() + course.getMeetingTimes());

        assertEquals(false, result);

    }

    @Test
    public void testassignStaffToCourse() {

        // assertEquals( ra.assignStudentToCourse(student, course),false);

        ra.assignCourseToStaff(staff, course);

        boolean result = ra.assignCourseToStaff(staff, course);

        assertEquals(true, result);

    }

    @Test
    public void testGenerateAcademicsReport2() {
        // Create Registration instance and necessary entities
        Registration registration = new Registration();
        Faculty faculty = new Faculty("F001", "FacultyName", "faculty@example.com");
        Student student = new Student("S001", "John Doe", "s", "john@example.com", faculty);
        MeetingSession session = new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(1));
        Semester semester = new Semester("Spring2023", LocalDate.parse("2020-02-20"), LocalDate.parse("2020-06-20"));
        ArrayList<MeetingSession> arrSession = new ArrayList<>();
        ArrayList<Semester> arrSemester = new ArrayList<>();
        arrSession.add(session);
        arrSemester.add(semester);
        Course course = new Course("Calculus", 4, faculty, arrSession, new ArrayList<>(), arrSemester);

        // Assign the course to the student
        registration.assignStudentToCourse(student, course);

        // Enter a grade for the course
        registration.enterGrade(student, course, "B+");

        // Generate academics report
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        registration.generateAcademicsReport(student, semester);

        // Verify the output
        String expectedOutput = "Academic Report for John Doe in Spring2023:\n" +
                "GPA for Spring2023: 3.5\n" +
                "Academic Standing: Dean`s List\n" +
                "Courses and Grades:\n" +
                "Calculus: B+\n";
        assertEquals(expectedOutput.toString(), outContent.toString());
    }

}
