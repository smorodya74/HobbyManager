package com.smorodya.hobbymanager.ui;

import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.data.Interval;
import com.smorodya.hobbymanager.data.ScheduleMode;

public class SummaryFormatter {
    public static String format(Habit h) {
        String type = (h.type.name().equals("BAD")) ? "Вредная" : "Полезная";

        if (h.scheduleMode == ScheduleMode.DAYS_OF_WEEK) {
            return type + " • Дни недели";
        }

        if (h.interval == Interval.DAILY) return type + " • Каждый день";
        if (h.interval == Interval.WEEKLY) return type + " • Раз в неделю";
        if (h.interval == Interval.MONTHLY) return type + " • Раз в месяц";

        return type;
    }
}