package com.tutoringapp.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tutoringapp.R;

import java.util.List;

/**
 * Adapter for displaying chat messages in the chatbot.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private Context context;
    private List<ChatMessage> chatMessages;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        super(context, 0, chatMessages);
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMessage chatMessage = chatMessages.get(position);
        
        if (chatMessage.isUser()) {
            // User message
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
        } else {
            // Bot message
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_bot, parent, false);
        }
        
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        tvMessage.setText(chatMessage.getMessage());
        
        return convertView;
    }
}
