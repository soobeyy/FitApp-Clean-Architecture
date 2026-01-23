package edu.ub.pis2425.projecte.presentation.ui.fragments.routines;

import android.os.Bundle;
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

import java.util.ArrayList;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentRoutineDetailBinding;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.ExerciseAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.ModifyRoutineViewModel;

public class RoutineDetailFragment extends Fragment {

    private FragmentRoutineDetailBinding binding;
    private ModifyRoutineViewModel viewModel;
    private NavController navController;
    private RoutineId routineId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutineDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        String idStr = requireArguments().getString("routineId");
        routineId = new RoutineId(idStr);

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ModifyRoutineViewModel.class);

        // Limpia el estado de éxito de edición al cargar la vista
        viewModel.clearCreateSuccess();

        ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        exerciseAdapter.setHideActionButtons(true); // Ocultar botones
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(exerciseAdapter);

        // Configurar el listener para navegar al detalle del ejercicio
        exerciseAdapter.setOnCardClickListener(exercise -> {
            Bundle args = new Bundle();
            args.putString("exerciseId", exercise.getId().toString());
            args.putBoolean("viewButton", false); // No mostrar el botón de editar
            navController.navigate(R.id.action_routineDetailFragment_to_exerciseDetailFragment, args);
        });

        viewModel.getRoutine().observe(getViewLifecycleOwner(), routine -> {
            if (routine == null) return;
            binding.tvRoutineName.setText(routine.getName());
            binding.tvRoutineDescription.setText(routine.getDescription());
            if (routine.getExercises() != null && !routine.getExercises().isEmpty()) {
                exerciseAdapter.setExercises(routine.getExercises());
            } else {
                exerciseAdapter.setExercises(new ArrayList<>()); // Lista vacía
            }
        });

        viewModel.loadRoutine(routineId);
    }

    private void initWidgetListeners() {
        binding.btnEditRoutine.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("routineId", routineId.toString());
            navController.navigate(R.id.action_routineDetailFragment_to_modifyRoutineFragment, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}