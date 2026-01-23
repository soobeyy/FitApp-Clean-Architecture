package edu.ub.pis2425.projecte.presentation.ui.fragments.exercises;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import edu.ub.pis2425.projecte.databinding.FragmentModifyExerciseBinding;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ModifyExerciseViewModel;

public class ModifyExerciseFragment extends Fragment {

    private FragmentModifyExerciseBinding binding;
    private ModifyExerciseViewModel modifyExerciseViewModel;
    private NavController navController;
    private Uri selectedImageUri;
    private ExerciseId exerciseId;

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
        binding = FragmentModifyExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Recibimos sólo el ID
        String idStr = requireArguments().getString("exerciseId");
        exerciseId = new ExerciseId(idStr);

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        modifyExerciseViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ModifyExerciseViewModel.class);

        // Carga inicial del ejercicio
        modifyExerciseViewModel.getExercise()
                .observe(getViewLifecycleOwner(), exercise -> {
                    binding.etExerciseName.setText(exercise.getName());
                    binding.etExerciseDescription.setText(exercise.getDescription());

                    if (!TextUtils.isEmpty(exercise.getImageUri())) {
                        Picasso.get()
                                .load(Uri.parse(exercise.getImageUri()))
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.ivExerciseImage);
                        selectedImageUri = Uri.parse(exercise.getImageUri());
                    } else {
                        binding.ivExerciseImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                });

        // Resultado de la actualización
        modifyExerciseViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Ejercicio actualizado", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
            } else if (Boolean.FALSE.equals(success)) {
                Toast.makeText(requireContext(), "Error al actualizar el ejercicio", Toast.LENGTH_SHORT).show();
            }
        });

        // Finalmente, pedimos los datos
        modifyExerciseViewModel.loadExercise(exerciseId);
    }

    private void initWidgetListeners() {
        binding.flImageContainer.setOnClickListener(v -> {
            galleryLauncher.launch(new String[]{"image/*"});
        });

        binding.btnSaveChanges.setOnClickListener(v -> {
            String name = binding.etExerciseName.getText().toString().trim();
            String desc = binding.etExerciseDescription.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)) {
                Toast.makeText(requireContext(),
                        "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;
            Exercise updated = new Exercise(exerciseId, name, desc, imageUri, modifyExerciseViewModel.getExercise().getValue().getValoracion());
            modifyExerciseViewModel.updateExercise(updated);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
