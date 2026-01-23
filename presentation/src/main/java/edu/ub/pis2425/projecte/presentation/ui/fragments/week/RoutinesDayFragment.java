package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import edu.ub.pis2425.projecte.databinding.FragmentRoutinesDayBinding;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.CompletingRoutinesAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RoutinesDayViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RoutinesDayFragment extends Fragment {

    private FragmentRoutinesDayBinding binding;
    private RoutinesDayViewModel routinesDayViewModel;
    private CompletingRoutinesAdapter routineAdapter;
    private NavController navController;
    private String dayId;
    private int compareTo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutinesDayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        if (getArguments() != null) {
            dayId = getArguments().getString("dayId");
        }

        if (dayId == null) {
            Toast.makeText(requireContext(), "Error: dayId es null", Toast.LENGTH_SHORT).show();
            return;
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_routinesDayFragment_to_weekFragment);
            }
        });

        compareTo = Day.compareTo(dayId);

        initRecyclerView();
        initViewModel();
    }

    private void initRecyclerView() {
        routineAdapter = new CompletingRoutinesAdapter();
        routineAdapter.setCompareTo(compareTo);
        binding.rvRoutines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRoutines.setAdapter(routineAdapter);

        routineAdapter.setOnCardClickListener(this::onCardClick);
    }

    private void onCardClick(Routine routine) {
        if (compareTo == 1) {
            Toast.makeText(requireContext(), "Todavía no puedes valorar esta rutina", Toast.LENGTH_SHORT).show();
        } else if(compareTo == 2) {
            Bundle bundle = new Bundle();
            bundle.putString("routineId", routine.getId().toString());
            bundle.putString("dayId", dayId);
            navController.navigate(R.id.action_routinesDayFragment_to_rateRoutineFragment, bundle);
        }else{
            Bundle bundle = new Bundle();
            bundle.putString("routineId", routine.getId().toString());
            bundle.putString("dayId", dayId);
            navController.navigate(R.id.action_routinesDayFragment_to_showValRoutineFragment, bundle);
        }
    }

    private void initViewModel() {
        routinesDayViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RoutinesDayViewModel.class);

        routinesDayViewModel.getRoutines().observe(getViewLifecycleOwner(), routines -> {
            routineAdapter.setRoutines(routines);
        });

        // Observe doneRoutines to update the adapter with completion status
        routinesDayViewModel.getDoneRoutines().observe(getViewLifecycleOwner(), doneRoutineIds -> {
            routineAdapter.setCheckVisible(doneRoutineIds);
        });

        routinesDayViewModel.loadRoutines(dayId);
    }

    private void showDeleteConfirmationDialog(String routineId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar rutina")
                .setMessage("¿Estás seguro de que deseas eliminar esta rutina?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    List<String> routineIds = new ArrayList<>(Routine.getIdsFromRoutines(routinesDayViewModel.getRoutines().getValue()));
                    routineIds.remove(routineId);
                    routinesDayViewModel.setRoutinesForDay(dayId, routineIds)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Toast.makeText(requireContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                                routinesDayViewModel.loadRoutines(dayId); // This will also refresh doneRoutines
                            }, throwable -> {
                                Toast.makeText(requireContext(), "Error al eliminar la rutina", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}