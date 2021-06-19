package com.example.bedalground;

public class MapItem {
    String title, content, category, time, meter, writer, chatKey;
    double latitude, longitude;

    public MapItem(String title, String content, String category, String time, String meter, String writer, String chatKey, double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.time = time;
        this.meter = meter;
        this.writer = writer;
        this.chatKey = chatKey;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeter() {
        return meter;
    }

    public void setMeter(String meter) {
        this.meter = meter;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
