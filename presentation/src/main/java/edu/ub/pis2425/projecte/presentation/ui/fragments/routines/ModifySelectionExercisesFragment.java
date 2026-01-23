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

import edu.ub.pis2425.projecte.databinding.FragmentSelectExercisesBinding;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.ExerciseSelectAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ExerciseListViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.ModifyRoutineViewModel;

public class ModifySelectionExercisesFragment extends Fragment {

    private FragmentSelectExercisesBinding binding;
    private ExerciseSelectAdapter adapter;
    private ExerciseListViewModel exerciseListViewModel;
    private ModifyRoutineViewModel modifyRoutineViewModel;
    private NavController navController;
    private String routineId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectExercisesBinding.inflate(inflater, container, false);
        modifyRoutineViewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ModifyRoutineViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        String idStr = requireArguments().getString("routineId");
        routineId = idStr;

        initRecyclerView();
        initViewModels();
        initListeners();
    }

    private void initViewModels() {
        exerciseListViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ExerciseListViewModel.class);

        // Observa los ejercicios disponibles
        exerciseListViewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            adapter.setExercises(exercises);
        });

        // Observa los ejercicios seleccionados desde RoutineDetailViewModel
        modifyRoutineViewModel.getSelectedExercises().observe(getViewLifecycleOwner(), selected -> {
            adapter.setSelectedExercises(selected != null ? selected : new ArrayList<>());
        });

        exerciseListViewModel.loadExercises();
        modifyRoutineViewModel.loadRoutine(new RoutineId(routineId));
    }

    private void initRecyclerView() {
        adapter = new ExerciseSelectAdapter(
                new ArrayList<>(),
                modifyRoutineViewModel.getSelectedExercises().getValue() != null
                        ? modifyRoutineViewModel.getSelectedExercises().getValue()
                        : new ArrayList<>(),
                (exercise, isSelected) -> modifyRoutineViewModel.toggleExerciseSelection(exercise),
                routineId
        );

        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);
    }

    private void initListeners() {
        binding.btnAddExercises.setOnClickListener(v -> navController.popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
