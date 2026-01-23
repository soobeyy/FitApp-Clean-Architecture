package edu.ub.pis2425.projecte.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.DayViewHolder> {

    private List<Day> days = new ArrayList<>();
    private HashMap<Day, List<Routine>> dayRoutineMap = new HashMap<>();
    private OnCardClickListener onCardClickListener;
    private OnAddClickListener onAddClickListener;

    // Mapa para convertir abreviaturas en inglés a nombres completos en español
    private static final Map<String, String> DAY_NAME_MAP = new HashMap<>();
    static {
        DAY_NAME_MAP.put("mon", "Lunes");
        DAY_NAME_MAP.put("tue", "Martes");
        DAY_NAME_MAP.put("wed", "Miércoles");
        DAY_NAME_MAP.put("thu", "Jueves");
        DAY_NAME_MAP.put("fri", "Viernes");
        DAY_NAME_MAP.put("sat", "Sábado");
        DAY_NAME_MAP.put("sun", "Domingo");
    }

    public interface OnCardClickListener {
        void onCardClick(Day day);
    }

    public interface OnAddClickListener {
        void onAddClick(Day day);
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        this.onCardClickListener = listener;
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        this.onAddClickListener = listener;
    }

    public void setDays(List<Day> days) {
        this.days = days;
        notifyDataSetChanged();
    }

    public void setDayRoutineMap(HashMap<Day, List<Routine>> dayRoutineMap) {
        this.dayRoutineMap = dayRoutineMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);
        // Convertir el nombre del día a español completo
        String dayName = DAY_NAME_MAP.getOrDefault(day.getName().toLowerCase(), day.getName());
        holder.tvDayName.setText(dayName); // e.g., "Lunes"
        holder.tvDayNumber.setText(String.valueOf(day.getDate().getDayOfMonth())); // e.g., "11"

        List<Routine> routines = dayRoutineMap.getOrDefault(day, new ArrayList<>());
        String routineNames = routines.stream()
                .map(Routine::getName)
                .limit(3) // Limitar a 3 nombres para evitar desbordamiento
                .collect(Collectors.joining(", "));
        holder.tvDayDesc.setText(routineNames.isEmpty() ? "Sin rutinas" :
                routineNames + (routines.size() > 3 ? "..." : ""));

        holder.itemView.setOnClickListener(v -> {
            if (onCardClickListener != null) {
                onCardClickListener.onCardClick(day);
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (onAddClickListener != null) {
                onAddClickListener.onAddClick(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayDesc, tvDayNumber;
        ImageButton btnAdd;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvName);
            tvDayDesc = itemView.findViewById(R.id.tvDescription);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            btnAdd = itemView.findViewById(R.id.btnDelete);
        }
    }
}