package edu.ub.pis2425.projecte.presentation.ui.fragments.authentication;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.ProfileViewModel;

public class ProfileFragment extends Fragment {
    private ProfileViewModel viewModel;
    private TextView userEmailTextView;
    private TextView routinesValueTextView;
    private TextView exercisesValueTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el ViewModel antes de usarlo
        viewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(ProfileViewModel.class);

        userEmailTextView = view.findViewById(R.id.tvUserEmail);
        TextView routinesValueTextView = view.findViewById(R.id.tvRoutinesValue);
        TextView exercisesValueTextView = view.findViewById(R.id.tvExercisesValue);

        viewModel.getRoutinesCount().observe(getViewLifecycleOwner(), routinesCount -> {
            routinesValueTextView.setText(String.valueOf(routinesCount));
        });

        viewModel.getExercisesCount().observe(getViewLifecycleOwner(), exercisesCount -> {
            exercisesValueTextView.setText(String.valueOf(exercisesCount));
        });

        viewModel.getClientState().observe(getViewLifecycleOwner(), client -> {
            if (client != null) {
                userEmailTextView.setText(client.getId().getId());
            } else {
                userEmailTextView.setText(R.string.error_loading_client);
            }
        });

        viewModel.loadClient();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
                navController.navigate(R.id.action_profileFragment_to_weekFragment);
            }
        });

        // Configurar el botón de logout
        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> {
            viewModel.logout();
        });

        viewModel.getLogoutState().observe(getViewLifecycleOwner(), isLoggedOut -> {
            if (Boolean.TRUE.equals(isLoggedOut)) {
                Toast.makeText(requireActivity(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
                navController.navigate(R.id.action_profileFragment_to_loginFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clearLogOutState();
    }
}