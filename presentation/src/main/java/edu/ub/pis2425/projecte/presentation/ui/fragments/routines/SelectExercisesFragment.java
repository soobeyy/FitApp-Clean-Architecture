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
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.ExerciseSelectAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.CreateRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ExerciseListViewModel;

public class SelectExercisesFragment extends Fragment {

    private FragmentSelectExercisesBinding binding;
    private ExerciseSelectAdapter adapter;
    private ExerciseListViewModel exerciseListViewModel;
    private CreateRoutineViewModel createRoutineViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectExercisesBinding.inflate(inflater, container, false);
        createRoutineViewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(CreateRoutineViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        initRecyclerView();
        initViewModels();
        initListeners();
    }

    private void initViewModels() {
        exerciseListViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ExerciseListViewModel.class);

        exerciseListViewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            adapter.setExercises(exercises);

            if (getArguments() != null && getArguments().containsKey("selectedExerciseIds")) {
                ArrayList<String> selectedIds = getArguments().getStringArrayList("selectedExerciseIds");
                ArrayList<Exercise> selected = new ArrayList<>();
                for (Exercise e : exercises) {
                    if (selectedIds.contains(e.getId())) {
                        selected.add(e);
                    }
                }
                createRoutineViewModel.setSelectedExercises(selected);
                adapter.setSelectedExercises(selected);
            }
        });

        exerciseListViewModel.loadExercises();

        createRoutineViewModel.getSelectedExercises().observe(getViewLifecycleOwner(), selected -> {
            adapter.setSelectedExercises(selected != null ? selected : new ArrayList<>());
        });
    }

    private void initRecyclerView() {
        adapter = new ExerciseSelectAdapter(
                new ArrayList<>(),
                createRoutineViewModel.getSelectedExercises().getValue() != null
                        ? createRoutineViewModel.getSelectedExercises().getValue()
                        : new ArrayList<>(),
                (exercise, isSelected) -> createRoutineViewModel.toggleExerciseSelection(exercise)
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
