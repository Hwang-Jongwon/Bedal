package com.example.bedalground;

public class ChatDTO {
    String from, message, realTime, showTime;

    public ChatDTO(String from, String message, String realTime, String showTime) {
        this.from = from;
        this.message = message;
        this.realTime = realTime;
        this.showTime = showTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRealTime() {
        return realTime;
    }

    public void setRealTime(String realTime) {
        this.realTime = realTime;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }
}
