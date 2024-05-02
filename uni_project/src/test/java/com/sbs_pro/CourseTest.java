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

public class CourseTest {

    private Course course;
    private Faculty faculty;
    private Semester semester;

    @Before
    public void setUp() {
        faculty = new Faculty("F001", "FacultyName", "faculty@example.com");
        semester = new Semester("Spring2023", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-05-31"));
        ArrayList<Course> arrCourse = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("courses.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int credits = Integer.parseInt(parts[1].trim());

                ArrayList<MeetingSession> arrSession = new ArrayList<>();
                // Parse the string into MeetingSession objects
                // Example: [Monday 14:00-16:00, Friday 14:00-16:00]
                String[] sessions = parts[3].trim().replaceAll("[\\[\\]]", "").split(", ");
                for (String sessionStr : sessions) {
                    String[] sessionDetails = sessionStr.split(" ");
                    DayOfWeek day = DayOfWeek.valueOf(sessionDetails[0].toUpperCase());
                    String[] times = sessionDetails[1].split("-");
                    LocalTime startTime = LocalTime.parse(times[0]);

                    for (String timeStr : times) {
                        Duration duration = Duration.between(startTime, LocalTime.parse(times[1]));
                        arrSession.add(new MeetingSession(day, startTime, duration));
                    }
                }

                ArrayList<Semester> arrSemester = new ArrayList<>();
                String[] semesters = parts[7].trim().replaceAll("[\\[\\]]", "").split(", ");
                for (String sem : semesters) {
                    arrSemester.add(new Semester(sem, LocalDate.parse("2023-01-01"), LocalDate.parse("2023-05-31")));
                }

                Course course = new Course(name, credits, faculty, arrSession, new ArrayList<>(), arrSemester);
                arrCourse.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("courses.txt file not found in resources.");
        }

        if (!arrCourse.isEmpty()) {
            course = arrCourse.get(0);
        }
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Biology", course.getName());
    }

    @Test
    public void testGetNumberOfCredits() {
        Assert.assertEquals(4, course.getNumberOfCredits());
    }

    @Test
    public void testGetFaculty() {
        Assert.assertTrue(course.getFaculty().isPresent());
        Assert.assertEquals(faculty, course.getFaculty().get());
    }

    @Test
    public void testGetMeetingTimes() {
        // Define expected meeting times
        ArrayList<MeetingSession> expectedMeetingTimes = new ArrayList<>();
        expectedMeetingTimes.add(new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(2)));
        expectedMeetingTimes.add(new MeetingSession(DayOfWeek.MONDAY, LocalTime.of(10, 0), Duration.ofHours(2)));

        // Get actual meeting times from the course
        ArrayList<MeetingSession> actualMeetingTimes = course.getMeetingTimes();

        // Check if the sizes are the same
        Assert.assertEquals(expectedMeetingTimes.size(), actualMeetingTimes.size());

        // Compare individual fields of each MeetingSession object
        for (int i = 0; i < expectedMeetingTimes.size(); i++) {
            MeetingSession expectedSession = expectedMeetingTimes.get(i);
            MeetingSession actualSession = actualMeetingTimes.get(i);

            Assert.assertEquals(expectedSession.getDayOfWeek(), actualSession.getDayOfWeek());
            Assert.assertEquals(expectedSession.getStartTime(), actualSession.getStartTime());
            Assert.assertEquals(expectedSession.getDuration(), actualSession.getDuration());
        }
    }

    @Test
    public void testGetPrerequisites() {
        Assert.assertTrue(course.getPrerequisites().isEmpty());
    }

    @Test
    public void testGetGrade() {
        Assert.assertEquals(0.0, course.getGrade(), 0);
    }

    @Test
    public void testGetCourseOfferSemester() {
        Assert.assertTrue(course.getCourseOfferSemester().isPresent());
        Assert.assertFalse(course.getCourseOfferSemester().get().isEmpty());
        System.out.println(semester);
        System.out.println(course.getCourseOfferSemester().get().get(0));
        Assert.assertEquals(semester, course.getCourseOfferSemester().get().get(0));
    }

}