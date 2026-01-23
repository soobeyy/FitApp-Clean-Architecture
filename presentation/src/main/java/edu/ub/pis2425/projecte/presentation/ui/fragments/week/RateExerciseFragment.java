package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

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

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentRateExerciseBinding;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ModifyExerciseViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateExerciseViewModel;

public class RateExerciseFragment extends Fragment {

    private FragmentRateExerciseBinding binding;
    private RateExerciseViewModel rateExerciseViewModel;
    private NavController navController;
    private Uri selectedImageUri;
    private ExerciseId exerciseId;
    private String dayId;
    private String routineId;

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
        binding = FragmentRateExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Recibimos sólo el ID
        String idStr = requireArguments().getString("exerciseId");
        exerciseId = new ExerciseId(idStr);
        dayId = requireArguments().getString("dayId");
        routineId = requireArguments().getString("routineId");

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        rateExerciseViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RateExerciseViewModel.class);

        // Carga inicial del ejercicio
        rateExerciseViewModel.getExercise()
                .observe(getViewLifecycleOwner(), exercise -> {
                    binding.tvExerciseName.setText(exercise.getName());
                    binding.tvExerciseDescription.setText(exercise.getDescription());

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

                    binding.tvExerciseName.setEnabled(false);
                    binding.tvExerciseDescription.setEnabled(false);
                });

        // Resultado de la actualización
        rateExerciseViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Ejercicio actualizado", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
            } else if (Boolean.FALSE.equals(success)) {
                Toast.makeText(requireContext(), "Error al actualizar el ejercicio", Toast.LENGTH_SHORT).show();
            }
        });

        // Finalmente, pedimos los datos
        rateExerciseViewModel.loadExercise(exerciseId);
    }

    private void initWidgetListeners() {
        binding.btniniexercise.setOnClickListener(v -> {

            rateExerciseViewModel.rateExercise(exerciseId.toString(), dayId);

            Bundle args = new Bundle();
            args.putString("exerciseId", exerciseId.toString());
            args.putString("dayId", dayId);
            args.putString("routineId", routineId);
            navController.navigate(R.id.action_rateExerciseFragment_to_rateSerieFragment, args);
        });

        binding.btnDontDoExercise.setOnClickListener(v -> {

            rateExerciseViewModel.rateExercise(exerciseId.toString(), dayId);

            Bundle args = new Bundle();
            args.putString("dayId", dayId);
            args.putString("routineId", routineId);

            navController.navigate(R.id.action_rateExerciseFragment_to_rateRoutineFragment, args);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
