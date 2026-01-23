package edu.ub.pis2425.projecte.presentation.viewmodel.authentication;

import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.features.usecases.authentication.SignUpUseCase;
import edu.ub.pis2425.projecte.presentation.viewmodel._livedata.StateLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SignUpViewModel extends ViewModel {
    /* Attributes */
    private final SignUpUseCase signUpUseCase;
    /* LiveData */
    private final StateLiveData<Void> signUpState;
    /* RxJava */
    private final CompositeDisposable disposables;

    /* Constructor */
    public SignUpViewModel(SignUpUseCase signUpUseCase) {
        super();
        this.signUpUseCase = signUpUseCase;
        signUpState = new StateLiveData<>();
        disposables = new CompositeDisposable();
    }

    /**
    * Returns the state of the sign-up
    * @return the state of the sign-up
    */
    public StateLiveData<Void> getSignUpState() {
        return signUpState;
    }

    /**
    * Signs up the user
    * @param username the username
    * @param password the password
    * @param passwordConfirmation the password confirmation
    */
    public void signUp(String username, String password, String passwordConfirmation) {
        Disposable d = signUpUseCase
                .execute(username, password, passwordConfirmation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            signUpState.postSuccess(null); // Cambia a postSuccess
                        },
                        throwable -> {
                            signUpState.postError(throwable);
                        }
                );

        disposables.add(d);
    }
}
