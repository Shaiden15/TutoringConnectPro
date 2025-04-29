package com.tutoringapp.models;

/**
 * Represents feedback provided by a student for a tutoring session.
 */
public class Feedback {
    private int id;
    private int sessionId;
    private int studentId;
    private int tutorId;
    private float rating;
    private String comments;
    private long feedbackDate;

    public Feedback() {
        // Default constructor
    }

    public Feedback(int id, int sessionId, int studentId, int tutorId, float rating, String comments, long feedbackDate) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(long feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
}
