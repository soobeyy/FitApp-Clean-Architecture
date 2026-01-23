package edu.ub.pis2425.projecte.presentation.ui.fragments.week;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.data.services.DataService;
import edu.ub.pis2425.projecte.databinding.FragmentWeekBinding;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.adapters.WeekAdapter;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.WeekViewModel;

public class WeekFragment extends Fragment {

    private FragmentWeekBinding binding;
    private WeekViewModel weekViewModel;
    private WeekAdapter weekAdapter;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWeekBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity(); // Cierra la aplicación
            }
        });

        initRecyclerView();
        initViewModel();
        initButtons();
    }

    private void initRecyclerView() {
        weekAdapter = new WeekAdapter();
        binding.rvDays.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDays.setAdapter(weekAdapter);

        weekAdapter.setOnCardClickListener(this::onCardClick);
        weekAdapter.setOnAddClickListener(this::onAddClick);
    }

    private void initViewModel() {
        weekViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(WeekViewModel.class);

        // Observar los días de la semana
        weekViewModel.getWeekDays().observe(getViewLifecycleOwner(), days -> {
            if (days != null && !days.isEmpty()) {
                weekAdapter.setDays(days);
            }
        });

        // Observar el dayRoutineMap
        weekViewModel.getDayRoutineMap().observe(getViewLifecycleOwner(), dayRoutineMap -> {
            if (dayRoutineMap != null) {
                weekAdapter.setDayRoutineMap(dayRoutineMap);
            } else {
                Toast.makeText(requireContext(), "Error al cargar las rutinas", Toast.LENGTH_SHORT).show();
            }
        });
        weekViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        weekViewModel.getCurrentWeek().observe(
                getViewLifecycleOwner(),
                week -> {
                    if (week != null) {
                        binding.tvWeekTitle.setText(week.getId().toString());
                    }
                }
        );

    }

    private void initButtons() {
        binding.btnPrevWeek.setOnClickListener(v -> {
            weekViewModel.navigateWeek(-1);
            weekViewModel.getCurrentWeek().observe(
                    getViewLifecycleOwner(),
                    week -> {
                        if (week != null) {
                            binding.tvWeekTitle.setText(week.getId().toString());
                        }
                    }
            );
        });

        binding.btnNextWeek.setOnClickListener(v -> {
            weekViewModel.navigateWeek(1);
            weekViewModel.getCurrentWeek().observe(
                    getViewLifecycleOwner(),
                    week -> {
                        if (week != null) {
                            binding.tvWeekTitle.setText(week.getId().toString());
                        }
                    }
            );
        });
    }

    private void onCardClick(Day day) {
        Bundle args = new Bundle();
        args.putString("dayId", day.getId().toString());
        navController.navigate(R.id.action_weekFragment_to_routinesDayFragment, args);
    }

    private void onAddClick(Day day) {

        if(Day.compareTo(day.getId().toString()) == 3){
            Toast.makeText(requireContext(), "No puedes añadir rutinas a un dia ya pasado", Toast.LENGTH_SHORT).show();
        }else{
            Bundle args = new Bundle();
            args.putString("dayId", day.getId().toString());
            navController.navigate(R.id.action_weekFragment_to_selectRoutineFragment, args);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
