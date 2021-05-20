package com.example.bedalground;

public class ChatListItem {
    private String title, last_message, last_time, chat_key;

    public ChatListItem(String title, String last_message, String last_time, String chat_key){
        this.title=title;
        this.last_message=last_message;
        this.last_time=last_time;
        this.chat_key=chat_key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }
}
