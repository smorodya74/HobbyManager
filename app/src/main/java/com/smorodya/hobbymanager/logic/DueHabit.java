package com.smorodya.hobbymanager.logic;

import com.smorodya.hobbymanager.data.Habit;

public class DueHabit {
    public final Habit habit;
    public final boolean checked;

    public DueHabit(Habit habit, boolean checked) {
        this.habit = habit;
        this.checked = checked;
    }
}