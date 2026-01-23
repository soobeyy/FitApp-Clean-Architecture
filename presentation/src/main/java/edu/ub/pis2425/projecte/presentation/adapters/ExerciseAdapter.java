package edu.ub.pis2425.projecte.presentation.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Exercise;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;
    private OnCardClickListener onCardClickListener; // Nuevo listener para el CardView
    private OnShareClickListener onShareClickListener; // Nuevo listener para el botón de compartir
    private boolean hideActionButtons = false; // Nueva bandera

    // Interfaz para el listener de eliminación
    public interface OnShareClickListener {
        void onShareClick(Exercise exercise);
    }

    // Interfaz para el listener de eliminación
    public interface OnDeleteClickListener {
        void onDeleteClick(Exercise exercise);
    }

    // Interfaz para el listener del CardView
    public interface OnCardClickListener {
        void onCardClick(Exercise exercise);
    }

    // Setter para el listener de compartir
    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    // Setter para el listener de eliminación
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    // Setter para el listener del CardView
    public void setOnCardClickListener(OnCardClickListener listener) {
        this.onCardClickListener = listener;
    }

    // Actualizar la lista de ejercicios
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setHideActionButtons(boolean hideActionButtons) {
        this.hideActionButtons = hideActionButtons;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvName.setText(exercise.getName());
        holder.tvDescription.setText(exercise.getDescription());

        // Controlar la visibilidad de los botones
        if (hideActionButtons) {
            holder.btnShare.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnShare.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
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

        // Listener para el botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(exercise);
            }
        });

        // Listener para el botón de compartir
        holder.btnShare.setOnClickListener(v -> {
            if (onShareClickListener != null) {
                onShareClickListener.onShareClick(exercise);
            }
        });

        // Listener para el CardView (toda la tarjeta)
        holder.itemView.setOnClickListener(v -> {
            if (onCardClickListener != null) {
                onCardClickListener.onCardClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageView ivExerciseImage;
        ImageButton btnDelete;
        ImageButton btnShare;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }

}