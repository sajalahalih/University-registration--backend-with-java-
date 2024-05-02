package com.sbs_pro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SemesterTest {

    private Semester semester;
    private final LocalDate startDate = LocalDate.of(2023, 1, 1);
    private final LocalDate endDate = LocalDate.of(2023, 5, 31);

    @Before
    public void setUp() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("semesters.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line = reader.readLine(); // Read and skip the header line
            line = reader.readLine(); // Read the first line of actual data
            while (line != null) {
                String[] parts = line.split(",");
                String semesterName = parts[0].trim();

                try {
                    LocalDate startDate = LocalDate.parse(parts[1].trim());
                    LocalDate endDate = LocalDate.parse(parts[2].trim());

                    semester = new Semester(semesterName, startDate, endDate);

                    ArrayList<Course> courses = new ArrayList<>();
                    String[] courseNames = parts[3].trim().replaceAll("[\\[\\]]", "").split(", ");
                    for (String courseName : courseNames) {
                        Course course = new Course();
                        course.setName(courseName);
                        Semester.addCourse(course);
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format in line: " + line);
                }
                line = reader.readLine();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSemesterConstructor() {
        String expectedName = "Fall2023";
        LocalDate expectedStartDate = LocalDate.of(2023, 8, 20);
        LocalDate expectedEndDate = LocalDate.of(2023, 12, 20);

        Assert.assertNotNull("Semester object is null", semester);
        Assert.assertEquals(expectedName, semester.getName().orElse(null));
        Assert.assertEquals(expectedStartDate, semester.getStartDate().orElse(null));
        Assert.assertEquals(expectedEndDate, semester.getEndDate().orElse(null));
        Assert.assertFalse(semester.getCourses().isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        Assert.assertNotNull("Semester object is null", semester);

        String newName = "Fall2023";
        LocalDate newStartDate = LocalDate.of(2023, 8, 23);
        LocalDate newEndDate = LocalDate.of(2023, 12, 15);

        semester.setName(newName);
        semester.setStartDate(newStartDate);
        semester.setEndDate(newEndDate);

        Assert.assertEquals(newName, semester.getName().orElse(null));
        Assert.assertEquals(newStartDate, semester.getStartDate().orElse(null));
        Assert.assertEquals(newEndDate, semester.getEndDate().orElse(null));
    }

    @Test
    public void testGetSessionPeriod() {
        long period = semester.getSessionPeriod(startDate, endDate);
        Assert.assertEquals(4, period); // Assuming a 4-month semester
    }

    @Test
    public void testAddCourse() {
        Course course = new Course(); // Assuming a no-arg constructor exists
        semester.addCourse(course);
        Assert.assertFalse(semester.getCourses().isEmpty());
        Assert.assertTrue(semester.getCourses().contains(course));
    }

    // Add more tests to cover other scenarios and edge cases...
}