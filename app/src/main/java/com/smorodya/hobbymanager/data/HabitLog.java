package com.smorodya.hobbymanager.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "habit_logs",
        indices = {@Index(value = {"habitId", "date"}, unique = true)}
)
public class HabitLog {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long habitId;

    public int date;

    public boolean checked;

    public HabitLog(long habitId, int date, boolean checked) {
        this.habitId = habitId;
        this.date = date;
        this.checked = checked;
    }
}