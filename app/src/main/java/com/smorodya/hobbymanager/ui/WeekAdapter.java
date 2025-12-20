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

public class WeekAdapter extends ListAdapter<DayItem, WeekAdapter.VH> {

    public interface Listener {
        void onDayClick(DayItem item);
    }

    private final Listener listener;

    public WeekAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    static final DiffUtil.ItemCallback<DayItem> DIFF = new DiffUtil.ItemCallback<>() {
        @Override public boolean areItemsTheSame(@NonNull DayItem a, @NonNull DayItem b) {
            return a.date.equals(b.date);
        }
        @Override public boolean areContentsTheSame(@NonNull DayItem a, @NonNull DayItem b) {
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
        DayItem item = getItem(position);

        h.b.tvDay.setText(String.valueOf(item.date.getDayOfMonth()));

        int onSurface = MaterialColors.getColor(h.itemView, com.google.android.material.R.attr.colorOnSurface);
        int todayRed = ContextCompat.getColor(h.itemView.getContext(), R.color.today_red);

        if (item.isToday) {
            h.b.tvDay.setTextColor(todayRed);
        } else {
            h.b.tvDay.setTextColor(onSurface);
        }

        h.b.tvDay.setTypeface(null, Typeface.NORMAL);
        h.b.underline.setVisibility(item.isSelected ? View.VISIBLE : View.INVISIBLE);

        h.itemView.setOnClickListener(v -> listener.onDayClick(item));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemWeekDayBinding b;
        VH(ItemWeekDayBinding b) { super(b.getRoot()); this.b = b; }
    }
}
