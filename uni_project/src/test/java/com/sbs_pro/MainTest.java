package com.sbs_pro;

import static org.junit.Assert.*;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MainTest {
    private ArrayList<Faculty> facultyList;
    private ArrayList<Semester> semesterList;
    private ArrayList<Course> courseList;
    private ArrayList<Staff> staffList;
    private ArrayList<Student> studentList;

    private Registration raafat = new Registration();

    @Before
    public void setUp() {
        readFaculty();
        readSemester();
        readStaff();
        readStudent();
        courseList = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("courses.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                String name = parts[0].trim();
                int credits = Integer.parseInt(parts[1].trim());
                String facultyName = parts[2].trim();

                ArrayList<MeetingSession> arrSession = new ArrayList<>();

                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(line);
                String sessionString = "";
                if (matcher.find()) {
                    sessionString = matcher.group(0);

                    String trimmedSessionString = sessionString.substring(1, sessionString.length() - 1);
                    String[] sessions = trimmedSessionString.split(",\\s*");

                    for (String sessionStr : sessions) {
                        String[] sessionDetails = sessionStr.split("\\s+");
                        DayOfWeek day = DayOfWeek.valueOf(sessionDetails[0].toUpperCase());
                        String[] times = sessionDetails[1].split("-");
                        LocalTime startTime = LocalTime.parse(times[0]);
                        LocalTime endTime = LocalTime.parse(times[1]);

                        Duration duration = Duration.between(startTime, endTime);
                        arrSession.add(new MeetingSession(day, startTime, duration));
                    }
                    // System.out.println("All Sessions for Course: " + arrSession);
                } else {
                    System.err.println("Invalid or incomplete session format: " + sessionString);
                    continue;
                }
                int facultyIndex = 0; // our file all the faculty already included in the faculty file
                for (int i = 0; i < facultyList.size(); i++) {
                    if (facultyList.get(i).getName().equals(facultyName)) {
                        facultyIndex = i;
                    }
                }
                String[] semesters = parts[7].trim().replaceAll("[\\[\\]]", "").split(", ");
                ArrayList<Semester> arrSemester = new ArrayList<>();
                for (String sem : semesters) {
                    for (int i = 0; i < semesterList.size(); i++) {
                        if (semesterList.get(i).getName().get().equals(sem)) {
                            arrSemester.add(semesterList.get(i));
                        }
                    }
                }
                String[] prerequisites = parts[5].trim().replaceAll("[\\[\\]]", "").split(", ");
                ArrayList<Course> arrPrerequisites = new ArrayList<>();
                for (String pre : prerequisites) {
                    for (int i = 0; i < courseList.size(); i++) {
                        if (courseList.get(i).getName().equals(pre)) {
                            arrPrerequisites.add(courseList.get(i));
                        }
                    }
                }

                Course course = new Course(name, credits, facultyList.get(facultyIndex), arrSession, arrPrerequisites,
                        arrSemester);
                courseList.add(course);
                raafat.courses.add(course);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("courses.txt file not found in resources.");
        }
    }

    public void readFaculty() {
        facultyList = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("faculty.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true; // To skip the header line
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(", ");
                if (data.length >= 4) {
                    String facultyID = data[0].trim();
                    String name = data[1].trim();
                    String contactDetails = data[2].trim();
                    Faculty faculty = new Faculty(facultyID, name, contactDetails);
                    facultyList.add(faculty);
                    raafat.faculties.add(faculty);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readSemester() {
        semesterList = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("semesters.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                String semesterName = parts[0].trim();

                try {
                    LocalDate startDate = LocalDate.parse(parts[1].trim());
                    LocalDate endDate = LocalDate.parse(parts[2].trim());

                    Semester semester = new Semester(semesterName, startDate, endDate);
                    semesterList.add(semester);
                    raafat.semesters.add(semester);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format in line: " + line);
                }
                line = reader.readLine();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void readStaff() {
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
    }

    public void readStudent() {
        studentList = new ArrayList<>();
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
                    String major = data[8].trim();

                    int facultyIndex = 0;
                    for (int i = 0; i < facultyList.size(); i++) {
                        if (facultyList.get(i).getName().equals(facultyName)) {
                            facultyIndex = i;
                        }
                    }
                    Student newStudent = new Student(studentID, name, major, contactDetails,
                            facultyList.get(facultyIndex));
                    studentList.add(newStudent);
                    raafat.students.add(newStudent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetContactDetails() {
        Assert.assertEquals("johndoe@example.com", staffList.get(0).getContactDetails());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Biology", courseList.get(0).getName());
    }

    @Test
    public void testcreateFaculity() {
        Faculty faculty = facultyList.get(0);
        raafat.createFaculty(faculty.getFacultyID(), faculty.getName(), faculty.getContactDetails());
        assertEquals(raafat.faculties.get(0).getFacultyID(), "F1001");

    }

    @Test
    public void testGetCourseOfferSemester() {
        Semester expectedSemester = semesterList.get(0);
        Semester actualSemester = courseList.get(0).getCourseOfferSemester().get(0);
        Assert.assertFalse(courseList.get(0).getCourseOfferSemester().isEmpty());
        Assert.assertEquals(expectedSemester.getName(), actualSemester.getName());
        Assert.assertEquals(expectedSemester.getStartDate(), actualSemester.getStartDate());
        Assert.assertEquals(expectedSemester.getEndDate(), actualSemester.getEndDate());

    }

    @Test
    public void testenterGrade() {
        raafat.assignStudentToCourse(studentList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));
        raafat.enterGrade(studentList.get(0), courseList.get(0), "A");
        assertEquals(studentList.get(0).getGrades().get(courseList.get(0)), new Double(4));

    }

    @Test
    public void testGetNumberOfCredits() {
        Assert.assertEquals(4, courseList.get(0).getNumberOfCredits());
    }

    @Test
    public void testcreateStudent() {
        Student student = studentList.get(0);
        raafat.createStudent(student.getStudentID(), student.getName(), student.getMajor(), student.getContactDetails(),
                facultyList.get(0));
        assertEquals(raafat.students.get(0).getName(), "Alice Smith");

    }

    @Test
    public void testGenerateAcademicsReport() {
        raafat.assignStudentToCourse(studentList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));
        raafat.assignStudentToCourse(studentList.get(0), courseList.get(2),courseList.get(2).getCourseOfferSemester().get(0));

        raafat.enterGrade(studentList.get(0), courseList.get(0), "B+");
        raafat.enterGrade(studentList.get(0), courseList.get(2), "C+");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        raafat.generateAcademicsReport(studentList.get(0), semesterList.get(0));

        String expectedOutput = "Academic Report for Alice Smith in Spring2023:\r\n" +
                "GPA for Spring2023: 3.07\r\n" +
                "Academic Standing: Honor\r\n" +
                "Courses and Grades:\r\n" +
                "Biology: B+\r\n" +
                "Math: C+\r\n";

        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testGetMeetingTimes() {
        ArrayList<MeetingSession> expectedMeetingTimes = new ArrayList<>();
        expectedMeetingTimes.add(new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(2)));
        expectedMeetingTimes.add(new MeetingSession(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), Duration.ofHours(2)));

        ArrayList<MeetingSession> actualMeetingTimes = courseList.get(0).getMeetingTimes();

        Assert.assertEquals(expectedMeetingTimes.size(), actualMeetingTimes.size());

        for (int i = 0; i < expectedMeetingTimes.size(); i++) {
            MeetingSession expectedSession = expectedMeetingTimes.get(i);
            MeetingSession actualSession = actualMeetingTimes.get(i);

            Assert.assertEquals(expectedSession.getDayOfWeek(), actualSession.getDayOfWeek());
            Assert.assertEquals(expectedSession.getStartTime(), actualSession.getStartTime());
            Assert.assertEquals(expectedSession.getDuration(), actualSession.getDuration());
        }
    }

    @Test
    public void testGetGrade() {
        Assert.assertEquals(0.0, courseList.get(0).getGrade(), 0);
    }

    @Test
    public void testGetStaffName() {
        Assert.assertEquals("John Doe", staffList.get(0).getName());
    }

    @Test
    public void testSetSatffName() {
        staffList.get(0).setName("Jane Doe");
        Assert.assertEquals("Jane Doe", staffList.get(0).getName());
    }

    @Test
    public void testSetSatffContactDetails() {
        staffList.get(0).setContactDetails("janedoe@example.com");
        Assert.assertEquals("janedoe@example.com", staffList.get(0).getContactDetails());
    }

    @Test
    public void testGetRole() {
        Assert.assertEquals("staff", staffList.get(0).getRole());
    }

    @Test
    public void testSetRole() {
        staffList.get(0).setRole("administrator");
        Assert.assertEquals("administrator", staffList.get(0).getRole());
    }

    @Test
    public void testSetContactDetails() {
        studentList.get(0).setContactDetails("jane@example.com");
        Assert.assertEquals("jane@example.com", studentList.get(0).getContactDetails());
    }

    @Test
    public void testassignStudentToCourse() {
        raafat.assignStudentToCourse(studentList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));

        boolean result = raafat.assignStudentToCourse(studentList.get(0), courseList.get(1),courseList.get(0).getCourseOfferSemester().get(0));

        assertEquals(false, result);
    }

    @Test
    public void testPrerequisitesForCourse() {
        assertEquals(courseList.get(3).getPrerequisites().size(), 1);
    }

    @Test
    public void testGetPrerequisites() {
        Assert.assertFalse(courseList.get(3).getPrerequisites().isEmpty());
        Assert.assertEquals(courseList.get(3).getPrerequisites().get(0).getName(), "Math");

    }

    @Test
    public void testCreateSemester() {
        try {
            raafat.createSemester("Spring2024", LocalDate.parse("2024-02-15"), LocalDate.parse("2024-06-15"));
            assertEquals(raafat.semesters.size(), 3);
        } catch (IllegalArgumentException e) {
            fail("Semester creation failed: " + e.getMessage());
        }
    }

    @Test
    public void testassignStaffToCourse() {
        boolean firstAssignmentResult = raafat.assignCourseToStaff(staffList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));
        assertEquals(true, firstAssignmentResult);

        boolean secondAssignmentResult = raafat.assignCourseToStaff(staffList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));
        assertEquals(false, secondAssignmentResult);
    }

    @Test
    public void rr() {
        boolean firstAssignment = raafat.assignStudentToCourse(studentList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));

        boolean secondAssignment = raafat.assignStudentToCourse(studentList.get(0), courseList.get(0),courseList.get(0).getCourseOfferSemester().get(0));

        assertEquals(true, firstAssignment);
        assertEquals(false, secondAssignment);
    }

    @Test
    public void testGetFaculty() {
        Assert.assertTrue(courseList.get(0).getFaculty().isPresent());
        Assert.assertEquals(facultyList.get(0), courseList.get(0).getFaculty().get());
    }

    @Test
    public void testcreateCourse() {
        Course course = courseList.get(0);
        raafat.createCourse(course.getName(), 3, facultyList.get(0), course.getMeetingTimes(),
                course.getPrerequisites(), course.getCourseOfferSemester());
        assertEquals(raafat.courses.size(), 6);

    }

}