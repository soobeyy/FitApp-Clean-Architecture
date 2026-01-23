package edu.ub.pis2425.projecte.presentation.ui.fragments.exercises;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.CopyExerciseViewModel;

public class CopyExerciseFragment extends Fragment {

    private EditText etExerciseId;
    private Button btnAddExercise;
    private CopyExerciseViewModel viewModel;
    private NavController navController;

    public CopyExerciseFragment() { /* Required empty constructor */ }

    public static CopyExerciseFragment newInstance() {
        return new CopyExerciseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_copy_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Obtener referencias UI
        etExerciseId   = view.findViewById(R.id.etExerciseId);
        btnAddExercise = view.findViewById(R.id.btnAddExercise);
        navController = Navigation.findNavController(view);

        viewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(CopyExerciseViewModel.class);

        // 3. Observador de resultado
        viewModel.duplicateResult.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(),
                        "Ejercicio duplicado correctamente",
                        Toast.LENGTH_SHORT).show();
                // Navegar de vuelta a la lista de ejercicios
                navController.navigate(R.id.action_copyExerciseFragment_to_exerciseListFragment);
            }
        });

        // 4. Listener del botón
        btnAddExercise.setOnClickListener(v -> {
            String idText = etExerciseId.getText().toString().trim();
            if (TextUtils.isEmpty(idText)) {
                Toast.makeText(requireContext(),
                        "Introduce un ID válido",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.duplicateExercise(idText);
        });
    }
}
