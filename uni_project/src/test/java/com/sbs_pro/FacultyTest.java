package com.sbs_pro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FacultyTest {

    private Faculty faculty;
    private Course course;
    private List<Faculty> facultyList;

    @Before
    public void setUp() {
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
                    facultyList.add(new Faculty(facultyID, name, contactDetails));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!facultyList.isEmpty()) {
            faculty = facultyList.get(0);
        } else {
            faculty = new Faculty("F001", "Default Faculty", "default@example.com");
        }
    }

    @Test
    public void testGetFacultyID() {
        Assert.assertEquals("F1001", faculty.getFacultyID());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("Science", faculty.getName());
    }

    @Test
    public void testSetName() {
        faculty.setName("Since");
        Assert.assertEquals("Since", faculty.getName());
    }

    @Test
    public void testGetContactDetails() {
        Assert.assertEquals("Science@example.com", faculty.getContactDetails());
    }

    @Test
    public void testSetContactDetails() {
        faculty.setContactDetails("dr.jones@example.com");
        Assert.assertEquals("dr.jones@example.com", faculty.getContactDetails());
    }

    @Test
    public void testGetRole() {
        Assert.assertEquals("Faculty", faculty.getRole());
    }

    @Test
    public void testGetOptionalContactDetails() {
        Optional<String> contactDetails = faculty.getOptionalContactDetails();
        Assert.assertTrue(contactDetails.isPresent());
        Assert.assertEquals("Science@example.com", contactDetails.get());
    }

}