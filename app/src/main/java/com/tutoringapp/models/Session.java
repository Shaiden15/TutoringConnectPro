package com.tutoringapp.models;

/**
 * Represents a tutoring session between a student and a tutor.
 */
public class Session {
    private int id;
    private int studentId;
    private int tutorId;
    private int subjectId;
    private String subjectName;
    private long sessionDate;
    private boolean isOnlineSession;
    private String status; // pending, accepted, rejected, completed
    private String rejectionReason;
    private long bookingDate;
    private boolean feedbackProvided;

    public Session() {
        // Default constructor
    }

    public Session(int id, int studentId, int tutorId, int subjectId, String subjectName,
                  long sessionDate, boolean isOnlineSession, String status,
                  String rejectionReason, long bookingDate, boolean feedbackProvided) {
        this.id = id;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.sessionDate = sessionDate;
        this.isOnlineSession = isOnlineSession;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.bookingDate = bookingDate;
        this.feedbackProvided = feedbackProvided;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(long sessionDate) {
        this.sessionDate = sessionDate;
    }

    public boolean isOnlineSession() {
        return isOnlineSession;
    }

    public void setOnlineSession(boolean onlineSession) {
        isOnlineSession = onlineSession;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public long getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(long bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean getFeedbackProvided() {
        return feedbackProvided;
    }

    public void setFeedbackProvided(boolean feedbackProvided) {
        this.feedbackProvided = feedbackProvided;
    }
}
