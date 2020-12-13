package edu.harvard.cs50.notes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "content")
    public String content;
}
