package com.sbs_pro;

public class Faculty extends User {

    private String facultyID;

    public Faculty(String facultyID, String name, String contactDetails) {
       super(name, "Faculty", contactDetails);
        this.facultyID = facultyID;
    }

    public String getFacultyID() {
        return facultyID;
    }

}
