package com.tutoringapp.payments;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Payment;
import com.tutoringapp.utils.SessionManager;

import java.util.List;

/**
 * PaymentHistoryActivity displays a history of student payments.
 */
public class PaymentHistoryActivity extends AppCompatActivity {

    private ListView listPayments;
    private TextView tvEmptyPayments;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private PaymentAdapter paymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        listPayments = findViewById(R.id.listPayments);
        tvEmptyPayments = findViewById(R.id.tvEmptyPayments);

        // Load payment history
        loadPaymentHistory();
    }

    /**
     * Load payment history from database
     */
    private void loadPaymentHistory() {
        int studentId = sessionManager.getUserId();
        List<Payment> paymentList = databaseHelper.getPaymentsByStudentId(studentId);
        
        if (paymentList.isEmpty()) {
            listPayments.setVisibility(View.GONE);
            tvEmptyPayments.setVisibility(View.VISIBLE);
        } else {
            listPayments.setVisibility(View.VISIBLE);
            tvEmptyPayments.setVisibility(View.GONE);
            paymentAdapter = new PaymentAdapter(this, paymentList);
            listPayments.setAdapter(paymentAdapter);
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
}
