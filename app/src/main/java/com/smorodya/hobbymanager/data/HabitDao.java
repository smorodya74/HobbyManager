package com.smorodya.hobbymanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAtMillis DESC")
    LiveData<List<Habit>> observeHabits();

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    LiveData<Habit> observeHabitById(long id);

    @Query("SELECT COUNT(*) FROM habits")
    int getCountSync();

    @Insert
    long insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("SELECT * FROM habits")
    List<Habit> getAllSync();
}