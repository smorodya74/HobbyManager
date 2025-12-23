package com.smorodya.hobbymanager.ui;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smorodya.hobbymanager.R;
import com.smorodya.hobbymanager.databinding.ActivityStatsBinding;

import java.util.ArrayList;

public class StatsActivity extends ComponentActivity {

    private ActivityStatsBinding binding;
    private StatsViewModel vm;

    private PeriodAdapter periodAdapter;
    private StatsPeriod selected = StatsPeriod.DAY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(StatsViewModel.class);

        periodAdapter = new PeriodAdapter(p -> {
            selected = p;
            submitPeriods(selected);
            vm.setPeriod(p);
        });

        binding.periodList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.periodList.setAdapter(periodAdapter);

        submitPeriods(selected);

        vm.observeStats().observe(this, r -> {
            binding.tvDone.setText(getString(R.string.done_of_total, r.done, r.total));
            binding.donut.setData(r.done, r.total);
        });
    }

    private void submitPeriods(StatsPeriod selected) {
        ArrayList<PeriodItem> items = new ArrayList<>();
        items.add(new PeriodItem(StatsPeriod.DAY, "За день", selected == StatsPeriod.DAY));
        items.add(new PeriodItem(StatsPeriod.WEEK, "За неделю", selected == StatsPeriod.WEEK));
        items.add(new PeriodItem(StatsPeriod.MONTH, "За месяц", selected == StatsPeriod.MONTH));
        items.add(new PeriodItem(StatsPeriod.ALL, "Всего", selected == StatsPeriod.ALL));
        periodAdapter.submitList(items);
    }
}
