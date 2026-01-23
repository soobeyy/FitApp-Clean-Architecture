package edu.ub.pis2425.projecte.presentation;

import android.app.Application;

public class MyApplication extends Application {
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the AppContainer when the application starts
        appContainer = new AppContainer(this);
    }

    @SuppressWarnings("unused")
    public AppContainer getAppContainer() {
        return appContainer;
    }

    @SuppressWarnings("unused")
    public AppContainer.ViewModelFactory getViewModelFactory() {
        return appContainer.viewModelFactory;
    }
}
