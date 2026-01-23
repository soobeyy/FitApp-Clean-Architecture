package edu.ub.pis2425.projecte.presentation.ui.fragments.routines;

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

import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentModifyRoutineBinding;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.ModifyRoutineViewModel;

public class ModifyRoutineFragment extends Fragment {

    private FragmentModifyRoutineBinding binding;
    private ModifyRoutineViewModel viewModel;
    private NavController navController;
    private RoutineId routineId;
    private boolean editing = true;
    private boolean goingToSelectExercises = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentModifyRoutineBinding.inflate(inflater, container, false);
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


        viewModel.getRoutine().observe(getViewLifecycleOwner(), routine -> {
            if (routine == null) return;
            if(editing) {
                binding.etRoutineName.setText(routine.getName());
                binding.etRoutineDescription.setText(routine.getDescription());
                editing = false;
            }
        });


        viewModel.getUpdateRoutineResult().observe(getViewLifecycleOwner(), success -> {
            if(success == null) return;
            if(Boolean.TRUE.equals(success)){
                Toast.makeText(requireContext(), "Rutina actualizada", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
            } else if (Boolean.FALSE.equals(success)) {
                Toast.makeText(requireContext(), "Error al actualizar la rutina", Toast.LENGTH_SHORT).show();
            }
            editing = true;
        });

        viewModel.getSelectedExercises().observe(getViewLifecycleOwner(), exercises -> {
            int count = exercises != null ? exercises.size() : 0;
            String buttonText = getString(R.string.select_exercises) + " (" + count + ")";
            binding.btnSelectExercises.setText(buttonText);
        });

        viewModel.loadRoutine(routineId);
    }

    private void initWidgetListeners(){

        binding.btnSelectExercises.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("routineId", routineId.toString());
            navController.navigate(R.id.action_modifyRoutineFragment_to_modifySelectionExercisesFragment, args);
            goingToSelectExercises = true;
        });

        binding.btnSaveRoutine.setOnClickListener(v -> {
            String name = binding.etRoutineName.getText().toString().trim();
            String description = binding.etRoutineDescription.getText().toString().trim();
            List<Exercise> exercises = viewModel.getSelectedExercises().getValue();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre de la rutina no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if(exercises == null || exercises.isEmpty()){
                Toast.makeText(requireContext(), "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
                return;
            }

            Routine updatedRoutine = new Routine(routineId, name, description, exercises);

            viewModel.updateRoutine(updatedRoutine);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Limpia el estado de éxito al salir del fragmento
        viewModel.clearCreateSuccess();
    }
}