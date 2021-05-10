package com.example.bedalground;

public class PostItem {
    String title, sub;
    Long time;
    double meter;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public double getMeter() {
        return meter;
    }

    public void setMeter(double meter) {
        this.meter = meter;
    }

    public PostItem(String title, String sub, Long time, double meter){
        this.title = title;
        this.sub = sub;
        this.time = time;
        this.meter = meter;
    }
}
