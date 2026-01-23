package edu.ub.pis2425.projecte.presentation.ui.fragments.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.databinding.FragmentLogInBinding;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.LogInViewModel;

public class LogInFragment extends Fragment {

    private LogInViewModel logInViewModel;
    private FragmentLogInBinding binding;
    private NavController navController;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
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

        /* Initializations */
        initWidgetListeners();
        initViewModel();
    }

    /**
     * Initialize the listeners of the widgets.
     */
    private void initWidgetListeners() {

        binding.btnLogIn.setOnClickListener(ignoredView -> {
            showLoadingDialog();
            logInViewModel.logIn(
                    String.valueOf(binding.etLoginUsername.getText()),
                    String.valueOf(binding.etLoginPassword.getText()),
                    requireContext()
            );
        });

        binding.btnSignUp.setOnClickListener(ignoredView -> {
            navController.navigate(R.id.action_logInFragment_to_signUpFragment);
        });
    }

    /**
     * Initialize the viewmodel and its observers.
     */
    private void initViewModel() {
        /* Init viewmodel */
        logInViewModel = new ViewModelProvider(
                requireActivity(),
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(LogInViewModel.class);

        /* Init observers */
        initObservers();
    }

    /**
     * Initialize the observers of the viewmodel.
     */
    private void initObservers() {
        /* Observe the login state */
        logInViewModel.getLogInState().observe(getViewLifecycleOwner(), logInState -> {
            // Whenever there's a change in the login state of the viewmodel
            hideLoadingDialog();
            if (logInState == null) return;
            switch (logInState.getStatus()) {
                case LOADING:
                    binding.btnLogIn.setEnabled(false);
                    break;
                case SUCCESS:
                    if (logInState.getData() == null) {
                        Toast.makeText(requireActivity(), "Error: Datos de usuario no disponibles", Toast.LENGTH_SHORT).show();
                        binding.btnLogIn.setEnabled(true);
                        break;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("CLIENT_ID", logInState.getData().getId());
                    navController.navigate(R.id.action_logInFragment_to_weekFragment, bundle);
                    break;
                case ERROR:
                    assert logInState.getError() != null;
                    String errorMessage = logInState.getError().getMessage();
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    binding.btnLogIn.setEnabled(true);
                    break;
                default:
                    throw new IllegalStateException("Valor inesperado: " + logInState.getStatus());
            }
        });
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Cargando perfil...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logInViewModel.clearLogInState();
        binding = null;
    }
}