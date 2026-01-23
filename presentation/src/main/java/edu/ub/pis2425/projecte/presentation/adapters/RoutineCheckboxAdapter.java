package edu.ub.pis2425.projecte.presentation.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Routine;

public class RoutineCheckboxAdapter extends RecyclerView.Adapter<RoutineCheckboxAdapter.RoutineViewHolder> {

    private final List<Routine> routines = new ArrayList<>();
    private final List<Routine> selectedRoutines = new ArrayList<>();



    public void setRoutines(List<Routine> routines) {
        this.routines.clear();
        this.routines.addAll(routines);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedRoutines(List<Routine> routines) {
        this.selectedRoutines.clear();
        this.selectedRoutines.addAll(routines);
        notifyDataSetChanged();
    }

    public List<Routine> getSelectedRoutines() {
        return selectedRoutines;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine_select, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.tvName.setText(routine.getName());
        holder.tvDescription.setText(routine.getDescription());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedRoutines.contains(routine));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedRoutines.add(routine);
            } else {
                selectedRoutines.remove(routine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        CheckBox checkBox;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            checkBox = itemView.findViewById(R.id.checkboxExercise);
        }
    }
}