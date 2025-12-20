package com.smorodya.hobbymanager.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import com.smorodya.hobbymanager.databinding.ItemHabitBinding;
import com.smorodya.hobbymanager.logic.DueHabit;

public class HabitAdapter extends ListAdapter<DueHabit, HabitAdapter.VH> {

    public interface Listener {
        void onChecked(long habitId, boolean checked);
        void onEdit(long habitId);
    }

    private final Listener listener;

    public HabitAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    static final DiffUtil.ItemCallback<DueHabit> DIFF = new DiffUtil.ItemCallback<>() {
        @Override public boolean areItemsTheSame(@NonNull DueHabit a, @NonNull DueHabit b) {
            return a.habit.id == b.habit.id;
        }
        @Override public boolean areContentsTheSame(@NonNull DueHabit a, @NonNull DueHabit b) {
            return a.checked == b.checked && a.habit.title.equals(b.habit.title);
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHabitBinding b = ItemHabitBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DueHabit item = getItem(position);

        h.b.title.setText(item.habit.title);
        h.b.subtitle.setText(SummaryFormatter.format(h.itemView.getContext(), item.habit));

        // Важно: сначала обнуляем слушатель (RecyclerView реюзит ViewHolder)
        h.b.check.setOnCheckedChangeListener(null);
        h.b.check.setChecked(item.checked);

        h.b.check.setOnCheckedChangeListener((btn, isChecked) ->
                listener.onChecked(item.habit.id, isChecked));

        h.itemView.setOnClickListener(v -> listener.onEdit(item.habit.id));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemHabitBinding b;
        VH(ItemHabitBinding b) { super(b.getRoot()); this.b = b; }
    }
}