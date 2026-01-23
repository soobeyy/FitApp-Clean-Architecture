package edu.ub.pis2425.projecte.presentation.viewmodel.week;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.ub.pis2425.projecte.domain.entities.Sensation;
import edu.ub.pis2425.projecte.features.usecases.week.RateSerieUseCase;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class RateSerieViewModel extends ViewModel {

    private final RateSerieUseCase rateSerieUseCase;
    private final MutableLiveData<Boolean> rateSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public RateSerieViewModel(RateSerieUseCase rateSerieUseCase) {
        this.rateSerieUseCase = rateSerieUseCase;
    }

    public MutableLiveData<Boolean> getRateSuccess() {
        return rateSuccess;
    }

    public void rateSerie(int kg, int reps, Sensation sensation, String dayId, String exerciseId) {
        disposables.add(
                rateSerieUseCase.execute(kg, reps, sensation, dayId, exerciseId)
                        .subscribe(
                                () -> rateSuccess.setValue(true),
                                err -> rateSuccess.setValue(false)
                        )
        );
    }
}
