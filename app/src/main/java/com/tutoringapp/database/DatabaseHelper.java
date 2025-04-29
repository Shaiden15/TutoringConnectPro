package com.tutoringapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tutoringapp.models.Feedback;
import com.tutoringapp.models.Payment;
import com.tutoringapp.models.Session;
import com.tutoringapp.models.Subject;
import com.tutoringapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper handles all database operations for the tutoring app.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    // Database info
    private static final String DATABASE_NAME = "tutoringapp.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SUBJECTS = "subjects";
    private static final String TABLE_SESSIONS = "sessions";
    private static final String TABLE_PAYMENTS = "payments";
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    
    // Common column names
    private static final String KEY_ID = "id";
    
    // Users Table columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_COURSE_CODE = "course_code";
    private static final String KEY_YEAR_OF_STUDY = "year_of_study";
    private static final String KEY_IS_TUTOR = "is_tutor";
    
    // Subjects Table columns
    private static final String KEY_SUBJECT_NAME = "name";
    private static final String KEY_SUBJECT_COURSE_CODE = "course_code";
    
    // Sessions Table columns
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_TUTOR_ID = "tutor_id";
    private static final String KEY_SUBJECT_ID = "subject_id";
    private static final String KEY_SESSION_SUBJECT_NAME = "subject_name";
    private static final String KEY_SESSION_DATE = "session_date";
    private static final String KEY_IS_ONLINE_SESSION = "is_online_session";
    private static final String KEY_STATUS = "status";
    private static final String KEY_REJECTION_REASON = "rejection_reason";
    private static final String KEY_BOOKING_DATE = "booking_date";
    private static final String KEY_FEEDBACK_PROVIDED = "feedback_provided";
    
    // Payments Table columns
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_SESSION_COUNT = "session_count";
    private static final String KEY_PAYMENT_DATE = "payment_date";
    private static final String KEY_PAYMENT_MONTH = "payment_month";
    
    // Feedback Table columns
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_RATING = "rating";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_FEEDBACK_DATE = "feedback_date";
    
    // Notifications Table columns
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IS_READ = "is_read";
    
    // Create table statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_FULL_NAME + " TEXT,"
            + KEY_COURSE_CODE + " TEXT,"
            + KEY_YEAR_OF_STUDY + " INTEGER,"
            + KEY_IS_TUTOR + " INTEGER DEFAULT 0"
            + ")";
    
    // Subjects table create statement
    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE " + TABLE_SUBJECTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_SUBJECT_NAME + " TEXT,"
            + KEY_SUBJECT_COURSE_CODE + " TEXT"
            + ")";
    
    // Sessions table create statement
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_STUDENT_ID + " INTEGER,"
            + KEY_TUTOR_ID + " INTEGER,"
            + KEY_SUBJECT_ID + " INTEGER,"
            + KEY_SESSION_SUBJECT_NAME + " TEXT,"
            + KEY_SESSION_DATE + " INTEGER,"
            + KEY_IS_ONLINE_SESSION + " INTEGER DEFAULT 0,"
            + KEY_STATUS + " TEXT,"
            + KEY_REJECTION_REASON + " TEXT,"
            + KEY_BOOKING_DATE + " INTEGER,"
            + KEY_FEEDBACK_PROVIDED + " INTEGER DEFAULT 0"
            + ")";
    
    // Payments table create statement
    private static final String CREATE_TABLE_PAYMENTS = "CREATE TABLE " + TABLE_PAYMENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_STUDENT_ID + " INTEGER,"
            + KEY_AMOUNT + " INTEGER,"
            + KEY_SESSION_COUNT + " INTEGER,"
            + KEY_PAYMENT_DATE + " INTEGER,"
            + KEY_PAYMENT_MONTH + " INTEGER"
            + ")";
    
    // Feedback table create statement
    private static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE " + TABLE_FEEDBACK + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_SESSION_ID + " INTEGER,"
            + KEY_STUDENT_ID + " INTEGER,"
            + KEY_TUTOR_ID + " INTEGER,"
            + KEY_RATING + " REAL,"
            + KEY_COMMENTS + " TEXT,"
            + KEY_FEEDBACK_DATE + " INTEGER"
            + ")";
    
    // Notifications table create statement
    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER,"
            + KEY_TITLE + " TEXT,"
            + KEY_MESSAGE + " TEXT,"
            + KEY_TIMESTAMP + " INTEGER,"
            + KEY_IS_READ + " INTEGER DEFAULT 0"
            + ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SUBJECTS);
        db.execSQL(CREATE_TABLE_SESSIONS);
        db.execSQL(CREATE_TABLE_PAYMENTS);
        db.execSQL(CREATE_TABLE_FEEDBACK);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        
        // Create new tables
        onCreate(db);
    }
    
    //-------------------------------------------------------------------------
    // User table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add a new user to the database
     */
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FULL_NAME, user.getFullName());
        values.put(KEY_COURSE_CODE, user.getCourseCode());
        values.put(KEY_YEAR_OF_STUDY, user.getYearOfStudy());
        values.put(KEY_IS_TUTOR, user.isTutor() ? 1 : 0);
        
        // Insert row
        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        
        return userId;
    }
    
    /**
     * Get a user by ID
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = " + userId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
            user.setCourseCode(cursor.getString(cursor.getColumnIndex(KEY_COURSE_CODE)));
            user.setYearOfStudy(cursor.getInt(cursor.getColumnIndex(KEY_YEAR_OF_STUDY)));
            user.setTutor(cursor.getInt(cursor.getColumnIndex(KEY_IS_TUTOR)) == 1);
        }
        
        cursor.close();
        db.close();
        
        return user;
    }
    
    /**
     * Get a user by email and password for login
     */
    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_USERS + 
                " WHERE " + KEY_EMAIL + " = '" + email + "'" +
                " AND " + KEY_PASSWORD + " = '" + password + "'";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
            user.setCourseCode(cursor.getString(cursor.getColumnIndex(KEY_COURSE_CODE)));
            user.setYearOfStudy(cursor.getInt(cursor.getColumnIndex(KEY_YEAR_OF_STUDY)));
            user.setTutor(cursor.getInt(cursor.getColumnIndex(KEY_IS_TUTOR)) == 1);
        }
        
        cursor.close();
        db.close();
        
        return user;
    }
    
    /**
     * Check if a user with the given email already exists
     */
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_USERS + 
                " WHERE " + KEY_EMAIL + " = '" + email + "'";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean exists = (cursor.getCount() > 0);
        
        cursor.close();
        db.close();
        
        return exists;
    }
    
    /**
     * Update user details
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FULL_NAME, user.getFullName());
        values.put(KEY_COURSE_CODE, user.getCourseCode());
        values.put(KEY_YEAR_OF_STUDY, user.getYearOfStudy());
        values.put(KEY_IS_TUTOR, user.isTutor() ? 1 : 0);
        
        // Updating row
        int result = db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
        
        db.close();
        
        return result > 0;
    }
    
    //-------------------------------------------------------------------------
    // Subject table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add a new subject to the database
     */
    public long addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, subject.getName());
        values.put(KEY_SUBJECT_COURSE_CODE, subject.getCourseCode());
        
        // Insert row
        long subjectId = db.insert(TABLE_SUBJECTS, null, values);
        db.close();
        
        return subjectId;
    }
    
    /**
     * Get a subject by ID
     */
    public Subject getSubjectById(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_SUBJECTS + 
                " WHERE " + KEY_ID + " = " + subjectId;
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        Subject subject = null;
        if (cursor.moveToFirst()) {
            subject = new Subject();
            subject.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            subject.setName(cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_NAME)));
            subject.setCourseCode(cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_COURSE_CODE)));
        }
        
        cursor.close();
        db.close();
        
        return subject;
    }
    
    /**
     * Get all subjects for a particular course
     */
    public List<Subject> getSubjectsByCourse(String courseCode) {
        List<Subject> subjectList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SUBJECTS + 
                " WHERE " + KEY_SUBJECT_COURSE_CODE + " = '" + courseCode + "'" +
                " ORDER BY " + KEY_SUBJECT_NAME + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                subject.setName(cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_NAME)));
                subject.setCourseCode(cursor.getString(cursor.getColumnIndex(KEY_SUBJECT_COURSE_CODE)));
                
                // Add subject to list
                subjectList.add(subject);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return subjectList;
    }
    
    //-------------------------------------------------------------------------
    // Session table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add a new session to the database
     */
    public long addSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, session.getStudentId());
        values.put(KEY_TUTOR_ID, session.getTutorId());
        values.put(KEY_SUBJECT_ID, session.getSubjectId());
        values.put(KEY_SESSION_SUBJECT_NAME, session.getSubjectName());
        values.put(KEY_SESSION_DATE, session.getSessionDate());
        values.put(KEY_IS_ONLINE_SESSION, session.isOnlineSession() ? 1 : 0);
        values.put(KEY_STATUS, session.getStatus());
        values.put(KEY_REJECTION_REASON, session.getRejectionReason());
        values.put(KEY_BOOKING_DATE, session.getBookingDate());
        values.put(KEY_FEEDBACK_PROVIDED, session.getFeedbackProvided() ? 1 : 0);
        
        // Insert row
        long sessionId = db.insert(TABLE_SESSIONS, null, values);
        db.close();
        
        return sessionId;
    }
    
    /**
     * Update session details
     */
    public boolean updateSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, session.getStudentId());
        values.put(KEY_TUTOR_ID, session.getTutorId());
        values.put(KEY_SUBJECT_ID, session.getSubjectId());
        values.put(KEY_SESSION_SUBJECT_NAME, session.getSubjectName());
        values.put(KEY_SESSION_DATE, session.getSessionDate());
        values.put(KEY_IS_ONLINE_SESSION, session.isOnlineSession() ? 1 : 0);
        values.put(KEY_STATUS, session.getStatus());
        values.put(KEY_REJECTION_REASON, session.getRejectionReason());
        values.put(KEY_BOOKING_DATE, session.getBookingDate());
        values.put(KEY_FEEDBACK_PROVIDED, session.getFeedbackProvided() ? 1 : 0);
        
        // Updating row
        int result = db.update(TABLE_SESSIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(session.getId()) });
        
        db.close();
        
        return result > 0;
    }
    
    /**
     * Get a session by ID
     */
    public Session getSessionById(int sessionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_ID + " = " + sessionId;
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        Session session = null;
        if (cursor.moveToFirst()) {
            session = createSessionFromCursor(cursor);
        }
        
        cursor.close();
        db.close();
        
        return session;
    }
    
    /**
     * Get all sessions for a student
     */
    public List<Session> getSessionsByStudentId(int studentId) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Get all sessions for a tutor
     */
    public List<Session> getSessionsByTutorId(int tutorId) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_TUTOR_ID + " = " + tutorId +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Get all sessions for a student with a specific status
     */
    public List<Session> getSessionsByStudentIdAndStatus(int studentId, String status) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " AND " + KEY_STATUS + " = '" + status + "'" +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Get all sessions for a tutor with a specific status
     */
    public List<Session> getSessionsByTutorIdAndStatus(int tutorId, String status) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_TUTOR_ID + " = " + tutorId +
                " AND " + KEY_STATUS + " = '" + status + "'" +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Get all sessions for a student with a specific status for a time period
     */
    public List<Session> getSessionsByStudentIdAndStatusForPeriod(
            int studentId, String status, long startDate, long endDate) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " AND " + KEY_STATUS + " = '" + status + "'" +
                " AND " + KEY_SESSION_DATE + " >= " + startDate +
                " AND " + KEY_SESSION_DATE + " <= " + endDate +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Get completed sessions without feedback for a student
     */
    public List<Session> getCompletedSessionsWithoutFeedback(int studentId) {
        List<Session> sessionList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " AND " + KEY_STATUS + " = 'completed'" +
                " AND " + KEY_FEEDBACK_PROVIDED + " = 0" +
                " ORDER BY " + KEY_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Session session = createSessionFromCursor(cursor);
                
                // Add session to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return sessionList;
    }
    
    /**
     * Create a Session object from cursor
     */
    private Session createSessionFromCursor(Cursor cursor) {
        Session session = new Session();
        session.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        session.setStudentId(cursor.getInt(cursor.getColumnIndex(KEY_STUDENT_ID)));
        session.setTutorId(cursor.getInt(cursor.getColumnIndex(KEY_TUTOR_ID)));
        session.setSubjectId(cursor.getInt(cursor.getColumnIndex(KEY_SUBJECT_ID)));
        session.setSubjectName(cursor.getString(cursor.getColumnIndex(KEY_SESSION_SUBJECT_NAME)));
        session.setSessionDate(cursor.getLong(cursor.getColumnIndex(KEY_SESSION_DATE)));
        session.setOnlineSession(cursor.getInt(cursor.getColumnIndex(KEY_IS_ONLINE_SESSION)) == 1);
        session.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
        session.setRejectionReason(cursor.getString(cursor.getColumnIndex(KEY_REJECTION_REASON)));
        session.setBookingDate(cursor.getLong(cursor.getColumnIndex(KEY_BOOKING_DATE)));
        session.setFeedbackProvided(cursor.getInt(cursor.getColumnIndex(KEY_FEEDBACK_PROVIDED)) == 1);
        
        return session;
    }
    
    /**
     * Get tutors available for a specific subject (excluding the requesting student)
     */
    public List<User> getTutorsForSubject(int subjectId, int excludeStudentId) {
        List<User> tutorList = new ArrayList<>();
        
        // Get subject to get the course code
        Subject subject = getSubjectById(subjectId);
        if (subject == null) {
            return tutorList;
        }
        
        String courseCode = subject.getCourseCode();
        
        String selectQuery = "SELECT * FROM " + TABLE_USERS + 
                " WHERE " + KEY_IS_TUTOR + " = 1" +
                " AND " + KEY_COURSE_CODE + " = '" + courseCode + "'" +
                " AND " + KEY_ID + " != " + excludeStudentId +
                " ORDER BY " + KEY_FULL_NAME + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                user.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
                user.setCourseCode(cursor.getString(cursor.getColumnIndex(KEY_COURSE_CODE)));
                user.setYearOfStudy(cursor.getInt(cursor.getColumnIndex(KEY_YEAR_OF_STUDY)));
                user.setTutor(cursor.getInt(cursor.getColumnIndex(KEY_IS_TUTOR)) == 1);
                
                // Add user to list
                tutorList.add(user);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return tutorList;
    }
    
    //-------------------------------------------------------------------------
    // Payment table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add a new payment to the database
     */
    public long addPayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, payment.getStudentId());
        values.put(KEY_AMOUNT, payment.getAmount());
        values.put(KEY_SESSION_COUNT, payment.getSessionCount());
        values.put(KEY_PAYMENT_DATE, payment.getPaymentDate());
        values.put(KEY_PAYMENT_MONTH, payment.getPaymentMonth());
        
        // Insert row
        long paymentId = db.insert(TABLE_PAYMENTS, null, values);
        db.close();
        
        return paymentId;
    }
    
    /**
     * Get all payments for a student
     */
    public List<Payment> getPaymentsByStudentId(int studentId) {
        List<Payment> paymentList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " ORDER BY " + KEY_PAYMENT_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment();
                payment.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                payment.setStudentId(cursor.getInt(cursor.getColumnIndex(KEY_STUDENT_ID)));
                payment.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT)));
                payment.setSessionCount(cursor.getInt(cursor.getColumnIndex(KEY_SESSION_COUNT)));
                payment.setPaymentDate(cursor.getLong(cursor.getColumnIndex(KEY_PAYMENT_DATE)));
                payment.setPaymentMonth(cursor.getLong(cursor.getColumnIndex(KEY_PAYMENT_MONTH)));
                
                // Add payment to list
                paymentList.add(payment);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return paymentList;
    }
    
    /**
     * Get payment for a specific month (if it exists)
     */
    public Payment getPaymentForMonth(int studentId, long startOfMonth, long endOfMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_PAYMENTS + 
                " WHERE " + KEY_STUDENT_ID + " = " + studentId +
                " AND " + KEY_PAYMENT_MONTH + " >= " + startOfMonth +
                " AND " + KEY_PAYMENT_MONTH + " <= " + endOfMonth;
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        Payment payment = null;
        if (cursor.moveToFirst()) {
            payment = new Payment();
            payment.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            payment.setStudentId(cursor.getInt(cursor.getColumnIndex(KEY_STUDENT_ID)));
            payment.setAmount(cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT)));
            payment.setSessionCount(cursor.getInt(cursor.getColumnIndex(KEY_SESSION_COUNT)));
            payment.setPaymentDate(cursor.getLong(cursor.getColumnIndex(KEY_PAYMENT_DATE)));
            payment.setPaymentMonth(cursor.getLong(cursor.getColumnIndex(KEY_PAYMENT_MONTH)));
        }
        
        cursor.close();
        db.close();
        
        return payment;
    }
    
    //-------------------------------------------------------------------------
    // Feedback table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add new feedback to the database
     */
    public long addFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, feedback.getSessionId());
        values.put(KEY_STUDENT_ID, feedback.getStudentId());
        values.put(KEY_TUTOR_ID, feedback.getTutorId());
        values.put(KEY_RATING, feedback.getRating());
        values.put(KEY_COMMENTS, feedback.getComments());
        values.put(KEY_FEEDBACK_DATE, feedback.getFeedbackDate());
        
        // Insert row
        long feedbackId = db.insert(TABLE_FEEDBACK, null, values);
        db.close();
        
        return feedbackId;
    }
    
    /**
     * Get feedback for a specific session
     */
    public Feedback getFeedbackBySessionId(int sessionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_FEEDBACK + 
                " WHERE " + KEY_SESSION_ID + " = " + sessionId;
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        Feedback feedback = null;
        if (cursor.moveToFirst()) {
            feedback = new Feedback();
            feedback.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            feedback.setSessionId(cursor.getInt(cursor.getColumnIndex(KEY_SESSION_ID)));
            feedback.setStudentId(cursor.getInt(cursor.getColumnIndex(KEY_STUDENT_ID)));
            feedback.setTutorId(cursor.getInt(cursor.getColumnIndex(KEY_TUTOR_ID)));
            feedback.setRating(cursor.getFloat(cursor.getColumnIndex(KEY_RATING)));
            feedback.setComments(cursor.getString(cursor.getColumnIndex(KEY_COMMENTS)));
            feedback.setFeedbackDate(cursor.getLong(cursor.getColumnIndex(KEY_FEEDBACK_DATE)));
        }
        
        cursor.close();
        db.close();
        
        return feedback;
    }
    
    /**
     * Get all feedback for a tutor
     */
    public List<Feedback> getFeedbackByTutorId(int tutorId) {
        List<Feedback> feedbackList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_FEEDBACK + 
                " WHERE " + KEY_TUTOR_ID + " = " + tutorId +
                " ORDER BY " + KEY_FEEDBACK_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                feedback.setSessionId(cursor.getInt(cursor.getColumnIndex(KEY_SESSION_ID)));
                feedback.setStudentId(cursor.getInt(cursor.getColumnIndex(KEY_STUDENT_ID)));
                feedback.setTutorId(cursor.getInt(cursor.getColumnIndex(KEY_TUTOR_ID)));
                feedback.setRating(cursor.getFloat(cursor.getColumnIndex(KEY_RATING)));
                feedback.setComments(cursor.getString(cursor.getColumnIndex(KEY_COMMENTS)));
                feedback.setFeedbackDate(cursor.getLong(cursor.getColumnIndex(KEY_FEEDBACK_DATE)));
                
                // Add feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return feedbackList;
    }
    
    //-------------------------------------------------------------------------
    // Notification table methods
    //-------------------------------------------------------------------------
    
    /**
     * Add a new notification to the database
     */
    public long addNotification(int userId, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_TITLE, title);
        values.put(KEY_MESSAGE, message);
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        values.put(KEY_IS_READ, 0);
        
        // Insert row
        long notificationId = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        
        return notificationId;
    }
    
    /**
     * Get all unread notifications for a user
     */
    public List<String[]> getUnreadNotifications(int userId) {
        List<String[]> notificationList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + 
                " WHERE " + KEY_USER_ID + " = " + userId +
                " AND " + KEY_IS_READ + " = 0" +
                " ORDER BY " + KEY_TIMESTAMP + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                String[] notification = new String[3];
                notification[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID)));  // notification ID
                notification[1] = cursor.getString(cursor.getColumnIndex(KEY_TITLE));            // title
                notification[2] = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));          // message
                
                // Add notification to list
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return notificationList;
    }
    
    /**
     * Mark a notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_IS_READ, 1);
        
        // Update row
        int result = db.update(TABLE_NOTIFICATIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(notificationId) });
        
        db.close();
        
        return result > 0;
    }
}
