package com.smorodya.hobbymanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.smorodya.hobbymanager.data.*;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.smorodya.hobbymanager.logic.DueHabit;
import com.smorodya.hobbymanager.logic.ScheduleUtils;

import java.time.LocalDate;
import java.util.*;

public class MainViewModel extends AndroidViewModel {

    private final HabitRepository repo;
    private final MediatorLiveData<List<DueHabit>> dueHabits = new MediatorLiveData<>();

    private List<Habit> cachedHabits = new ArrayList<>();
    private List<HabitLog> cachedLogs = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repo = new HabitRepository(application);

        int today = DateUtils.todayInt();
        LiveData<List<Habit>> habitsLd = repo.observeHabits();
        LiveData<List<HabitLog>> logsLd = repo.observeLogsByDate(today);

        dueHabits.addSource(habitsLd, habits -> {
            cachedHabits = habits == null ? new ArrayList<>() : habits;
            recompute();
        });

        dueHabits.addSource(logsLd, logs -> {
            cachedLogs = logs == null ? new ArrayList<>() : logs;
            recompute();
        });
    }

    public LiveData<List<DueHabit>> observeDueHabits() {
        return dueHabits;
    }

    public void onCheckedChanged(long habitId, boolean checked) {
        repo.setChecked(habitId, DateUtils.todayInt(), checked);
    }

    public void deleteHabit(Habit habit) {
        repo.deleteHabit(habit);
    }

    private void recompute() {
        LocalDate date = LocalDate.now();

        Map<Long, HabitLog> logMap = new HashMap<>();
        for (HabitLog l : cachedLogs) logMap.put(l.habitId, l);

        List<DueHabit> out = new ArrayList<>();
        for (Habit h : cachedHabits) {
            if (!ScheduleUtils.isDueToday(h, date)) continue;

            HabitLog log = logMap.get(h.id);

            // дефолт для дня без лога:
            boolean defaultChecked = (h.type == HabitType.BAD);
            boolean checked = (log != null) ? log.checked : defaultChecked;

            out.add(new DueHabit(h, checked));
        }

        dueHabits.setValue(out);
    }
}