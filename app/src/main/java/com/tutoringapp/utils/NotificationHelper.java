package com.tutoringapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.tutoringapp.R;
import com.tutoringapp.dashboard.DashboardActivity;
import com.tutoringapp.database.DatabaseHelper;

/**
 * NotificationHelper handles creating and displaying notifications to users.
 * It also stores notifications in the database for persistence.
 */
public class NotificationHelper {

    private Context context;
    private DatabaseHelper databaseHelper;
    
    private static final String CHANNEL_ID = "tutoringapp_channel";
    private static final String CHANNEL_NAME = "Tutoring App Notifications";
    private static final String CHANNEL_DESC = "Notifications for tutoring sessions";
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        createNotificationChannel();
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    /**
     * Send a notification to a user
     * @param userId The user ID to send the notification to
     * @param title The title of the notification
     * @param message The message content of the notification
     */
    public void sendNotification(int userId, String title, String message) {
        // Store notification in database
        long notificationId = databaseHelper.addNotification(userId, title, message);
        
        // Check if user is currently logged in
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isLoggedIn() && sessionManager.getUserId() == userId) {
            // User is logged in, show notification immediately
            showNotification(title, message);
        }
    }
    
    /**
     * Display a notification on the device
     * @param title The title of the notification
     * @param message The message content of the notification
     */
    private void showNotification(String title, String message) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Generate a unique ID for this notification
        int notificationID = (int) System.currentTimeMillis();
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
    
    /**
     * Check for unread notifications for the current user
     * This is typically called when the user logs in or when the app is resumed
     */
    public void checkForNotifications() {
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            
            // Get unread notifications from database
            for (String[] notification : databaseHelper.getUnreadNotifications(userId)) {
                int notificationId = Integer.parseInt(notification[0]);
                String title = notification[1];
                String message = notification[2];
                
                // Show notification
                showNotification(title, message);
                
                // Mark as read
                databaseHelper.markNotificationAsRead(notificationId);
            }
        }
    }
}
