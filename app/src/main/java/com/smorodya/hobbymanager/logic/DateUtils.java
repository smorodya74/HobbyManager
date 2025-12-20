package com.smorodya.hobbymanager.logic;


import java.time.LocalDate;

public class DateUtils {

    public static int todayInt() {
        return toInt(LocalDate.now());
    }

    public static int toInt(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    public static LocalDate fromInt(int yyyymmdd) {
        int y = yyyymmdd / 10000;
        int m = (yyyymmdd / 100) % 100;
        int d = yyyymmdd % 100;
        return LocalDate.of(y, m, d);
    }
}