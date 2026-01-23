package edu.ub.pis2425.projecte.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Routine;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private List<Routine> routines = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;
    private OnCardClickListener onCardClickListener;
    private OnShareClickListener onShareClickListener;
    private boolean hideActionButtons = false; // Nueva bandera

    public interface OnDeleteClickListener {
        void onDeleteClick(Routine routine);
    }

    public interface OnCardClickListener {
        void onCardClick(Routine routine);
    }

    public interface OnShareClickListener {
        void onShareClick(Routine routine);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.onCardClickListener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines != null ? routines : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setHideActionButtons(boolean hideActionButtons) {
        this.hideActionButtons = hideActionButtons;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    // En RoutineAdapter.java
    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.bind(routine, onDeleteClickListener, onCardClickListener, onShareClickListener);

        if (hideActionButtons) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnShare.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnShare.setVisibility(View.VISIBLE);
        }
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageButton btnDelete;
        ImageView btnShare;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnShare = itemView.findViewById(R.id.btnShare);
        }

        void bind(Routine routine, OnDeleteClickListener deleteListener, OnCardClickListener cardListener, OnShareClickListener onShareClickListener) {
            tvName.setText(routine.getName());
            tvDescription.setText(routine.getDescription());

            btnDelete.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onDeleteClick(routine);
                }
            });

            itemView.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && cardListener != null) {
                    cardListener.onCardClick(routine);
                }
            });
            
            btnShare.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onShareClickListener != null) {
                    onShareClickListener.onShareClick(routine);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }
}
