package com.smorodya.hobbymanager.ui;

import com.smorodya.hobbymanager.data.Habit;
import com.smorodya.hobbymanager.R;
import com.smorodya.hobbymanager.data.Interval;
import com.smorodya.hobbymanager.data.ScheduleMode;

public class SummaryFormatter {
    public static String format(android.content.Context ctx, Habit h) {
        String type = (h.type.name().equals("BAD")) ? ctx.getString(R.string.type_bad) : ctx.getString(R.string.type_good);

        if (h.scheduleMode == ScheduleMode.DAYS_OF_WEEK) {
            return type + " • " + ctx.getString(R.string.schedule_days_of_week);
        }

        if (h.interval == Interval.DAILY) return type + " • " + ctx.getString(R.string.schedule_daily);
        if (h.interval == Interval.WEEKLY) return type + " • " + ctx.getString(R.string.schedule_weekly);
        if (h.interval == Interval.MONTHLY) return type + " • " + ctx.getString(R.string.schedule_monthly);

        return type;
    }
}