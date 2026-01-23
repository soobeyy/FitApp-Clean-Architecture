package edu.ub.pis2425.projecte.presentation.viewmodel.authentication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import edu.ub.pis2425.projecte.R;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.features.usecases.authentication.GetClientByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogOutUseCase;
import edu.ub.pis2425.projecte.presentation.pos.ClientPO;
import edu.ub.pis2425.projecte.presentation.pos.mappers.DomainToPOMapper;
import edu.ub.pis2425.projecte.presentation.ui.fragments.authentication.ProfileFragment;
import edu.ub.pis2425.projecte.presentation.viewmodel._livedata.StateLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfileViewModel extends ViewModel {

    private final GetClientByIdUseCase getClientByIdUseCase;
    private final LogOutUseCase logoutUseCase;
    private final MutableLiveData<Client> clientState;
    private final CompositeDisposable disposables;
    private final MutableLiveData<Boolean> logoutState;
    private final MutableLiveData<Integer> routinesCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> exercisesCount = new MutableLiveData<>();

    public ProfileViewModel(GetClientByIdUseCase getClientByIdUseCase, LogOutUseCase logoutUseCase) {
        this.getClientByIdUseCase = getClientByIdUseCase;
        this.logoutUseCase = logoutUseCase;
        this.clientState = new MutableLiveData<>();
        this.disposables = new CompositeDisposable();
        this.logoutState = new MutableLiveData<>();
    }

    public void clearLogOutState() {
        logoutState.setValue(null);
    }

    public MutableLiveData<Client> getClientState() {
        return clientState;
    }

    public LiveData<Integer> getRoutinesCount() {
        return routinesCount;
    }

    public LiveData<Integer> getExercisesCount() {
        return exercisesCount;
    }

    public void loadClient() {
        disposables.add(
                getClientByIdUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                client -> {
                                    clientState.postValue(client);
                                    routinesCount.postValue(client.getRoutines().size());
                                    exercisesCount.postValue(client.getExercises().size());
                                },
                                throwable -> {
                                    // Manejo de errores: puedes registrar el error o actualizar otro LiveData
                                    clientState.postValue(null); // Por ejemplo, establece null en caso de error
                                    routinesCount.postValue(0);
                                    exercisesCount.postValue(0);
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<Boolean> getLogoutState() {
        return logoutState;
    }

    public void logout() {
        disposables.add(
                logoutUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Logout exitoso
                                    logoutState.postValue(true);
                                },
                                throwable -> {
                                    // Manejo de errores
                                    logoutState.postValue(false);
                                }
                        )
        );
    }
}