package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

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

import java.util.List;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentSelectRoutineBinding;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.RoutineCheckboxAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.RoutineListViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.SelectRoutineViewModel;

public class SelectRoutineFragment extends Fragment {

    private FragmentSelectRoutineBinding binding;
    private RoutineListViewModel routineListViewModel;
    private SelectRoutineViewModel selectRoutineViewModel;
    private RoutineCheckboxAdapter routineAdapter;
    private NavController navController;
    private String dayId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectRoutineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        if (getArguments() != null) {
            dayId = getArguments().getString("dayId");
        }

        initRecyclerView();
        initViewModel();

        binding.btnConfirmRoutines.setOnClickListener(v -> {
            if (dayId == null) {
                Toast.makeText(getContext(), "Error: dayId es null", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Routine> selectedRoutines = routineAdapter.getSelectedRoutines();
            List<String> selectedRoutineIds = Routine.getIdsFromRoutines(selectedRoutines);
            selectRoutineViewModel.addRoutinesToDay(dayId, selectedRoutineIds);
        });

        selectRoutineViewModel.getAddSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                navController.navigate(R.id.action_selectRoutineFragment_to_weekFragment);
                Toast.makeText(getContext(), "Tu selección de rutinas se ha procesado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al seleccionar tus rutinas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        routineAdapter = new RoutineCheckboxAdapter();
        binding.rvRoutines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRoutines.setAdapter(routineAdapter);
    }

    private void initViewModel() {
        routineListViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RoutineListViewModel.class);

        selectRoutineViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(SelectRoutineViewModel.class);

        routineListViewModel.getRoutines().observe(getViewLifecycleOwner(), routines -> {
            routineAdapter.setRoutines(routines);
        });

        selectRoutineViewModel.getSelectedRoutines()
                .observe(getViewLifecycleOwner(), routines -> {
                    routineAdapter.setSelectedRoutines(routines);
                });

        routineListViewModel.loadRoutines();
        selectRoutineViewModel.loadSelectedRoutines(dayId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}