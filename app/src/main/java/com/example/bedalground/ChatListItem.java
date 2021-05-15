package com.example.bedalground;

public class ChatListItem {
    String title, last_message, last_time, chat_key;
    public ChatListItem(String title, String last_message, String last_time, String chat_key){
        this.title=title;
        this.last_message=last_message;
        this.last_time=last_time;
        this.chat_key=chat_key;
    }
}
