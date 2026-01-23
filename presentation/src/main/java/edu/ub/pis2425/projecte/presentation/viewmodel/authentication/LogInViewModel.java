package edu.ub.pis2425.projecte.presentation.viewmodel.authentication;

import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.data.services.DataService;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogInUseCase;
import edu.ub.pis2425.projecte.presentation.pos.ClientPO;
import edu.ub.pis2425.projecte.presentation.pos.mappers.DomainToPOMapper;
import edu.ub.pis2425.projecte.presentation.viewmodel._livedata.StateLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import android.content.Context;

public class LogInViewModel extends ViewModel {
    private final LogInUseCase logInUseCase;
    private final StateLiveData<ClientId> logInState;
    private final CompositeDisposable disposables;

    public LogInViewModel(LogInUseCase logInUseCase) {
        this.logInUseCase = logInUseCase;
        logInState = new StateLiveData<>();
        disposables = new CompositeDisposable();
    }

    public void clearLogInState() {
        logInState.setValue(null);
    }

    public StateLiveData<ClientId> getLogInState() {
        return logInState;
    }

    public void logIn(String username, String password, Context context) {
        Disposable d = logInUseCase
                .execute(new ClientId(username), password) // Asegúrate de que ClientId esté correctamente importado
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        client -> onLoginSuccess(client, context),
                        logInState::postError
                );

        disposables.add(d);
    }

    private void onLoginSuccess(Client client, Context context) {
        DataService.getInstance(context).setClient(client);
        logInState.postSuccess(client.getId());
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}