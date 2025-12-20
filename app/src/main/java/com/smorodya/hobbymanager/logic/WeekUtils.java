package com.smorodya.hobbymanager.logic;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class WeekUtils {
    public static LocalDate mondayOfWeek(LocalDate anyDate) {
        return anyDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}