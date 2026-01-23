package edu.ub.pis2425.projecte.presentation.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.ItemSerieBinding;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.entities.Sensation;
import java.util.ArrayList;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private List<Serie> series = new ArrayList<>();

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSerieBinding binding = ItemSerieBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SeriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        Serie serie = series.get(position);
        holder.binding.tvWeight.setText(
                holder.itemView.getContext().getString(R.string.weight_format, serie.getWeight()));
        holder.binding.tvRepetitions.setText(
                holder.itemView.getContext().getString(R.string.repetitions_format, serie.getRepetitions()));

        int sensationDrawable;
        switch (serie.getSensation()) {
            case SOBRADO:
                sensationDrawable = R.drawable.green;
                break;
            case NORMAL:
                sensationDrawable = R.drawable.yellow;
                break;
            case JUSTITO:
                sensationDrawable = R.drawable.red;
                break;
            default:
                sensationDrawable = R.drawable.red; // Fallback for unexpected values
                break;
        }
        holder.binding.ivSensation.setImageResource(sensationDrawable);
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public void setSeries(List<Serie> series) {
        this.series = series != null ? series : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        final ItemSerieBinding binding;

        SeriesViewHolder(@NonNull ItemSerieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}