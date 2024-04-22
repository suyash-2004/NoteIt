package com.example.noteit_new.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.noteit_new.Entity.Note;
import java.util.List;

/* loaded from: classes.dex */
@Dao
public interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertnotes(Note note);

    @Delete
    void deletenote(Note note);

    @Query("SELECT * FROM notes_table")
    List<Note> getallnotes();
}