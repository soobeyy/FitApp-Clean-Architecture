package edu.ub.pis2425.projecte.presentation.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import edu.ub.pis2425.projecte.databinding.ActivityMainBinding;
import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.data.services.DataService;
import edu.ub.pis2425.projecte.presentation.MyApplication;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.LogInViewModel;

public class MainActivity extends AppCompatActivity {

    /* Attributes */
    private NavController navController;
    AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onStart() {
        super.onStart();

        // Crear una instancia de LogInViewModel
        LogInViewModel logInViewModel = new ViewModelProvider(
                this,
                ((MyApplication) getApplication()).getViewModelFactory()
        ).get(LogInViewModel.class);

        // Verificar si la sesión está activa
        if (DataService.getInstance(this).getClientId() != null) {
            // Redirigir al usuario a la pantalla principal
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.logInFragment) {
                navController.navigate(R.id.action_logInFragment_to_weekFragment);
            }
        } else {
            // Redirigir al inicio de sesión si no hay sesión activa
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.logInFragment) {
                navController.navigate(R.id.action_global_logInFragment);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            try {
                navController.navigate(R.id.action_global_profileFragment);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the activity is being created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataService.getInstance(this).initialize();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        /* Initializations */
        initNavigation();
    }

    /**
     * Initialize the navigation.
     */
    private void initNavigation() {
        /* Set up the navigation controller */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_main);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        /* Set up the bottom navigation, indicating the fragments that will take part on it. */
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.weekFragment,
                R.id.exerciseListFragment,
                R.id.routineListFragment
        ).build();

        /* Set up navigation with both the action bar and the bottom navigation view */
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Escucha personalizada para el BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.weekFragment) {
                if (navController.getCurrentDestination().getId() != R.id.weekFragment) {
                    navController.navigate(R.id.weekFragment);
                }
            } else if (itemId == R.id.exerciseListFragment) {
                if (navController.getCurrentDestination().getId() != R.id.exerciseListFragment) {
                    navController.navigate(R.id.exerciseListFragment);
                }
            } else if (itemId == R.id.routineListFragment) {
                if (navController.getCurrentDestination().getId() != R.id.routineListFragment) {
                    navController.navigate(R.id.routineListFragment);
                }
            }

            // Marcar el ítem como seleccionado (efecto visual)
            return true;
        });

        /*
          Detect when the navigation destination changes.
         */
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Set the visibility of the bottom navigation view and the upper toolbar depending
            // on the navigation destination
            setBottomNavigationViewVisibility(destination);
            setActionBarVisibility(destination);
        });
    }

    public void setProfileButtonVisibility(boolean visible) {
        Menu menu = binding.toolbar.getMenu();
        if (menu != null) {
            MenuItem profileItem = menu.findItem(R.id.action_profile);
            if (profileItem != null) {
                profileItem.setVisible(visible);
            }
        }
    }

    /**
     * Set the visibility of the bottom navigation view depending on the navigation destination.
     * @param destination The navigation destination.
     */
    private void setBottomNavigationViewVisibility(NavDestination destination) {
        /* If the destination is the log in or sign up fragment, hide the bottom navigation view */
        if (destination.getId() == R.id.logInFragment || destination.getId() == R.id.signUpFragment) {
            binding.bottomNavigationView.setVisibility(View.GONE);
        } else {
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set the visibility of the upper toolbar and profile button depending on the navigation destination.
     * @param destination The navigation destination.
     */
    private void setActionBarVisibility(NavDestination destination) {
        /* If the destination is the log in fragment, hide the toolbar */
        if (destination.getId() == R.id.logInFragment) {
            binding.toolbar.setVisibility(View.GONE);
        } else {
            binding.toolbar.setVisibility(View.VISIBLE);
        }

        /* Show profile button only in weekFragment, exerciseListFragment, and routineListFragment */
        setProfileButtonVisibility(
                destination.getId() == R.id.weekFragment ||
                        destination.getId() == R.id.exerciseListFragment ||
                        destination.getId() == R.id.routineListFragment
        );
    }

    /**
     * Handle the navigating up action (left-pointing arrow in the action bar).
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Esto hace lo mismo que el botón "atrás" del sistema
        return true;
    }
}