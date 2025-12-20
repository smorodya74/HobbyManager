package com.smorodya.hobbymanager.ui;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.*;

import com.google.android.material.color.MaterialColors;
import com.smorodya.hobbymanager.R;
import com.smorodya.hobbymanager.databinding.ItemWeekDayBinding;

public class WeekDaysAdapter extends ListAdapter<WeekDayItem, WeekDaysAdapter.VH> {

    public interface Listener {
        void onDayClick(WeekDayItem item);
    }

    private final Listener listener;

    public WeekDaysAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    static final DiffUtil.ItemCallback<WeekDayItem> DIFF = new DiffUtil.ItemCallback<>() {
        @Override public boolean areItemsTheSame(@NonNull WeekDayItem a, @NonNull WeekDayItem b) {
            return a.date.equals(b.date);
        }
        @Override public boolean areContentsTheSame(@NonNull WeekDayItem a, @NonNull WeekDayItem b) {
            return a.isSelected == b.isSelected && a.isToday == b.isToday;
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeekDayBinding b = ItemWeekDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        WeekDayItem item = getItem(position);

        h.b.tvDay.setText(String.valueOf(item.date.getDayOfMonth()));

        int onSurface = MaterialColors.getColor(h.itemView, com.google.android.material.R.attr.colorOnSurface);
        int todayRed = ContextCompat.getColor(h.itemView.getContext(), R.color.today_red);

        // 1) Цвет: красный ТОЛЬКО у today
        if (item.isToday) {
            h.b.tvDay.setTextColor(todayRed);
        } else {
            h.b.tvDay.setTextColor(onSurface);
        }

        // 2) Выбранный день: ТОЛЬКО подчёркивание (без смены цвета/жирности)
        h.b.tvDay.setTypeface(null, Typeface.NORMAL);
        h.b.underline.setVisibility(item.isSelected ? View.VISIBLE : View.INVISIBLE);

        h.itemView.setOnClickListener(v -> listener.onDayClick(item));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemWeekDayBinding b;
        VH(ItemWeekDayBinding b) { super(b.getRoot()); this.b = b; }
    }
}