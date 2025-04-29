package com.tutoringapp.models;

/**
 * Represents a user in the tutoring application.
 * Users can be students or tutors (or both).
 */
public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String courseCode;
    private int yearOfStudy;
    private boolean isTutor;

    public User() {
        // Default constructor
    }

    public User(int id, String email, String password, String fullName, String courseCode, int yearOfStudy, boolean isTutor) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.courseCode = courseCode;
        this.yearOfStudy = yearOfStudy;
        this.isTutor = isTutor;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public boolean isTutor() {
        return isTutor;
    }

    public void setTutor(boolean tutor) {
        isTutor = tutor;
    }
}
