package com.sbs_pro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class Staff extends User {
    private final ReentrantLock lock = new ReentrantLock();
    private String staffID;
   
    ArrayList<Course> staffCourses;

    public Staff(String staffID, String name, String contactDetails) {

        super(name, "Staff", contactDetails);

         this.staffID = staffID;
    
        this.staffCourses = new ArrayList<>();

    }

  

    public List<Course> getStaffCourses() {
        return staffCourses;
    }

    public synchronized void setStaffCourses(ArrayList<Course> staffCourses) {
        this.staffCourses = staffCourses;
    }

    public void enterGrade(Student student, Course course, String grade) {
        lock.lock();
        try {
            student.setGrade(course, grade);
            // student.recalculateCUM();
        } finally {
            lock.unlock();
        }
    }

}
