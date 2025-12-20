package com.smorodya.hobbymanager.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.smorodya.hobbymanager.databinding.ActivityMainBinding;
import com.smorodya.hobbymanager.logic.DueHabit;

public class MainActivity extends ComponentActivity implements HabitAdapter.Listener {

    private ActivityMainBinding binding;
    private MainViewModel vm;
    private HabitAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new HabitAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        vm.observeDueHabits().observe(this, dueHabits -> adapter.submitList(dueHabits));

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditHabitActivity.class)));

        attachSwipeToDelete();
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback cb = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getBindingAdapterPosition();
                DueHabit item = adapter.getCurrentList().get(pos);
                vm.deleteHabit(item.habit);
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