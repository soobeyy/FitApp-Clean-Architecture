package edu.ub.pis2425.projecte.presentation.ui.fragments.exercises;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentExerciseDetailBinding;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.ExerciseAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ModifyExerciseViewModel;

public class ExerciseDetailFragment extends Fragment {

    private FragmentExerciseDetailBinding binding;
    private ModifyExerciseViewModel viewModel;
    private NavController navController;
    private ExerciseId exerciseId;
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
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        String idStr = requireArguments().getString("exerciseId");
        exerciseId = new ExerciseId(idStr);

        boolean viewButton = requireArguments().getBoolean("viewButton", true);

        if (!viewButton){
            binding.btnEditExercise.setVisibility(View.GONE);
        }
        else{
            binding.btnEditExercise.setVisibility(View.VISIBLE);
        }

        initViewModel();
        initWidgetListeners();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ModifyExerciseViewModel.class);



        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise == null) return;
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

        });

        viewModel.loadExercise(exerciseId);
    }

    private void initWidgetListeners() {
        binding.btnEditExercise.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("exerciseId", exerciseId.toString());
            navController.navigate(R.id.action_exerciseDetailFragment_to_modifyExerciseFragment, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}