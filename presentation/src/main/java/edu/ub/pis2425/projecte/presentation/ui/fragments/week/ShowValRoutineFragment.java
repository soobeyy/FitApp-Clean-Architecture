package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentShowValRoutineBinding;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.CompletingExercisesAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateRoutineViewModel;

public class ShowValRoutineFragment extends Fragment {

    private FragmentShowValRoutineBinding binding;
    private RateRoutineViewModel viewModel;
    private NavController navController;
    private RoutineId routineId;
    private String dayId;
    private CompletingExercisesAdapter exerciseAdapter;
    private int compareTo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShowValRoutineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Get routineId and dayId from arguments
        String idStr = requireArguments().getString("routineId");
        routineId = new RoutineId(idStr);
        dayId = requireArguments().getString("dayId");

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        compareTo = Day.compareTo(dayId);
        viewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RateRoutineViewModel.class);

        // Clear edit success state
        viewModel.clearCreateSuccess();

        exerciseAdapter = new CompletingExercisesAdapter();
        exerciseAdapter.setCompareTo(compareTo);

        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(exerciseAdapter);

        // Configure listener for navigating to exercise detail
        exerciseAdapter.setOnCardClickListener(exercise -> {
            List<Serie> series = viewModel.getSeriesForExercise(new ExerciseId(exercise.getId().toString()), dayId);
            if (series == null || series.isEmpty()) {
                Toast.makeText(requireContext(), "No hiciste este ejercicio", Toast.LENGTH_SHORT).show();
            } else {
                Bundle args = new Bundle();
                args.putString("exerciseId", exercise.getId().toString());
                args.putString("dayId", dayId);
                args.putString("routineId", routineId.toString());

                navController.navigate(R.id.action_showValRoutineFragment_to_showValExerciseFragment, args);
            }
        });

        // Observe routine data
        viewModel.getRoutine().observe(getViewLifecycleOwner(), routine -> {
            if (routine == null) return;
            binding.tvRoutineName.setText(routine.getName());
            binding.tvRoutineDescription.setText(routine.getDescription());
            if (routine.getExercises() != null && !routine.getExercises().isEmpty()) {
                exerciseAdapter.setExercises(routine.getExercises());
            } else {
                exerciseAdapter.setExercises(new ArrayList<>());
            }
        });

        // Observe done exercises
        viewModel.getDoneExercises().observe(getViewLifecycleOwner(), doneExerciseIds -> {
            if (doneExerciseIds != null) {
                exerciseAdapter.setCheckVisible(doneExerciseIds);
            } else {
                exerciseAdapter.setCheckVisible(new ArrayList<>());
            }
        });

        // Observe update result for navigation
        viewModel.getUpdateRoutineResult().observe(getViewLifecycleOwner(), result -> {
            if (Boolean.TRUE.equals(result)) {
                navController.navigate(R.id.action_rateRoutineFragment_to_weekFragment);
            }
        });
    }

    private void initWidgetListeners() {

    }

    @Override
    public void onStart() {
        super.onStart();
        // Reload routine and done exercises every time the fragment becomes visible
        viewModel.loadRoutine(routineId, dayId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}