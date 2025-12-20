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

import com.smorodya.hobbymanager.databinding.ActivityMainBinding;
import com.smorodya.hobbymanager.logic.DateUtils;
import com.smorodya.hobbymanager.logic.DueHabit;
import com.smorodya.hobbymanager.logic.WeekUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends ComponentActivity implements HabitAdapter.Listener {

    private ActivityMainBinding binding;
    private MainViewModel vm;

    private HabitAdapter habitsAdapter;
    private WeekDaysAdapter weekAdapter;

    private LocalDate selectedDate = LocalDate.now();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(MainViewModel.class);

        // --- Week row ---
        weekAdapter = new WeekDaysAdapter(item -> {
            selectedDate = item.date;

            submitCurrentWeek(selectedDate);

            vm.setSelectedDate(DateUtils.toInt(selectedDate));
        });

        binding.weekList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.weekList.setAdapter(weekAdapter);

        vm.setSelectedDate(DateUtils.toInt(selectedDate));
        submitCurrentWeek(selectedDate);

        // --- Habits list ---
        habitsAdapter = new HabitAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(habitsAdapter);

        vm.observeDueHabits().observe(this, dueHabits -> habitsAdapter.submitList(dueHabits));

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditHabitActivity.class)));

        attachSwipeToDelete();
    }

    private void submitCurrentWeek(LocalDate selected) {
        LocalDate today = LocalDate.now();
        LocalDate monday = WeekUtils.mondayOfWeek(today);

        ArrayList<WeekDayItem> items = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            items.add(new WeekDayItem(d, d.equals(today), d.equals(selected)));
        }
        weekAdapter.submitList(items);
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback cb = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getBindingAdapterPosition();
                DueHabit item = habitsAdapter.getCurrentList().get(pos);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Удалить привычку?")
                        .setMessage("Привычка будет удалена.")
                        .setPositiveButton("Удалить", (d, w) -> vm.deleteHabit(item.habit))
                        .setNegativeButton("Отмена", (d, w) -> habitsAdapter.notifyItemChanged(pos))
                        .setOnCancelListener(d -> habitsAdapter.notifyItemChanged(pos))
                        .show();
            }
        };

        new ItemTouchHelper(cb).attachToRecyclerView(binding.list);
    }

    @Override
    public void onChecked(long habitId, boolean checked) {
        vm.onCheckedChanged(habitId, checked);
    }

    @Override
    public void onEdit(long habitId) {
        Intent i = new Intent(this, AddEditHabitActivity.class);
        i.putExtra(AddEditHabitActivity.EXTRA_HABIT_ID, habitId);
        startActivity(i);
    }
}