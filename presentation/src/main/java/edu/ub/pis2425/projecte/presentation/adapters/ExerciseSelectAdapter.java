package edu.ub.pis2425.projecte.presentation.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;

public class ExerciseSelectAdapter extends RecyclerView.Adapter<ExerciseSelectAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private List<Exercise> selectedExercises; // Lista para reflejar los ejercicios seleccionados
    private OnExerciseSelectedListener listener;
    private RoutineId routineId = null;

    public ExerciseSelectAdapter(List<Exercise> exercises, List<Exercise> selectedExercises, OnExerciseSelectedListener listener) {
        this.exercises = exercises;
        this.selectedExercises = selectedExercises; // Lista de ejercicios seleccionados
        this.listener = listener;
    }

    public ExerciseSelectAdapter(List<Exercise> exercises, List<Exercise> selectedExercises, OnExerciseSelectedListener listener, String RoutineId) {
        this.exercises = exercises;
        this.selectedExercises = selectedExercises; // Lista de ejercicios seleccionados
        this.listener = listener;
        this.routineId = new RoutineId(RoutineId);
    }

    // Actualizar la lista de ejercicios
    @SuppressLint("NotifyDataSetChanged")
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    // Actualizar la lista de ejercicios seleccionados
    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedExercises(List<Exercise> selectedExercises) {
        this.selectedExercises = selectedExercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_select, parent, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, selectedExercises.contains(exercise));

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

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvDescription;
        private final CheckBox checkBoxExercise;
        private final ImageView ivExerciseImage;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            checkBoxExercise = itemView.findViewById(R.id.checkboxExercise);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
        }

        public void bind(Exercise exercise, boolean isSelected) {
            tvName.setText(exercise.getName());
            tvDescription.setText(exercise.getDescription());

            checkBoxExercise.setOnCheckedChangeListener(null); // Evita múltiples triggers
            checkBoxExercise.setChecked(isSelected);

            checkBoxExercise.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onExerciseSelected(exercise, isChecked);
            });
        }
    }

    public interface OnExerciseSelectedListener {
        void onExerciseSelected(Exercise exercise, boolean isSelected);
    }
}