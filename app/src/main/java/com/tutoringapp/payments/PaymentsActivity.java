package com.tutoringapp.payments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Payment;
import com.tutoringapp.models.Session;
import com.tutoringapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * PaymentsActivity handles payments for tutoring sessions.
 */
public class PaymentsActivity extends AppCompatActivity {

    private TextView tvStudentName, tvStudentEmail, tvSessionCount, tvAmountDue, tvNoPayment;
    private Button btnPayNow, btnViewHistory;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat;
    
    private static final int PRICE_PER_SESSION = 100; // R100 per session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvSessionCount = findViewById(R.id.tvSessionCount);
        tvAmountDue = findViewById(R.id.tvAmountDue);
        tvNoPayment = findViewById(R.id.tvNoPayment);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Load payment info
        loadPaymentInfo();

        // Set up button click listeners
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentsActivity.this, PaymentHistoryActivity.class));
            }
        });
    }

    /**
     * Load payment information for the current month
     */
    private void loadPaymentInfo() {
        int studentId = sessionManager.getUserId();
        
        // Set student details
        tvStudentName.setText(sessionManager.getUserFullName());
        tvStudentEmail.setText(sessionManager.getUserEmail());
        
        // Get the first day of current month 
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();
        
        // Get the last day of current month
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfMonth = calendar.getTimeInMillis();
        
        // Check if payment for this month already exists
        Payment existingPayment = databaseHelper.getPaymentForMonth(studentId, startOfMonth, endOfMonth);
        if (existingPayment != null) {
            // Already paid for this month
            tvNoPayment.setVisibility(View.VISIBLE);
            tvNoPayment.setText("You've already paid for " + dateFormat.format(new Date()) + 
                    "\nAmount: R" + existingPayment.getAmount());
            
            tvStudentName.setVisibility(View.GONE);
            tvStudentEmail.setVisibility(View.GONE);
            tvSessionCount.setVisibility(View.GONE);
            tvAmountDue.setVisibility(View.GONE);
            btnPayNow.setVisibility(View.GONE);
            return;
        }
        
        // Get all accepted sessions for the previous month
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfPrevMonth = calendar.getTimeInMillis();
        
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfPrevMonth = calendar.getTimeInMillis();
        
        List<Session> acceptedSessions = databaseHelper.getSessionsByStudentIdAndStatusForPeriod(
                studentId, "accepted", startOfPrevMonth, endOfPrevMonth);
        
        // Add completed sessions that were accepted
        List<Session> completedSessions = databaseHelper.getSessionsByStudentIdAndStatusForPeriod(
                studentId, "completed", startOfPrevMonth, endOfPrevMonth);
        acceptedSessions.addAll(completedSessions);
        
        int sessionCount = acceptedSessions.size();
        int amountDue = sessionCount * PRICE_PER_SESSION;
        
        if (sessionCount == 0) {
            // No sessions to pay for
            tvNoPayment.setVisibility(View.VISIBLE);
            tvNoPayment.setText("You have no sessions to pay for this month.");
            
            tvStudentName.setVisibility(View.GONE);
            tvStudentEmail.setVisibility(View.GONE);
            tvSessionCount.setVisibility(View.GONE);
            tvAmountDue.setVisibility(View.GONE);
            btnPayNow.setVisibility(View.GONE);
        } else {
            // Display payment information
            tvNoPayment.setVisibility(View.GONE);
            
            tvStudentName.setVisibility(View.VISIBLE);
            tvStudentEmail.setVisibility(View.VISIBLE);
            tvSessionCount.setVisibility(View.VISIBLE);
            tvAmountDue.setVisibility(View.VISIBLE);
            btnPayNow.setVisibility(View.VISIBLE);
            
            tvSessionCount.setText("Total sessions: " + sessionCount);
            tvAmountDue.setText("Amount due: R" + amountDue);
        }
    }

    /**
     * Process payment via PayFast
     */
    private void processPayment() {
        int studentId = sessionManager.getUserId();
        
        // Get the first day of previous month for calculating sessions
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfPrevMonth = calendar.getTimeInMillis();
        
        // Get the last day of previous month
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long endOfPrevMonth = calendar.getTimeInMillis();
        
        // Count sessions for previous month
        List<Session> acceptedSessions = databaseHelper.getSessionsByStudentIdAndStatusForPeriod(
                studentId, "accepted", startOfPrevMonth, endOfPrevMonth);
        List<Session> completedSessions = databaseHelper.getSessionsByStudentIdAndStatusForPeriod(
                studentId, "completed", startOfPrevMonth, endOfPrevMonth);
        acceptedSessions.addAll(completedSessions);
        
        int sessionCount = acceptedSessions.size();
        int amountDue = sessionCount * PRICE_PER_SESSION;
        
        if (sessionCount == 0) {
            Toast.makeText(this, "No payment due", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get current month for payment record
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long paymentMonth = calendar.getTimeInMillis();
        
        // Create a new payment record
        Payment payment = new Payment();
        payment.setStudentId(studentId);
        payment.setAmount(amountDue);
        payment.setSessionCount(sessionCount);
        payment.setPaymentDate(System.currentTimeMillis());
        payment.setPaymentMonth(paymentMonth);
        
        // Save payment to database
        long paymentId = databaseHelper.addPayment(payment);
        
        if (paymentId > 0) {
            // Redirect to PayFast website
            // In a real application, you would implement a proper PayFast integration
            // using their API and security requirements
            
            // Example URL for demonstration
            String payFastUrl = "https://www.payfast.co.za/eng/process";
            
            // Add payment parameters
            Uri.Builder builder = Uri.parse(payFastUrl).buildUpon();
            // Replace with actual merchant ID
            builder.appendQueryParameter("merchant_id", "MERCHANT_ID");
            builder.appendQueryParameter("merchant_key", "MERCHANT_KEY");
            builder.appendQueryParameter("amount", String.valueOf(amountDue));
            builder.appendQueryParameter("item_name", "Tutoring Sessions");
            builder.appendQueryParameter("return_url", "com.tutoringapp://payment_success");
            builder.appendQueryParameter("cancel_url", "com.tutoringapp://payment_cancelled");
            
            // Start browser with PayFast URL
            Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
            
            // Comment: In a real application, replace the above URL with the actual PayFast API endpoint
            // and implement proper security measures as required by PayFast
            
            startActivity(intent);
            
            // Refresh the page when returning
            loadPaymentInfo();
        } else {
            Toast.makeText(this, "Payment processing failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh payment info when activity is resumed
        loadPaymentInfo();
    }
}
