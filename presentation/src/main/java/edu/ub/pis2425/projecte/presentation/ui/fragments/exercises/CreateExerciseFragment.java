package edu.ub.pis2425.projecte.presentation.ui.fragments.exercises;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;

import edu.ub.pis2425.projecte.R;

import edu.ub.pis2425.projecte.databinding.FragmentCreateExerciseBinding;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.CreateExerciseViewModel;

public class CreateExerciseFragment extends Fragment {

    private FragmentCreateExerciseBinding binding;
    private CreateExerciseViewModel createExerciseViewModel;
    private NavController navController;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String[]> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        requireActivity().getContentResolver()
                                .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignored) { }
                    Picasso.get()
                            .load(selectedImageUri)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivExerciseImage);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        createExerciseViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(CreateExerciseViewModel.class);

        createExerciseViewModel.getCreateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Ejercicio creado con éxito", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_createExerciseFragment_to_exerciseListFragment);
            } else if (Boolean.FALSE.equals(success)) {
                Toast.makeText(requireContext(), "Error al crear ejercicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initWidgetListeners() {
        binding.flImageContainer.setOnClickListener(v ->
                galleryLauncher.launch(new String[]{"image/*"})
        );

        binding.btnSaveExercise.setOnClickListener(v -> {
            String name = binding.etExerciseName.getText().toString().trim();
            String description = binding.etExerciseDescription.getText().toString().trim();
            String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                createExerciseViewModel.createExercise(name, description, imageUri);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
