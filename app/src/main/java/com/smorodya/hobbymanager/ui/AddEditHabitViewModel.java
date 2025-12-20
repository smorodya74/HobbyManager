package com.smorodya.hobbymanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.data.HabitRepository;

public class AddEditHabitViewModel extends AndroidViewModel {

    private final HabitRepository repo;

    public AddEditHabitViewModel(@NonNull Application application) {
        super(application);
        repo = new HabitRepository(application);
    }

    public LiveData<Habit> observeHabit(long id) {
        return repo.observeHabitById(id);
    }

    public HabitRepository repo() {
        return repo;
    }
}