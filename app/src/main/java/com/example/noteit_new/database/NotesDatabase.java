package com.example.noteit_new.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.noteit_new.Dao.NotesDao;
import com.example.noteit_new.Entity.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)

public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase instance;

    public abstract NotesDao notesDao();

    public static synchronized NotesDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            NotesDatabase.class, "notes_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
