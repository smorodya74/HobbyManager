package com.smorodya.hobbymanager.logic;


import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.data.Interval;
import com.smorodya.hobbymanager.data.ScheduleMode;

import java.time.LocalDate;
import java.time.YearMonth;

public class ScheduleUtils {

    // Пн=0 ... Вс=6
    public static int dayOfWeekBit(LocalDate date) {
        int dow = date.getDayOfWeek().getValue(); // 1..7
        return 1 << (dow - 1);
    }

    public static boolean isDueToday(Habit habit, LocalDate date) {
        if (habit.scheduleMode == ScheduleMode.DAYS_OF_WEEK) {
            int bit = dayOfWeekBit(date);
            return (habit.daysOfWeekMask & bit) != 0;
        }

        if (habit.interval == Interval.DAILY) return true;

        LocalDate start = DateUtils.fromInt(habit.startDate);

        if (habit.interval == Interval.WEEKLY) {
            return date.getDayOfWeek() == start.getDayOfWeek();
        }

        if (habit.interval == Interval.MONTHLY) {
            int day = start.getDayOfMonth();
            YearMonth ym = YearMonth.of(date.getYear(), date.getMonth());
            int lastDay = ym.lengthOfMonth();
            int target = Math.min(day, lastDay);
            return date.getDayOfMonth() == target;
        }

        return false;
    }
}