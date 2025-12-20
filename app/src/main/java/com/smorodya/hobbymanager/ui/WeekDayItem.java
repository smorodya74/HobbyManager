package com.smorodya.hobbymanager.ui;

import java.time.LocalDate;

public class WeekDayItem {
    public final LocalDate date;
    public final boolean isToday;
    public final boolean isSelected;

    public WeekDayItem(LocalDate date, boolean isToday, boolean isSelected) {
        this.date = date;
        this.isToday = isToday;
        this.isSelected = isSelected;
    }
}