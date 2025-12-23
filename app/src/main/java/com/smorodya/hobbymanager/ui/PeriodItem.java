package com.smorodya.hobbymanager.ui;

public class PeriodItem {
    public final StatsPeriod period;
    public final String title;
    public final boolean isSelected;

    public PeriodItem(StatsPeriod period, String title, boolean isSelected) {
        this.period = period;
        this.title = title;
        this.isSelected = isSelected;
    }
}
