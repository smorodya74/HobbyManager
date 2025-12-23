package com.smorodya.hobbymanager.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title;

    @NonNull
    public HabitType type;

    @NonNull
    public ScheduleMode scheduleMode;

    public int daysOfWeekMask;

    @NonNull
    public Interval interval;

    public int startDate;

    public long createdAtMillis;

    public Habit(@NonNull String title,
                 @NonNull HabitType type,
                 @NonNull ScheduleMode scheduleMode,
                 int daysOfWeekMask,
                 @NonNull Interval interval,
                 int startDate,
                 long createdAtMillis) {
        this.title = title;
        this.type = type;
        this.scheduleMode = scheduleMode;
        this.daysOfWeekMask = daysOfWeekMask;
        this.interval = interval;
        this.startDate = startDate;
        this.createdAtMillis = createdAtMillis;
    }
}