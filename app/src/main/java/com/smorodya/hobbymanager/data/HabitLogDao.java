package com.smorodya.hobbymanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface HabitLogDao {

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    LiveData<List<HabitLog>> observeLogsByDate(int date);

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    HabitLog getLogSync(long habitId, int date);

    @Insert
    long insert(HabitLog log);

    @Update
    void update(HabitLog log);

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    void deleteLogsForHabit(long habitId);

    @Query("SELECT * FROM habit_logs WHERE date BETWEEN :from AND :to")
    List<HabitLog> getLogsBetweenSync(int from, int to);
}