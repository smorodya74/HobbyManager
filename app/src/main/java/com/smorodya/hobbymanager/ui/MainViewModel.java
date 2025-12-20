package com.smorodya.hobbymanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.data.HabitLog;
import com.smorodya.hobbymanager.data.HabitRepository;
import com.smorodya.hobbymanager.data.HabitType;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.smorodya.hobbymanager.logic.DueHabit;
import com.smorodya.hobbymanager.logic.ScheduleUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {

    private final HabitRepository repo;

    // Выбранная дата (yyyymmdd)
    private final MutableLiveData<Integer> selectedDate = new MutableLiveData<>(DateUtils.todayInt());

    // Прокси LiveData, которая будет "подключаться" к logs на выбранную дату
    private final MediatorLiveData<List<HabitLog>> logsForSelectedDate = new MediatorLiveData<>();
    private LiveData<List<HabitLog>> currentLogsSource;

    private final MediatorLiveData<List<DueHabit>> dueHabits = new MediatorLiveData<>();

    private List<Habit> cachedHabits = new ArrayList<>();
    private List<HabitLog> cachedLogs = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repo = new HabitRepository(application);

        // 1) Подписка на список привычек
        dueHabits.addSource(repo.observeHabits(), habits -> {
            cachedHabits = habits == null ? new ArrayList<>() : habits;
            recompute();
        });

        // 2) Подписка на логи выбранной даты
        dueHabits.addSource(logsForSelectedDate, logs -> {
            cachedLogs = logs == null ? new ArrayList<>() : logs;
            recompute();
        });

        // 3) При смене selectedDate переключаем источник логов
        dueHabits.addSource(selectedDate, d -> {
            if (d == null) d = DateUtils.todayInt();
            switchLogsSource(d);
            // recompute() можно не вызывать тут, потому что logsForSelectedDate тоже триггернет,
            // но оставим для быстрого обновления (без ожидания эмиссии)
            recompute();
        });

        // Инициализация источника логов на стартовую дату
        Integer init = selectedDate.getValue();
        if (init == null) init = DateUtils.todayInt();
        switchLogsSource(init);
    }

    public LiveData<List<DueHabit>> observeDueHabits() {
        return dueHabits;
    }

    public LiveData<Integer> observeSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(int yyyymmdd) {
        selectedDate.setValue(yyyymmdd);
    }

    public void onCheckedChanged(long habitId, boolean checked) {
        Integer d = selectedDate.getValue();
        if (d == null) d = DateUtils.todayInt();
        repo.setChecked(habitId, d, checked);
    }

    public void deleteHabit(Habit habit) {
        repo.deleteHabit(habit);
    }

    private void switchLogsSource(int dateInt) {
        if (currentLogsSource != null) {
            logsForSelectedDate.removeSource(currentLogsSource);
        }
        currentLogsSource = repo.observeLogsByDate(dateInt);
        logsForSelectedDate.addSource(currentLogsSource, logsForSelectedDate::setValue);
    }

    private void recompute() {
        Integer d = selectedDate.getValue();
        if (d == null) d = DateUtils.todayInt();

        LocalDate date = DateUtils.fromInt(d);

        Map<Long, HabitLog> logMap = new HashMap<>();
        for (HabitLog l : cachedLogs) {
            logMap.put(l.habitId, l);
        }

        List<DueHabit> out = new ArrayList<>();
        for (Habit h : cachedHabits) {
            if (!ScheduleUtils.isDueToday(h, date)) continue;

            HabitLog log = logMap.get(h.id);
            boolean defaultChecked = (h.type == HabitType.BAD);
            boolean checked = (log != null) ? log.checked : defaultChecked;

            out.add(new DueHabit(h, checked));
        }

        dueHabits.setValue(out);
    }
}