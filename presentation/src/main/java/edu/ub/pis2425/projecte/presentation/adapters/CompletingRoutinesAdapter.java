package edu.ub.pis2425.projecte.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;

public class CompletingRoutinesAdapter extends RecyclerView.Adapter<CompletingRoutinesAdapter.RoutineViewHolder> {

    private List<Routine> routines = new ArrayList<>();
    private OnCardClickListener onCardClickListener;
    private Map<String, Boolean> checkVisibilityMap = new HashMap<>();
    private int compareTo;


    public void setCompareTo(int compareTo) {
        this.compareTo = compareTo;
    }

    public interface OnCardClickListener {
        void onCardClick(Routine routine);
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.onCardClickListener = listener;
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines != null ? routines : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCheckVisible(List<String> routineIds) {
        // Clear previous visibility settings
        checkVisibilityMap.clear();
        // Set true for routines in the provided list
        if (routineIds != null) {
            for (String routineId : routineIds) {
                checkVisibilityMap.put(routineId, true);
            }
        }
        notifyDataSetChanged(); // Refresh all items to update checkmark visibility
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completing_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    // En RoutineAdapter.java
    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.bind(routine, onCardClickListener);

        Boolean isCheckVisible = checkVisibilityMap.getOrDefault(routine.getId().toString(), false);
        if (isCheckVisible && compareTo == 3) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(routine);
                }
            });

        } else if (isCheckVisible) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Ya has completado esta rutina", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.imgCheck.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(routine);
                }
            });
        }
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageView imgCheck;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgCheck = itemView.findViewById(R.id.imgCheck);
        }

        void bind(Routine routine, OnCardClickListener cardListener) {
            tvName.setText(routine.getName());
            tvDescription.setText(routine.getDescription());

            itemView.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && cardListener != null) {
                    cardListener.onCardClick(routine);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }
}
