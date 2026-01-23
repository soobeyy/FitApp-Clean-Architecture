package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

import android.content.res.ColorStateList;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentRateSerieBinding;
import edu.ub.pis2425.projecte.domain.entities.Sensation;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateSerieViewModel;

public class RateSerieFragment extends Fragment {

    private FragmentRateSerieBinding binding;
    private NavController navController;
    private Sensation sensation = null;
    private RateSerieViewModel rateViewModel;
    private String exerciseId;
    private String dayId;
    private String routineId;
    private ImageView emojiRojo;
    private ImageView emojiAmarillo;
    private ImageView emojiVerde;

    private static final int COLOR_ROJO = 0xFFFF0000; // #FF0000 (Rojo)
    private static final int COLOR_AMARILLO = 0xFFFFFF00; // #FFFF00 (Amarillo)
    private static final int COLOR_VERDE = 0xFF00FF00; // #00FF00 (Verde)
    private static final int COLOR_GRIS = 0xFF808080; // #808080 (Gris medio)

    ColorStateList grayTint = ColorStateList.valueOf(COLOR_GRIS);
    ColorStateList rojoTint = ColorStateList.valueOf(COLOR_ROJO);
    ColorStateList amarilloTint = ColorStateList.valueOf(COLOR_AMARILLO);
    ColorStateList verdeTint = ColorStateList.valueOf(COLOR_VERDE);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRateSerieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        exerciseId = requireArguments().getString("exerciseId");
        dayId = requireArguments().getString("dayId");
        routineId = requireArguments().getString("routineId");

        initViewModel();
        initListeners();
    }

    private void initViewModel(){
        rateViewModel = new ViewModelProvider(this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(RateSerieViewModel.class);

        rateViewModel.getRateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Serie completada correctamente", Toast.LENGTH_SHORT).show();
                binding.etWeight.setText("");
                binding.etRepetitions.setText("");
                sensation = null;
                emojiRojo.setImageTintList(grayTint);
                emojiAmarillo.setImageTintList(grayTint);
                emojiVerde.setImageTintList(grayTint);
            } else {
                Toast.makeText(requireContext(), "Error al completar la serie", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListeners() {

        // Referencias a los botones
        emojiRojo = binding.emojiRojo;
        emojiAmarillo = binding.emojiAmarillo;
        emojiVerde = binding.emojiVerde;



        // Listener para el botón rojo
        binding.emojiRojo.setOnClickListener(v -> {
            sensation = Sensation.JUSTITO;

            // Rojo en color, otros en gris
            emojiRojo.setImageTintList(rojoTint);
            emojiAmarillo.setImageTintList(grayTint);
            emojiVerde.setImageTintList(grayTint);
        });

        // Listener para el botón amarillo
        binding.emojiAmarillo.setOnClickListener(v -> {
            sensation = Sensation.NORMAL;

            // Amarillo en color, otros en gris
            emojiRojo.setImageTintList(grayTint);
            emojiAmarillo.setImageTintList(amarilloTint);
            emojiVerde.setImageTintList(grayTint);
        });

        // Listener para el botón verde
        binding.emojiVerde.setOnClickListener(v -> {
            sensation = Sensation.SOBRADO;

            // Verde en color, otros en gris
            emojiRojo.setImageTintList(grayTint);
            emojiAmarillo.setImageTintList(grayTint);
            emojiVerde.setImageTintList(verdeTint);
        });

        emojiRojo.setImageTintList(grayTint);
        emojiAmarillo.setImageTintList(grayTint);
        emojiVerde.setImageTintList(grayTint);

        binding.btnNextSerie.setOnClickListener(v ->{
            int kg = binding.etWeight.getText().toString().isEmpty() ? -1 : Integer.parseInt(binding.etWeight.getText().toString());
            int reps = binding.etRepetitions.getText().toString().isEmpty() ? -1 : Integer.parseInt(binding.etRepetitions.getText().toString());
            if (sensation != null && kg != -1 && reps != -1) {
                rateViewModel.rateSerie(kg, reps, sensation, dayId, exerciseId);
            } else {
                Toast.makeText(requireContext(),
                        "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnFinishExercise.setOnClickListener(v -> {

            int kg = binding.etWeight.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.etWeight.getText().toString());
            int reps = binding.etRepetitions.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.etRepetitions.getText().toString());



            if ((sensation != null && kg != 0 && reps != 0) || (sensation == null && kg == 0 && reps == 0)) {
                if(sensation != null && kg != 0 && reps != 0){
                    rateViewModel.rateSerie(kg, reps, sensation, dayId, exerciseId);
                }

                Toast.makeText(requireContext(), "Ejercicio completado", Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putString("exerciseId", exerciseId);
                args.putString("dayId", dayId);
                args.putString("routineId", routineId);
                navController.navigate(R.id.action_rateSerieFragment_to_rateRoutineFragment, args);
            } else {
                Toast.makeText(requireContext(),
                        "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
