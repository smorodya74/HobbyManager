package com.smorodya.hobbymanager.data;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static String fromHabitType(HabitType value) {
        return value == null ? null : value.name();
    }

    @TypeConverter
    public static HabitType toHabitType(String value) {
        return value == null ? HabitType.GOOD : HabitType.valueOf(value);
    }

    @TypeConverter
    public static String fromScheduleMode(ScheduleMode value) {
        return value == null ? null : value.name();
    }

    @TypeConverter
    public static ScheduleMode toScheduleMode(String value) {
        return value == null ? ScheduleMode.INTERVAL : ScheduleMode.valueOf(value);
    }

    @TypeConverter
    public static String fromInterval(Interval value) {
        return value == null ? null : value.name();
    }

    @TypeConverter
    public static Interval toInterval(String value) {
        return value == null ? Interval.DAILY : Interval.valueOf(value);
    }
}