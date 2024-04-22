package com.example.noteit_new.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes_table")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "dateTime")
    private String dateTime;

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @ColumnInfo(name = "noteText")
    private String noteText;

    @ColumnInfo(name = "subtitle")
    private String subtitle;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "webLink")
    private String webLink;

    public String getColor() {
        return color;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getId() {
        return id;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getNoteText() {
        return this.noteText;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public String getTitle() {
        return this.title;
    }

    public String getWebLink() {
        return this.webLink;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public Note() {
    }

    public Note(String title, String subtitle, String noteText, String dateTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.noteText = noteText;
        this.dateTime = dateTime;
    }

    public String toString() {
        return this.title + " : " + this.dateTime;
    }
}
