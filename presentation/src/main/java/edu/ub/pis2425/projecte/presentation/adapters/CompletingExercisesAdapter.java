package edu.ub.pis2425.projecte.presentation.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Exercise;

public class CompletingExercisesAdapter extends RecyclerView.Adapter<CompletingExercisesAdapter.CompletingExercisesViewHolder> {

    private List<Exercise> exercises = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;
    private OnCardClickListener onCardClickListener;
    private OnShareClickListener onShareClickListener;
    private Map<String, Boolean> checkVisibilityMap = new HashMap<>();
    private int compareTo;

    public void setCompareTo(int compareTo) {
        this.compareTo = compareTo;
    }

    public interface OnShareClickListener {
        void onShareClick(Exercise exercise);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Exercise exercise);
    }

    public interface OnCardClickListener {
        void onCardClick(Exercise exercise);
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.onCardClickListener = listener;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Updated method to handle a list of exercise IDs
    public void setCheckVisible(List<String> exerciseIds) {
        // Clear previous visibility settings
        checkVisibilityMap.clear();
        // Set true for exercises in the provided list
        if (exerciseIds != null) {
            for (String exerciseId : exerciseIds) {
                checkVisibilityMap.put(exerciseId, true);
            }
        }
        notifyDataSetChanged(); // Refresh all items to update checkmark visibility
    }

    @NonNull
    @Override
    public CompletingExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completing_exercise, parent, false);
        return new CompletingExercisesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletingExercisesViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvName.setText(exercise.getName());
        holder.tvDescription.setText(exercise.getDescription());

        // Configure checkmark visibility based on the map
        Boolean isCheckVisible = checkVisibilityMap.getOrDefault(exercise.getId().toString(), false);
        if (isCheckVisible && compareTo == 3) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(exercise);
                }
            });

        } else if (isCheckVisible) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Ya has completado este ejercicio", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.imgCheck.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(exercise);
                }
            });
        }

        if (exercise.getImageUri() != null && !exercise.getImageUri().isEmpty()) {
            Uri imageUri = Uri.parse(exercise.getImageUri());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivExerciseImage);
        } else {
            holder.ivExerciseImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class CompletingExercisesViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageView ivExerciseImage;
        ImageView imgCheck;

        public CompletingExercisesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            imgCheck = itemView.findViewById(R.id.imgCheck);
        }
    }
}