package com.tutoringapp.chatbot;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatbotActivity provides an in-app assistant to help users with common questions.
 */
public class ChatbotActivity extends AppCompatActivity {

    private ListView listMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help Assistant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        listMessages = findViewById(R.id.listMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Initialize chat messages
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        listMessages.setAdapter(chatAdapter);

        // Add welcome message
        addBotMessage("Hi " + sessionManager.getUserFullName() + "! I'm your tutoring assistant. How can I help you today? You can ask me about booking sessions, payments, feedback, or using the app.");

        // Set up send button click listener
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    /**
     * Handle sending a user message
     */
    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message to chat
        addUserMessage(message);
        etMessage.setText("");

        // Generate bot response
        String response = generateResponse(message);
        addBotMessage(response);
    }

    /**
     * Add a user message to the chat
     */
    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true);
        chatMessages.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    /**
     * Add a bot message to the chat
     */
    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false);
        chatMessages.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    /**
     * Scroll the chat list to the bottom
     */
    private void scrollToBottom() {
        listMessages.post(new Runnable() {
            @Override
            public void run() {
                listMessages.setSelection(chatAdapter.getCount() - 1);
            }
        });
    }

    /**
     * Generate a response based on user input
     */
    private String generateResponse(String message) {
        message = message.toLowerCase();

        // Check for different types of queries
        if (containsAny(message, "hello", "hi", "hey", "greetings")) {
            return "Hello! How can I assist you today?";
        } else if (containsAny(message, "book", "session", "tutor", "tutoring")) {
            return "To book a session, go to the dashboard, select a subject, choose a tutor, and click 'Book Session'. You can select the date, time, and whether it's online or in-person.";
        } else if (containsAny(message, "payment", "pay", "cost", "fee", "payfast")) {
            return "Sessions cost R100 each. Payments are due on the 1st of each month for sessions accepted in the previous month. Go to the Payment page to see your bill and pay via PayFast.";
        } else if (containsAny(message, "feedback", "review", "rate")) {
            return "After a completed session, you can provide feedback by rating the tutor and adding comments. Go to the Feedback page to see sessions that need feedback.";
        } else if (containsAny(message, "profile", "account", "tutor mode", "become tutor")) {
            return "You can edit your profile details and toggle 'Tutor Mode' to become a tutor. Go to the Profile page from the navigation menu.";
        } else if (containsAny(message, "online", "teams", "ms teams")) {
            return "Online sessions are conducted via Microsoft Teams. When booking a session, select 'Online' as the session type. The tutor will provide the meeting link once they accept your request.";
        } else if (containsAny(message, "in person", "face to face", "library", "steve-biko")) {
            return "In-person sessions take place at the Steve-Biko Library. When booking a session, select 'In Person' as the session type.";
        } else if (containsAny(message, "reject", "decline", "cancel", "reschedule")) {
            return "If a tutor rejects your session request, they must provide a reason. You'll receive a notification, and the session will appear in your 'Rejected' tab. You can then book with another tutor or select a different time.";
        } else if (containsAny(message, "thanks", "thank you", "thx", "appreciate")) {
            return "You're welcome! I'm happy to help. Is there anything else you need assistance with?";
        } else if (containsAny(message, "bye", "goodbye", "see you", "exit")) {
            return "Goodbye! Feel free to ask for help anytime. Have a great day!";
        } else {
            return "I'm not sure I understand. Could you rephrase or ask about booking sessions, payments, feedback, or using the app?";
        }
    }

    /**
     * Check if a message contains any of the given keywords
     */
    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
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
