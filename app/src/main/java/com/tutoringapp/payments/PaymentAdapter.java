package com.tutoringapp.payments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tutoringapp.R;
import com.tutoringapp.models.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying payment history items in a list.
 */
public class PaymentAdapter extends ArrayAdapter<Payment> {

    private Context context;
    private List<Payment> paymentList;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat monthFormat;

    public PaymentAdapter(Context context, List<Payment> paymentList) {
        super(context, 0, paymentList);
        this.context = context;
        this.paymentList = paymentList;
        this.dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        this.monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        }

        Payment payment = paymentList.get(position);

        TextView tvPaymentId = convertView.findViewById(R.id.tvPaymentId);
        TextView tvPaymentMonth = convertView.findViewById(R.id.tvPaymentMonth);
        TextView tvPaymentDate = convertView.findViewById(R.id.tvPaymentDate);
        TextView tvSessionCount = convertView.findViewById(R.id.tvSessionCount);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);

        // Set payment details
        tvPaymentId.setText("Receipt #" + payment.getId());
        
        try {
            // Format payment month
            Date paymentMonth = new Date(payment.getPaymentMonth());
            tvPaymentMonth.setText("For: " + monthFormat.format(paymentMonth));
            
            // Format payment date
            Date paymentDate = new Date(payment.getPaymentDate());
            tvPaymentDate.setText("Paid on: " + dateFormat.format(paymentDate));
        } catch (Exception e) {
            tvPaymentMonth.setText("For: Unknown month");
            tvPaymentDate.setText("Paid on: Unknown date");
        }
        
        tvSessionCount.setText("Sessions: " + payment.getSessionCount());
        tvAmount.setText("Amount: R" + payment.getAmount());

        return convertView;
    }
}
