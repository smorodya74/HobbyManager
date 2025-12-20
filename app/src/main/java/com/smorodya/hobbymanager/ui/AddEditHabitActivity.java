package com.smorodya.hobbymanager.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.smorodya.hobbymanager.R;
import com.smorodya.hobbymanager.data.*;
import com.smorodya.hobbymanager.databinding.ActivityAddEditHabitBinding;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.google.android.material.chip.Chip;

public class AddEditHabitActivity extends ComponentActivity {

    public static final String EXTRA_HABIT_ID = "habit_id";

    private ActivityAddEditHabitBinding binding;
    private AddEditHabitViewModel viewModel;

    private long editingId = -1;
    private Habit editingHabit = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AddEditHabitViewModel.class);

        initDaysChips();
        initScheduleUi();

        editingId = getIntent().getLongExtra(EXTRA_HABIT_ID, -1);

        if (editingId > 0) {
            binding.toolbar.setTitle(getString(R.string.edit_habit));
            viewModel.observeHabit(editingId).observe(this, habit -> {
                if (habit == null) return;
                editingHabit = habit;
                fillForm(habit);
            });
        } else {
            binding.toolbar.setTitle(getString(R.string.add_habit));
            binding.rbDaily.setChecked(true);
        }

        binding.btnSave.setOnClickListener(v -> onSave());
    }

    private void initScheduleUi() {
        binding.rgSchedule.setOnCheckedChangeListener((g, checkedId) -> {
            boolean days = checkedId == R.id.rbDays;
            binding.chipsDays.setVisibility(days ? View.VISIBLE : View.GONE);
        });

        // switch: true=полезная, false=вредная
        binding.swType.setChecked(true);
        binding.swType.setText(getString(R.string.type_good));
        binding.swType.setOnCheckedChangeListener((btn, isChecked) -> {
            binding.swType.setText(getString(isChecked ? R.string.type_good : R.string.type_bad));
        });
    }

    private void initDaysChips() {
        String[] days = getResources().getStringArray(R.array.week_short);
        for (String d : days) {
            Chip c = new Chip(this);
            c.setText(d);
            c.setCheckable(true);
            binding.chipsDays.addView(c);
        }
    }

    private void fillForm(Habit h) {
        binding.etTitle.setText(h.title);

        boolean isGood = h.type == HabitType.GOOD;
        binding.swType.setChecked(isGood);
        binding.swType.setText(getString(isGood ? R.string.type_good : R.string.type_bad));

        if (h.scheduleMode == ScheduleMode.DAYS_OF_WEEK) {
            binding.rbDays.setChecked(true);
            binding.chipsDays.setVisibility(View.VISIBLE);
            applyMaskToChips(h.daysOfWeekMask);
        } else {
            binding.chipsDays.setVisibility(View.GONE);
            if (h.interval == Interval.DAILY) binding.rbDaily.setChecked(true);
            else if (h.interval == Interval.WEEKLY) binding.rbWeekly.setChecked(true);
            else binding.rbMonthly.setChecked(true);
        }
    }

    private void applyMaskToChips(int mask) {
        for (int i = 0; i < binding.chipsDays.getChildCount(); i++) {
            View v = binding.chipsDays.getChildAt(i);
            if (v instanceof Chip) {
                int bit = 1 << i; // Пн=0 ... Вс=6
                ((Chip) v).setChecked((mask & bit) != 0);
            }
        }
    }

    private int readMaskFromChips() {
        int mask = 0;
        for (int i = 0; i < binding.chipsDays.getChildCount(); i++) {
            View v = binding.chipsDays.getChildAt(i);
            if (v instanceof Chip && ((Chip) v).isChecked()) {
                mask |= (1 << i);
            }
        }
        return mask;
    }

    private void onSave() {
        String title = binding.etTitle.getText() == null ? "" : binding.etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            binding.etTitle.setError(getString(R.string.enter_title));
            return;
        }

        HabitType type = binding.swType.isChecked() ? HabitType.GOOD : HabitType.BAD;

        int checkedId = binding.rgSchedule.getCheckedRadioButtonId();

        ScheduleMode scheduleMode;
        int mask = 0;
        Interval interval = Interval.DAILY;

        if (checkedId == R.id.rbDays) {
            scheduleMode = ScheduleMode.DAYS_OF_WEEK;
            mask = readMaskFromChips();
            if (mask == 0) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.schedule_label)
                        .setMessage(R.string.select_one_day_message)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                return;
            }
        } else {
            scheduleMode = ScheduleMode.INTERVAL;
            if (checkedId == R.id.rbDaily) interval = Interval.DAILY;
            else if (checkedId == R.id.rbWeekly) interval = Interval.WEEKLY;
            else interval = Interval.MONTHLY;
        }

        int startDate = (editingHabit != null) ? editingHabit.startDate : DateUtils.todayInt();
        long createdAt = (editingHabit != null) ? editingHabit.createdAtMillis : System.currentTimeMillis();

        Habit h = new Habit(title, type, scheduleMode, mask, interval, startDate, createdAt);
        if (editingHabit != null) h.id = editingHabit.id;

        if (editingHabit == null) {
            viewModel.repo().insertHabit(
                    h,
                    this::finish,
                    this::showLimitReached
            );
        } else {
            viewModel.repo().updateHabit(h, this::finish);
        }
    }

    private void showLimitReached() {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle(R.string.limit_reached_title)
                .setMessage(R.string.limit_reached_message)
                .setPositiveButton(R.string.ok, null)
                .show());
    }
}