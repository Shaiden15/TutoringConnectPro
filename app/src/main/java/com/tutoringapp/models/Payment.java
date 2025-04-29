package com.tutoringapp.models;

/**
 * Represents a payment made by a student for their tutoring sessions.
 */
public class Payment {
    private int id;
    private int studentId;
    private int amount;
    private int sessionCount;
    private long paymentDate;
    private long paymentMonth; // First day of the month payment is for

    public Payment() {
        // Default constructor
    }

    public Payment(int id, int studentId, int amount, int sessionCount, long paymentDate, long paymentMonth) {
        this.id = id;
        this.studentId = studentId;
        this.amount = amount;
        this.sessionCount = sessionCount;
        this.paymentDate = paymentDate;
        this.paymentMonth = paymentMonth;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public long getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(long paymentMonth) {
        this.paymentMonth = paymentMonth;
    }
}
