package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentShowValExerciseBinding;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.SeriesAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateExerciseViewModel;

public class ShowValExerciseFragment extends Fragment {

    private FragmentShowValExerciseBinding binding;
    private RateExerciseViewModel viewModel;
    private NavController navController;
    private SeriesAdapter seriesAdapter;
    private ExerciseId exerciseId;
    private String dayId;
    private String routineId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShowValExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Obtener argumentos
        exerciseId = new ExerciseId(requireArguments().getString("exerciseId"));
        dayId = requireArguments().getString("dayId");
        routineId = requireArguments().getString("routineId");

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RateExerciseViewModel.class);

        seriesAdapter = new SeriesAdapter();
        binding.rvSeries.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSeries.setAdapter(seriesAdapter);

        // Observar los datos del ejercicio
        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise == null) return;
            binding.tvExerciseName.setText(exercise.getName());
            binding.tvExerciseDescription.setText(exercise.getDescription());

            // Obtener las series para el día especificado
            List<Serie> series = exercise.getValoracion().getOrDefault(dayId, new ArrayList<>());
            seriesAdapter.setSeries(series);

            // Cargar la imagen del ejercicio
            if (!TextUtils.isEmpty(exercise.getImageUri())) {
                Picasso.get()
                        .load(exercise.getImageUri())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(binding.ivExerciseImage);
            } else {
                binding.ivExerciseImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        });

        // Cargar el ejercicio
        viewModel.loadExercise(exerciseId);
    }

    private void initWidgetListeners() {
        binding.flImageContainer.setOnClickListener(v -> {
            // Implementar lógica para seleccionar una imagen si es necesario
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}