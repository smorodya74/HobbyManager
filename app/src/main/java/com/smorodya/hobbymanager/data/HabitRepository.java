package com.smorodya.hobbymanager.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitRepository {

    private final HabitDao habitDao;
    private final HabitLogDao logDao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public HabitRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        habitDao = db.habitDao();
        logDao = db.habitLogDao();
    }

    public LiveData<List<Habit>> observeHabits() {
        return habitDao.observeHabits();
    }

    public LiveData<Habit> observeHabitById(long id) {
        return habitDao.observeHabitById(id);
    }

    public LiveData<List<HabitLog>> observeLogsByDate(int date) {
        return logDao.observeLogsByDate(date);
    }

    public void insertHabit(Habit habit, Runnable onSuccess, Runnable onLimitReached) {
        io.execute(() -> {
            int count = habitDao.getCountSync();
            if (count >= 5) {
                if (onLimitReached != null) onLimitReached.run();
                return;
            }
            habitDao.insert(habit);
            if (onSuccess != null) onSuccess.run();
        });
    }

    public void updateHabit(Habit habit, Runnable onSuccess) {
        io.execute(() -> {
            habitDao.update(habit);
            if (onSuccess != null) onSuccess.run();
        });
    }

    public void deleteHabit(Habit habit) {
        io.execute(() -> {
            logDao.deleteLogsForHabit(habit.id);
            habitDao.delete(habit);
        });
    }

    public void setChecked(long habitId, int date, boolean checked) {
        io.execute(() -> {
            HabitLog log = logDao.getLogSync(habitId, date);
            if (log == null) {
                logDao.insert(new HabitLog(habitId, date, checked));
            } else {
                log.checked = checked;
                logDao.update(log);
            }
        });
    }

    public List<Habit> getHabitsSync() {
        return habitDao.getAllSync();
    }

    public List<HabitLog> getLogsBetweenSync(int from, int to) {
        return logDao.getLogsBetweenSync(from, to);
    }

    public ExecutorService io() {
        return io;
    }
}