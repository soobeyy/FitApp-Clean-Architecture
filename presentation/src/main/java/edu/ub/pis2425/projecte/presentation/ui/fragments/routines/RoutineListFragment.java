package edu.ub.pis2425.projecte.presentation.ui.fragments.routines;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentRoutineListBinding;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.RoutineAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.RoutineListViewModel;

public class RoutineListFragment extends Fragment {

    private FragmentRoutineListBinding binding;
    private RoutineListViewModel routineListViewModel;
    private RoutineAdapter routineAdapter;
    private NavController navController;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    // Podríamos mostrar un mensaje al usuario
                }
                routineListViewModel.loadRoutines();
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutineListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        initRecyclerView();
        initViewModel();
        initWidgetListeners();
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // En RoutineListFragment.java
    private void initRecyclerView() {
        routineAdapter = new RoutineAdapter();
        binding.rvRoutines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRoutines.setAdapter(routineAdapter);

        routineAdapter.setOnDeleteClickListener(this::showDeleteConfirmationDialog);
        routineAdapter.setOnShareClickListener(this::showIdCopiedSuccessMessage);
        routineAdapter.setOnCardClickListener(this::onCardClick);
    }

    private void onCardClick(Routine routine) {
        Bundle bundle = new Bundle();
        bundle.putString("routineId", routine.getId().toString());
        navController.navigate(R.id.action_routineListFragment_to_routineDetailFragment, bundle);
    }

    private void showIdCopiedSuccessMessage(Routine routine) {
        String shareText = routine.getId().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Mostrar el menú de compartir
        startActivity(Intent.createChooser(shareIntent, "Compartir ID de la rutina"));
    }

    private void initViewModel() {
        routineListViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RoutineListViewModel.class);

        routineListViewModel.getRoutines().observe(getViewLifecycleOwner(), routines -> {
            routineAdapter.setRoutines(routines);
        });

        routineListViewModel.loadRoutines();
    }

    private void initWidgetListeners() {
        binding.btnCreateRoutine.setOnClickListener(v ->
                navController.navigate(R.id.action_routineListFragment_to_createRoutineFragment)
        );

        binding.shareButton.setOnClickListener(v ->
                navController.navigate(R.id.action_routineListFragment_to_copyRoutineFragment)
        );
    }

    private void showDeleteConfirmationDialog(Routine routine) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar rutina")
                .setMessage("¿Estás seguro de que quieres eliminar la rutina \"" + routine.getName() + "\"?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Sí", (dialog, which) ->
                        routineListViewModel.deleteRoutine(routine)
                )
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        routineListViewModel.loadRoutines();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
