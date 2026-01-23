package edu.ub.pis2425.projecte.presentation.ui.fragments.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentSignUpBinding;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.ui.activities.MainActivity;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.SignUpViewModel;

public class SignUpFragment extends Fragment {

    /* Attributes */
    private SignUpViewModel signUpViewModel;
    private FragmentSignUpBinding binding;
    private NavController navController;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ((MainActivity) requireActivity()).setProfileButtonVisibility(false);

        initViewModel();
        initWidgetListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).setProfileButtonVisibility(true);
        binding = null;
    }

    /**
     * Initialize the listeners of the widgets.
     */
    private void initWidgetListeners() {
        binding.btnSignUp.setOnClickListener(ignoredView -> {
            String username = String.valueOf(binding.etSignupUsername.getText()).trim();
            String password = String.valueOf(binding.etSignupPassword.getText()).trim();
            String passwordConfirmation = String.valueOf(binding.etSignupPasswordConfirmation.getText()).trim();

            if (username.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
                Toast.makeText(requireActivity(), "Porfavor rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                showLoadingDialog();
                signUpViewModel.signUp(username, password, passwordConfirmation);
            }
        });
    }

    /**
     * Initialize the viewmodel and its observers.
     */
    private void initViewModel() {
        signUpViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(SignUpViewModel.class);

        initObservers();
    }

    /**
     * Initialize the observers of the viewmodel.
     */
    private void initObservers() {
        signUpViewModel.getSignUpState().observe(getViewLifecycleOwner(), state -> {
            hideLoadingDialog();
            switch (state.getStatus()) {
                case SUCCESS:
                    if (navController != null && navController.getCurrentDestination() != null
                            && navController.getCurrentDestination().getId() == R.id.signUpFragment) {
                        navController.navigate(R.id.action_signUpFragment_to_logInFragment);
                    }
                    break;
                case ERROR:
                    Toast.makeText(requireActivity(), state.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Creando perfil...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}