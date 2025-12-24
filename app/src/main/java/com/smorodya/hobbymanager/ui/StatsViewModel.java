package com.smorodya.hobbymanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.smorodya.hobbymanager.data.AppDatabase;
import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.data.HabitDao;
import com.smorodya.hobbymanager.data.HabitLog;
import com.smorodya.hobbymanager.data.HabitLogDao;
import com.smorodya.hobbymanager.data.HabitType;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.smorodya.hobbymanager.logic.ScheduleUtils;
import com.smorodya.hobbymanager.logic.WeekUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsViewModel extends AndroidViewModel {

    private final HabitDao habitDao;
    private final HabitLogDao logDao;

    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<StatsPeriod> period = new MutableLiveData<>(StatsPeriod.DAY);
    private final MutableLiveData<Integer> baseDate = new MutableLiveData<>(DateUtils.todayInt());

    private final MediatorLiveData<StatsResult> stats = new MediatorLiveData<>();

    public StatsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        habitDao = db.habitDao();
        logDao = db.habitLogDao();

        stats.setValue(new StatsResult(0, 0));

        stats.addSource(period, p -> recalc());
        stats.addSource(baseDate, d -> recalc());

        recalc();
    }

    public LiveData<StatsResult> observeStats() {
        return stats;
    }

    public LiveData<StatsPeriod> observePeriod() {
        return period;
    }

    public void setPeriod(StatsPeriod p) {
        period.setValue(p);
    }

    public void setBaseDate(int yyyymmdd) {
        baseDate.setValue(yyyymmdd);
    }

    private void recalc() {
        StatsPeriod p = period.getValue();
        Integer dInt = baseDate.getValue();
        if (p == null) p = StatsPeriod.DAY;
        if (dInt == null) dInt = DateUtils.todayInt();

        final StatsPeriod periodFinal = p;
        final int baseDateFinal = dInt;

        io.execute(() -> {
            List<Habit> habits = habitDao.getAllSync();
            if (habits == null || habits.isEmpty()) {
                stats.postValue(new StatsResult(0, 0));
                return;
            }

            DateRange range = calcRange(periodFinal, DateUtils.fromInt(baseDateFinal), habits);
            LocalDate start = range.start;
            LocalDate end = range.end;

            int from = DateUtils.toInt(start);
            int to = DateUtils.toInt(end);

            List<HabitLog> logs = logDao.getLogsBetweenSync(from, to);

            Map<Long, Boolean> logMap = new HashMap<>();
            if (logs != null) {
                for (HabitLog l : logs) {
                    logMap.put(makeKey(l.habitId, l.date), l.checked);
                }
            }

            int done = 0;
            int total = 0;

            LocalDate cur = start;
            while (!cur.isAfter(end)) {
                int curInt = DateUtils.toInt(cur);

                for (Habit h : habits) {
                    if (!ScheduleUtils.isDueToday(h, cur)) continue;

                    total++;

                    Boolean checked = logMap.get(makeKey(h.id, curInt));

                    boolean value;
                    if (checked != null) {
                        value = checked;
                    } else {
                        value = (h.type == HabitType.BAD);
                    }

                    if (value) done++;
                }

                cur = cur.plusDays(1);
            }

            stats.postValue(new StatsResult(done, total));
        });
    }

    private DateRange calcRange(StatsPeriod p, LocalDate baseDate, List<Habit> habits) {
        switch (p) {
            case DAY: {
                return new DateRange(baseDate, baseDate);
            }
            case WEEK: {
                LocalDate monday = WeekUtils.mondayOfWeek(baseDate);
                return new DateRange(monday, monday.plusDays(6));
            }
            case MONTH: {
                LocalDate start = baseDate.withDayOfMonth(1);
                LocalDate end = baseDate.withDayOfMonth(baseDate.lengthOfMonth());
                return new DateRange(start, end);
            }
            case ALL:
            default: {
                LocalDate end = LocalDate.now();
                int min = DateUtils.toInt(end);
                for (Habit h : habits) {
                    if (h.startDate > 0 && h.startDate < min) min = h.startDate;
                }
                LocalDate start = DateUtils.fromInt(min);
                if (start.isAfter(end)) start = end;
                return new DateRange(start, end);
            }
        }
    }

    private static class DateRange {
        final LocalDate start;
        final LocalDate end;

        DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    private long makeKey(long habitId, int date) {
        return (habitId << 32) ^ (date & 0xffffffffL);
    }
}