package com.pushparaj.googlemaps;

/**
 * Created by Vijay on 23-03-2017.
 */

public class NotificationRows {
    String title,text;

    public NotificationRows() {
    }

    public NotificationRows(String text, String title) {

        this.text = text;
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
