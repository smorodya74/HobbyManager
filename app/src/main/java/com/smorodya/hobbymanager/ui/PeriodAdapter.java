package com.smorodya.hobbymanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import com.google.android.material.color.MaterialColors;
import com.smorodya.hobbymanager.databinding.ItemPeriodBinding;

public class PeriodAdapter extends ListAdapter<PeriodItem, PeriodAdapter.VH> {

    public interface Listener {
        void onClick(StatsPeriod p);
    }

    private final Listener listener;

    public PeriodAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    static final DiffUtil.ItemCallback<PeriodItem> DIFF = new DiffUtil.ItemCallback<>() {
        @Override public boolean areItemsTheSame(@NonNull PeriodItem a, @NonNull PeriodItem b) {
            return a.period == b.period;
        }
        @Override public boolean areContentsTheSame(@NonNull PeriodItem a, @NonNull PeriodItem b) {
            return a.isSelected == b.isSelected && a.title.equals(b.title);
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPeriodBinding b = ItemPeriodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PeriodItem item = getItem(position);

        h.b.tvTitle.setText(item.title);

        int onSurface = MaterialColors.getColor(h.itemView, com.google.android.material.R.attr.colorOnSurface);
        h.b.tvTitle.setTextColor(onSurface);

        h.b.underline.setVisibility(item.isSelected ? View.VISIBLE : View.INVISIBLE);

        h.itemView.setOnClickListener(v -> listener.onClick(item.period));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemPeriodBinding b;
        VH(ItemPeriodBinding b) { super(b.getRoot()); this.b = b; }
    }
}
