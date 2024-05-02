package com.sbs_pro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Student extends User {

    private String studentID;
    private String major;
    static ArrayList<Course> courses;
    private double CUM;
    private Map<Course, Double> grades;
    private Map<Semester, Double> GPA;
    private Faculty faculty;

    

        public Student(String studentID, String name, String major, String contactDetails, Faculty faculty) {

            super(name, "Student", contactDetails);
    
        this.studentID = studentID;
       
        this.major = major;
     
        this.CUM = 0.0;
        this.courses = new ArrayList<>();
        this.grades = new HashMap<>();
        this.GPA = new HashMap<>();
        this.faculty = faculty;
    }


    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public synchronized void setGpaSemester(Map<Semester, Double> GPA) {

        this.GPA = GPA;
    }

    public double getCUM() {
        return CUM;
    }

    public Map<Course, Double> getGrades() {
        return grades;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public String getStudentID() {
        return this.studentID;
    }

    public synchronized void setGrade(Course course, String grade) {
        this.grades.put(course, convertGrade(grade));
    }

    public synchronized void recalculateCUM() {
        double totalPoints = 0.0;
        double totalCredits = 0.0;
        for (Map.Entry<Course, Double> entry : grades.entrySet()) {
            Course course = entry.getKey();
            Double grade = entry.getValue();
            totalPoints += grade * course.getNumberOfCredits();
            totalCredits += course.getNumberOfCredits();
        }
        this.CUM = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    public Optional<Faculty> getFaculty() {
        return Optional.ofNullable(faculty);
    }

    public synchronized void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public double calcGPA(ArrayList<Course> courses, Semester semester) {
        ForkJoinPool pool = new ForkJoinPool();
        double GPA = pool.invoke(new CalcGPATask(courses, semester, this.grades, 0, courses.size()));
        this.GPA.put(semester, GPA);
        return Math.round(GPA * 100.0) / 100.0;
    }

    private class CalcGPATask extends RecursiveTask<Double> {
        private final ArrayList<Course> courses;
        private final Semester semester;
        private final Map<Course, Double> grades;
        private final int start;
        private final int end;
        private static final int THRESHOLD = 10;

        public CalcGPATask(ArrayList<Course> courses, Semester semester, Map<Course, Double> grades, int start,
                int end) {
            this.courses = courses;
            this.semester = semester;
            this.grades = grades;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                return computeDirectly();
            } else {
                int mid = start + (end - start) / 2;
                CalcGPATask left = new CalcGPATask(courses, semester, grades, start, mid);
                CalcGPATask right = new CalcGPATask(courses, semester, grades, mid, end);

                left.fork();
                Double rightResult = right.compute();
                Double leftResult = left.join();

                // Combine the total grade points and total credits from both subtasks
                double totalGradePoints = leftResult + rightResult;
                int totalCreditsLeft = calculateTotalCredits(start, mid);
                int totalCreditsRight = calculateTotalCredits(mid, end);
                int totalCredits = totalCreditsLeft + totalCreditsRight;

                return totalCredits > 0 ? totalGradePoints / totalCredits : 0;
            }
        }

        private Double computeDirectly() {
            double totalGradePoints = 0.0;
            int totalCredits = 0;

            for (int i = start; i < end; i++) {
                Course course = courses.get(i);

                if (grades.containsKey(course)) {
                    double grade = grades.get(course);
                    int credits = course.getNumberOfCredits();
                    totalGradePoints += grade * credits;
                    totalCredits += credits;

                }
            }

            return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        }

        private int calculateTotalCredits(int start, int end) {
            int totalCredits = 0;
            for (int i = start; i < end; i++) {
                Course course = courses.get(i);
                if ( course.getCourseOfferSemester().contains(semester)) {
                    totalCredits += course.getNumberOfCredits();
                }
            }
            return totalCredits;
        }
    }

    private double convertGrade(String grade) {
        if (grade.equalsIgnoreCase("A"))
            return 4.0;
        else if (grade.equalsIgnoreCase("B+"))
            return 3.5;
        else if (grade.equalsIgnoreCase("B"))
            return 3.0;
        else if (grade.equalsIgnoreCase("C+"))
            return 2.5;
        else if (grade.equalsIgnoreCase("C"))
            return 2.0;
        else if (grade.equalsIgnoreCase("D+"))
            return 1.5;
        else if (grade.equalsIgnoreCase("D"))
            return 1.0;
        else
            return 0.0;
    }
}