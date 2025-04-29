package com.tutoringapp.models;

/**
 * Represents a subject that can be tutored.
 */
public class Subject {
    private int id;
    private String name;
    private String courseCode;

    public Subject() {
        // Default constructor
    }

    public Subject(int id, String name, String courseCode) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
