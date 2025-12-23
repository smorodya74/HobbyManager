package com.smorodya.hobbymanager.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smorodya.hobbymanager.R;
import com.smorodya.hobbymanager.databinding.ActivityMainBinding;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.smorodya.hobbymanager.logic.DueHabit;
import com.smorodya.hobbymanager.logic.WeekUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends ComponentActivity implements HabitAdapter.Listener {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private HabitAdapter habitsAdapter;
    private WeekAdapter weekAdapter;

    private LocalDate selectedDate = LocalDate.now();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupWeekRow();
        setupHabitsList();

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditHabitActivity.class)));

        attachSwipeToDelete();

        viewModel.setSelectedDate(DateUtils.toInt(selectedDate));
        updateWeek(selectedDate);
        updateTitle(selectedDate);

        viewModel.observeSelectedDate().observe(this, d -> {
            if (d == null) return;
            LocalDate date = DateUtils.fromInt(d);
            selectedDate = date;
            updateTitle(date);
        });

        binding.btnStats.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));
    }

    private void setupWeekRow() {
        weekAdapter = new WeekAdapter(item -> {
            selectedDate = item.date;

            updateWeek(selectedDate);
            updateTitle(selectedDate);

            viewModel.setSelectedDate(DateUtils.toInt(selectedDate));
        });

        binding.weekList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.weekList.setAdapter(weekAdapter);
    }

    private void setupHabitsList() {
        habitsAdapter = new HabitAdapter(this);

        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(habitsAdapter);

        viewModel.observeDueHabits().observe(this, dueHabits -> habitsAdapter.submitList(dueHabits));
    }

    private void updateWeek(LocalDate selected) {
        LocalDate today = LocalDate.now();
        LocalDate monday = WeekUtils.mondayOfWeek(today);

        ArrayList<DayItem> items = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            items.add(new DayItem(d, d.equals(today), d.equals(selected)));
        }
        weekAdapter.submitList(items);
    }

    private void updateTitle(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            binding.toolbar.setTitle(getString(R.string.habits_today));
        } else {
            String title = getString(R.string.main_title) + " • " +
                    String.format("%02d.%02d", date.getDayOfMonth(), date.getMonthValue());
            binding.toolbar.setTitle(title);
        }
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback cb = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getBindingAdapterPosition();
                DueHabit item = habitsAdapter.getCurrentList().get(pos);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.delete_habit_title))
                        .setMessage(getString(R.string.delete_habit_message))
                        .setPositiveButton(R.string.delete, (d, w) -> viewModel.deleteHabit(item.habit))
                        .setNegativeButton(R.string.cancel, (d, w) -> habitsAdapter.notifyItemChanged(pos))
                        .setOnCancelListener(d -> habitsAdapter.notifyItemChanged(pos))
                        .show();
            }
        };

        new ItemTouchHelper(cb).attachToRecyclerView(binding.list);
    }

    @Override
    public void onChecked(long habitId, boolean checked) {
        viewModel.onCheckedChanged(habitId, checked);
    }

    @Override
    public void onEdit(long habitId) {
        Intent i = new Intent(this, AddEditHabitActivity.class);
        i.putExtra(AddEditHabitActivity.EXTRA_HABIT_ID, habitId);
        startActivity(i);
    }
}
