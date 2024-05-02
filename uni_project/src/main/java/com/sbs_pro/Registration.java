package com.sbs_pro;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Registration {
    private final ReentrantLock lock = new ReentrantLock();
    ArrayList<Student> students;
    ArrayList<Faculty> faculties;
    ArrayList<Staff> staffs;
    ArrayList<Course> courses;
    ArrayList<Semester> semesters;

    public Registration() {
        this.students = new ArrayList<>();
        this.faculties = new ArrayList<>();
        this.staffs = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.semesters = new ArrayList<>();
    }

    public void createCourse(String name, int numberOfCredits, Faculty faculty,
            ArrayList<MeetingSession> meetingSessions, ArrayList<Course> prerequisites,
            ArrayList<Semester> courseOfferSemester) {
        lock.lock();
        try {
            Course newCourse = new Course(name, numberOfCredits, faculty, meetingSessions, prerequisites,
                    courseOfferSemester);
            courses.add(newCourse);
           

        } finally {
            lock.unlock();
        }
    }

    public void createStudent(String studentID, String name, String major, String contactDetails, Faculty faculty) {
        lock.lock();
        try {
            Student newStudent = new Student(studentID, name, major, contactDetails, faculty);
            students.add(newStudent);
        } finally {
            lock.unlock();
        }
    }

    public void createFaculty(String facultyID, String name, String contactDetails) {
        lock.lock();
        try {
            Faculty newFaculty = new Faculty(facultyID, name, contactDetails);
            System.out.println("Adding new faculty. Current size: " + faculties.size());
            faculties.add(newFaculty);
            System.out.println("New faculty added. New size: " + faculties.size());
        } finally {
            lock.unlock();
        }
    }

    public void createSemester(String name, LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
        lock.lock();
        try {
            // Check if the semester duration is exactly 4 months
            if (!endDate.equals(startDate.plusMonths(4))) {
                throw new IllegalArgumentException("Semester duration must be exactly 4 months");
            }
    
            Semester newSemester = new Semester(name, startDate, endDate);
    
            if (semesters.stream()
                    .anyMatch(existingSemester -> !startDate.isAfter(existingSemester.getEndDate().orElse(LocalDate.MIN)))) {
                throw new IllegalArgumentException("New semester starts before the end of an existing semester");
            }
    
            if (semesters.stream().anyMatch(existingSemester -> isDateOverlap(newSemester, existingSemester))) {
                throw new IllegalArgumentException("Date overlap found with an existing semester");
            }
    
            System.out.println("Adding new Semester. Current size: " + semesters.size());
            semesters.add(newSemester);
            System.out.println("New Semester added. New size: " + semesters.size());
        } finally {
            lock.unlock();
        }
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

    public boolean assignStudentToCourse(Student student, Course course, Semester sem) {
        lock.lock();
        try {
            if (student.getCourses().stream()
                    .anyMatch(cor -> cor.getCourseOfferSemester().contains(sem) && cor.equals(course))) {
                System.out.println("Student already enrolled in " + course.getName());
                return false;
            }
    
            // Uncomment the following block if you want to check for course existence without considering semesters
            // if (student.getCourses().contains(course)) {
            //     System.out.println("Student already enrolled in " + course.getName());
            //     return false;
            // }
    
            if (!hasPrerequisitesCompleted(student, course)) {
                System.out.println("Student lacks prerequisites for " + course.getName());
                return false;
            }
    
            if (hasScheduleConflict(student, course, sem)) {
                System.out.println("Schedule conflict found for " + course.getName());
                return false;
            }
    
            student.getCourses().add(course);
            System.out.println("Course " + course.getName() + " assigned to student " + student.getName());
            return true;
        } finally {
            lock.unlock();
        }
    }
    

    private boolean hasPrerequisitesCompleted(Student student, Course course) {
        List<Course> studentCourses = student.getCourses();
        List<Course> prerequisites = course.getPrerequisites();
    
        // Check if student has completed all prerequisites for the course
        return prerequisites.stream()
                .allMatch(prerequisite -> studentCourses.contains(prerequisite) && prerequisite.getGrade() != 0);
    }
    
    private boolean hasScheduleConflict(Student student, Course course, Semester sem) {
        List<MeetingSession> courseSessions = course.getMeetingTimes();
    
        List<MeetingSession> studentSessions = student.getCourses().stream()
                .filter(enrolledCourse -> enrolledCourse.getCourseOfferSemester().contains(sem))
                .flatMap(enrolledCourse -> enrolledCourse.getMeetingTimes().stream())
                .collect(Collectors.toList());
    
        return courseSessions.stream()
                .anyMatch(newSession ->
                        studentSessions.stream().anyMatch(enrolledSession ->
                                isTimeOverlap(newSession, enrolledSession)));
    }
    

    private boolean isTimeOverlap(MeetingSession session1, MeetingSession session2) {
        Optional<DayOfWeek> dayOfWeek1 = session1.getDayOfWeek();
        Optional<LocalTime> startTime1Opt = session1.getStartTime();
        Optional<Duration> duration1Opt = session1.getDuration();

        Optional<DayOfWeek> dayOfWeek2 = session2.getDayOfWeek();
        Optional<LocalTime> startTime2Opt = session2.getStartTime();
        Optional<Duration> duration2Opt = session2.getDuration();

        if (!dayOfWeek1.isPresent() || !startTime1Opt.isPresent() || !duration1Opt.isPresent() ||
                !dayOfWeek2.isPresent() || !startTime2Opt.isPresent() || !duration2Opt.isPresent()) {
            return true; // Not enough information to determine overlap
        }

        LocalTime startTime1 = startTime1Opt.get();
        LocalTime endTime1 = startTime1.plus(duration1Opt.get());
        endTime1 = startTime1.plus(10, ChronoUnit.MINUTES);

        LocalTime startTime2 = startTime2Opt.get();
        LocalTime endTime2 = startTime2.plus(duration2Opt.get());
        endTime2 = startTime2.plus(10, ChronoUnit.MINUTES);
        if (dayOfWeek1.get().equals(dayOfWeek2.get())) {
            boolean startsBeforeEnd = startTime1.isBefore(endTime2);
            boolean endsAfterStart = endTime1.isAfter(startTime2);

            return startsBeforeEnd && endsAfterStart; // If both conditions are true, there's an overlap
        }
        return false; // Different days, no overlap
    }

    public boolean assignCourseToStaff(Staff staff, Course course, Semester sem) {
        lock.lock();
        try {
            if (hasScheduleConflict(staff, course,sem)) {
                System.out.println("Schedule conflict found for course " + course.getName() + " with staff member "
                        + staff.getName());
                return false;
            }

            staff.staffCourses.add(course);

            System.out.println("Course " + course.getName() + " assigned to staff member " + staff.getName());
            return true;
        } finally {
            lock.unlock();
        }
    }

    private boolean hasScheduleConflict(Staff staff, Course newCourse, Semester sem) {
        List<MeetingSession> staffSessions = staff.getStaffCourses()
                .stream()
                .filter(enrolledCourse -> enrolledCourse.getCourseOfferSemester().contains(sem))
                .flatMap(enrolledCourse -> enrolledCourse.getMeetingTimes().stream())
                .collect(Collectors.toList());
    
        return newCourse.getMeetingTimes().stream()
                .anyMatch(newSession ->
                        staffSessions.stream()
                                .anyMatch(staffSession -> !isTimeOverlap(newSession, staffSession)));
    }
    

    public ArrayList<Course> GetAvailableCourses(Semester semester) {
        return semester.getCourses();
    }

    public void generateAcademicsReport(Student student, Semester semester) {
        System.out.println("Academic Report for " + student.getName() + " in " + semester.getName().get() + ":");

        // Calculate GPA for the given semester
        double semesterGPA = student.calcGPA(student.getCourses(), semester);
        System.out.println("GPA for " + semester.getName().get() + ": " + semesterGPA);
        //
        // Determine academic standing based on GPA
        String academicStanding = determineAcademicStanding(semesterGPA);
        System.out.println("Academic Standing: " + academicStanding);

        // Display course names and grades
        System.out.println("Courses and Grades:");
        student.getGrades()
                .forEach((course, grade) -> System.out.println(course.getName() + ": " + convertGrade(grade)));
    }

    String determineAcademicStanding(double gpa) {
        return gpa >= 3.75 ? "Highest Dean`s List"
                : gpa >= 3.5 ? "Dean`s List" : gpa >= 3.0 ? "Honor" : gpa <= 1.74 ? "Propation" : "";
    }

    public Optional<Course> findCourseByName(String name) {
        return courses.stream()
                .filter(course -> name.equals(course.getName()))
                .findFirst();
    }

    private String convertGrade(double grade) {
        if (grade == 4.0)
            return "A";
        else if (grade == 3.5)
            return "B+";
        else if (grade == 3.0)
            return "B";
        else if (grade == 2.5)
            return "C+";
        else if (grade == 2.0)
            return "C";
        else if (grade == 1.5)
            return "D+";
        else if (grade == 1.0)
            return "D";
        else
            return "F";
    }

    public boolean checkForDateOverlap(ArrayList<Semester> semesters) {
        return semesters.stream()
                .anyMatch(semester1 ->
                        semesters.stream()
                                .filter(semester2 -> !semester1.equals(semester2))
                                .anyMatch(semester2 -> isDateOverlap(semester1, semester2))
                );
    }

    private boolean isDateOverlap(Semester semester1, Semester semester2) {
        Optional<LocalDate> start1 = semester1.getStartDate();
        Optional<LocalDate> end1 = semester1.getEndDate();
        Optional<LocalDate> start2 = semester2.getStartDate();
        Optional<LocalDate> end2 = semester2.getEndDate();

        // Ensure all dates are present
        if (!start1.isPresent() || !end1.isPresent() || !start2.isPresent() || !end2.isPresent()) {
            return false; // Cannot determine overlap without all dates
        }

        LocalDate startDate1 = start1.get();
        LocalDate endDate1 = end1.get();
        LocalDate startDate2 = start2.get();
        LocalDate endDate2 = end2.get();

        // Check for overlap
        return !startDate1.isAfter(endDate2) && !endDate1.isBefore(startDate2);
    }

}