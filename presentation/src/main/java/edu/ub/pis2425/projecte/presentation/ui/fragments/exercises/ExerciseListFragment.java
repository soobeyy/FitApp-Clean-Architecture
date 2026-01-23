package edu.ub.pis2425.projecte.presentation.ui.fragments.exercises;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentExerciseListBinding;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.ExerciseAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ExerciseListViewModel;

public class ExerciseListFragment extends Fragment {

    private FragmentExerciseListBinding binding;
    private ExerciseListViewModel exerciseListViewModel;
    private ExerciseAdapter exerciseAdapter;
    private NavController navController;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    // Podrías mostrar un mensaje al usuario
                }
                exerciseListViewModel.loadExercises();
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseListBinding.inflate(inflater, container, false);
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

    private void initRecyclerView() {
        exerciseAdapter = new ExerciseAdapter();
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(exerciseAdapter);

        exerciseAdapter.setOnDeleteClickListener(this::showDeleteConfirmationDialog);
        exerciseAdapter.setOnShareClickListener(this::showIdCopiedSuccessMessage);
        exerciseAdapter.setOnCardClickListener(this::onCardClick);
    }

    private void showIdCopiedSuccessMessage(Exercise exercise) {
        String shareText = exercise.getId().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Mostrar el menú de compartir
        startActivity(Intent.createChooser(shareIntent, "Compartir ID del ejercicio"));
    }

    private void initViewModel() {
        exerciseListViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ExerciseListViewModel.class);

        exerciseListViewModel.getExercises()
                .observe(getViewLifecycleOwner(), exerciseAdapter::setExercises);
    }

    private void initWidgetListeners() {
        binding.btnCreateExercise.setOnClickListener(v ->
                navController.navigate(R.id.action_exerciseListFragment_to_createExerciseFragment)
        );

        binding.shareButton.setOnClickListener(v ->
                navController.navigate(R.id.action_exerciseListFragment_to_copyExerciseFragment)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        exerciseListViewModel.loadExercises();
    }

    private void onCardClick(Exercise exercise) {
        Bundle args = new Bundle();
        args.putString("exerciseId", exercise.getId().toString());
        navController.navigate(R.id.action_exerciseListFragment_to_exerciseDetailFragment, args);
    }

    private void showDeleteConfirmationDialog(Exercise exercise) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar el ejercicio \"" + exercise.getName() + "\"?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Sí", (dialog, which) ->
                        exerciseListViewModel.deleteExercise(exercise)
                )
                .setCancelable(true)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
