package edu.ub.pis2425.projecte.presentation.ui.fragments.routines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentCreateRoutineBinding;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.CreateRoutineViewModel;

public class CreateRoutineFragment extends Fragment {

    private FragmentCreateRoutineBinding binding;
    private CreateRoutineViewModel viewModel;
    private NavController navController;
    private final MutableLiveData<Boolean> createSuccess = new MutableLiveData<>(false);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateRoutineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        initViewModel();
        initListeners();
    }

    private void initViewModel() {
        // Inicializar CreateRoutineViewModel con alcance de actividad
        viewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(CreateRoutineViewModel.class);

        // Observar el resultado de la creación de la rutina
        viewModel.getCreateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Rutina creada con éxito", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_createRoutineFragment_to_routineListFragment);
            } else if (Boolean.FALSE.equals(success)) {
                Toast.makeText(requireContext(), "Error al crear rutina", Toast.LENGTH_SHORT).show();
            }
            viewModel.clearCreateSuccess();
            viewModel.clearSelectedExercises();
        });

        viewModel.getSelectedExercises().observe(getViewLifecycleOwner(), exercises -> {
            int count = exercises != null ? exercises.size() : 0;
            String buttonText = getString(R.string.select_exercises) + " (" + count + ")";
            binding.btnSelectExercises.setText(buttonText);
        });
    }


    private void initListeners() {
        binding.btnSaveRoutine.setOnClickListener(v -> {
            String name = binding.etRoutineName.getText().toString().trim();
            String description = binding.etRoutineDescription.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Necesitas introducir un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Exercise> exercises = viewModel.getSelectedExercises().getValue();

            if (exercises == null || exercises.isEmpty()) {
                Toast.makeText(requireContext(), "Debes seleccionar al menos un ejercicio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamar a createRoutine con los datos ingresados
            viewModel.createRoutine(name, description);
        });

        // Botón para navegar a la selección de ejercicios (si es necesario)
        binding.btnSelectExercises.setOnClickListener(v -> {
            navController.navigate(R.id.action_createRoutineFragment_to_selectExercisesFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}