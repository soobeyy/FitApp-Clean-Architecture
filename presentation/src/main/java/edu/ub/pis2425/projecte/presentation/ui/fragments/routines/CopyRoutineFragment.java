package edu.ub.pis2425.projecte.presentation.ui.fragments.routines;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.CopyRoutineViewModel;

public class CopyRoutineFragment extends Fragment {

    private EditText etRoutineId;
    private Button btnCopyRoutine;
    private CopyRoutineViewModel viewModel;

    public CopyRoutineFragment() { /* Required empty constructor */ }

    public static CopyRoutineFragment newInstance() {
        return new CopyRoutineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_copy_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etRoutineId = view.findViewById(R.id.etRoutineId);
        btnCopyRoutine = view.findViewById(R.id.btnAddRoutine);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(CopyRoutineViewModel.class);

        // Observador del resultado de copiado
        viewModel.copyResult.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(),
                        "Rutina copiada correctamente",
                        Toast.LENGTH_SHORT).show();
                // Navegar de vuelta a la lista de rutinas
                Navigation.findNavController(view).navigate(R.id.action_copyRoutineFragment_to_routineListFragment);
            }
        });

        btnCopyRoutine.setOnClickListener(v -> {
            String idText = etRoutineId.getText().toString().trim();
            if (TextUtils.isEmpty(idText)) {
                Toast.makeText(requireContext(),
                        "Introduce un ID válido", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.copyRoutine(idText);
        });
    }
}