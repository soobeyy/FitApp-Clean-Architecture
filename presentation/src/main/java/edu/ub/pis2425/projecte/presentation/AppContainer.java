package edu.ub.pis2425.projecte.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ub.pis2425.projecte.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projecte.data.repositories.firestore.DayFirestoreRepository;
import edu.ub.pis2425.projecte.data.repositories.firestore.ExerciseFirestoreRepository;
import edu.ub.pis2425.projecte.data.repositories.firestore.RoutineFirestoreRepository;
import edu.ub.pis2425.projecte.data.repositories.firestore.SerieFirestoreRepository;
import edu.ub.pis2425.projecte.data.repositories.firestore.WeekFirestoreRepository;
import edu.ub.pis2425.projecte.data.services.DataService;
import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.repositories.SerieRepository;
import edu.ub.pis2425.projecte.features.repositories.WeekRepository;
import edu.ub.pis2425.projecte.features.usecases._implementation.authentication.GetClientByIdUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.authentication.LogOutUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.GetDoneExercisesUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.NavigateWeekUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.RateExerciseUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.RateSerieUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases.authentication.GetClientByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogOutUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.DuplicateExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.DuplicateRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.GetDoneExercisesUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.NavigateWeekUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.RateExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.RateSerieUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.SetRoutinesToDayUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.CheckIfClientExistsUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.CreateExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.CreateRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.DeleteExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.DeleteRoutineUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.EditExerciseUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetAllExercisesUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.GetAllRoutinesUseCase;
import edu.ub.pis2425.projecte.features.usecases.exercises.GetExerciseByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.routines.GetRoutineByIdUseCase;
import edu.ub.pis2425.projecte.features.usecases.week.GetRoutinesFromDayUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.LogInUseCase;
import edu.ub.pis2425.projecte.features.usecases.authentication.SignUpUseCase;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.DuplicateExerciseUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.DuplicateRoutineUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.SetRoutinesToDayUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.authentication.CheckIfClientExistsUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.CreateExerciseUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.CreateRoutineUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.DeleteExerciseUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.DeleteRoutineUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.EditExerciseUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.EditRoutineUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.FetchAllExercisesUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.FetchAllRoutinesUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.exercises.GetExerciseByIdUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.routines.GetRoutineByIdUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.week.GetRoutinesFromDayUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.authentication.LogInUseCaseImpl;
import edu.ub.pis2425.projecte.features.usecases._implementation.authentication.SignUpUseCaseImpl;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.CopyExerciseViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.ProfileViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.CopyRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.CreateExerciseViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.CreateRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ModifyExerciseViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.exercises.ExerciseListViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.LogInViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.ModifyRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.routines.RoutineListViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateExerciseViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RateSerieViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.RoutinesDayViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.SelectRoutineViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.authentication.SignUpViewModel;
import edu.ub.pis2425.projecte.presentation.viewmodel.week.WeekViewModel;


public class AppContainer {
    private final Application application;

    /* Repositorios */
    private final SerieRepository serieRepository = new SerieFirestoreRepository();
    private final DayRepository dayRepository = new DayFirestoreRepository();
    private final ExerciseRepository exerciseRepository = new ExerciseFirestoreRepository(dayRepository, serieRepository);
    private final WeekRepository weekRepository = new WeekFirestoreRepository(dayRepository);
    private final RoutineRepository routineRepository = new RoutineFirestoreRepository(exerciseRepository);
    private final ClientRepository clientRepository = new ClientFirestoreRepository(exerciseRepository, routineRepository, weekRepository, dayRepository);

    /* Servicios */
    private final IDataService dataService;

    /* UseCases */
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final LogOutUseCase logOutUseCase;
    private final LogInUseCase logInUseCase;
    private final CheckIfClientExistsUseCase checkIfClientExistsUseCase =
            new CheckIfClientExistsUseCaseImpl(clientRepository);
    private final SignUpUseCase signUpUseCase =
            new SignUpUseCaseImpl(clientRepository, checkIfClientExistsUseCase);
    private final GetAllExercisesUseCase getAllExercisesUseCase;
    private final CreateExerciseUseCase createExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;
    private final EditExerciseUseCase editExerciseUseCase =
            new EditExerciseUseCaseImpl(exerciseRepository);
    private final GetExerciseByIdUseCase getExerciseByIdUseCase =
            new GetExerciseByIdUseCaseImpl(exerciseRepository);
    private final GetAllRoutinesUseCase getAllRoutinesUseCase;
    private final CreateRoutineUseCase createRoutineUseCase;
    private final DeleteRoutineUseCase deleteRoutineUseCase;
    private final EditRoutineUseCaseImpl editRoutineUseCase =
            new EditRoutineUseCaseImpl(routineRepository);
    private final GetRoutineByIdUseCase getRoutineByIdUseCase =
            new GetRoutineByIdUseCaseImpl(routineRepository);
    private final SetRoutinesToDayUseCase setRoutinesToDayUseCase;
    private final GetRoutinesFromDayUseCase getRoutinesFromDayUseCase;
    private final DuplicateExerciseUseCase duplicateExerciseUseCase;
    private final DuplicateRoutineUseCase duplicateRoutineUseCase;
    private final RateSerieUseCase rateSerieUseCase =
            new RateSerieUseCaseImpl(exerciseRepository, serieRepository);
    private final RateExerciseUseCase rateExerciseUseCase =
            new RateExerciseUseCaseImpl(exerciseRepository);
    private final GetDoneExercisesUseCase getDoneExercisesUseCase =
            new GetDoneExercisesUseCaseImpl(routineRepository);
    private final NavigateWeekUseCase navigateWeekUseCase =
            new NavigateWeekUseCaseImpl(weekRepository);

    /* ViewModels */


    /* ViewModel factory */
    public final ViewModelFactory viewModelFactory = new ViewModelFactory(this);

    public AppContainer(Application application) {
        this.application = application;

        this.dataService = DataService.getInstance(application.getApplicationContext());

        this.logInUseCase = new LogInUseCaseImpl(clientRepository, dataService);
        this.createExerciseUseCase = new CreateExerciseUseCaseImpl(exerciseRepository, clientRepository, dataService);
        this.deleteExerciseUseCase = new DeleteExerciseUseCaseImpl(exerciseRepository, clientRepository, dataService);
        this.getAllExercisesUseCase = new FetchAllExercisesUseCaseImpl(clientRepository, dataService);
        this.duplicateExerciseUseCase = new DuplicateExerciseUseCaseImpl(exerciseRepository, clientRepository, dataService);
        this.createRoutineUseCase = new CreateRoutineUseCaseImpl(routineRepository, clientRepository, dataService);
        this.getAllRoutinesUseCase = new FetchAllRoutinesUseCaseImpl(clientRepository, dataService);
        this.deleteRoutineUseCase = new DeleteRoutineUseCaseImpl(routineRepository, clientRepository, dataService);
        this.setRoutinesToDayUseCase = new SetRoutinesToDayUseCaseImpl(clientRepository, dataService);
        this.getRoutinesFromDayUseCase = new GetRoutinesFromDayUseCaseImpl(clientRepository, dayRepository, dataService);
        this.duplicateRoutineUseCase = new DuplicateRoutineUseCaseImpl(routineRepository, clientRepository, dataService, exerciseRepository);
        this.getClientByIdUseCase = new GetClientByIdUseCaseImpl(clientRepository, dataService);
        this.logOutUseCase = new LogOutUseCaseImpl(dataService);
    }

    public Application getApplication() {
        return application;
    }

    public static class ViewModelFactory implements ViewModelProvider.Factory {
        private final AppContainer appContainer;

        public ViewModelFactory(AppContainer appContainer) {
            this.appContainer = appContainer;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
                return (T) new SignUpViewModel(appContainer.signUpUseCase);
            } else if (modelClass.isAssignableFrom(LogInViewModel.class)) {
                return (T) new LogInViewModel(appContainer.logInUseCase);
            } else if (modelClass.isAssignableFrom(CreateExerciseViewModel.class)) {
                return (T) new CreateExerciseViewModel(appContainer.createExerciseUseCase);
            } else if (modelClass.isAssignableFrom(ExerciseListViewModel.class)) {
                return (T) new ExerciseListViewModel(
                        appContainer.getAllExercisesUseCase,
                        appContainer.deleteExerciseUseCase
                );
            } else if (modelClass.isAssignableFrom(ModifyExerciseViewModel.class)) {
                return (T) new ModifyExerciseViewModel(
                        appContainer.getExerciseByIdUseCase,
                        appContainer.editExerciseUseCase
                );
            } else if (modelClass.isAssignableFrom(WeekViewModel.class)) {
                return (T) new WeekViewModel(
                        appContainer.getRoutinesFromDayUseCase,
                        appContainer.navigateWeekUseCase
                );
            } else if (modelClass.isAssignableFrom(RoutineListViewModel.class)) {
                return (T) new RoutineListViewModel(
                        appContainer.getAllRoutinesUseCase,
                        appContainer.deleteRoutineUseCase
                );
            } else if (modelClass.isAssignableFrom(ModifyRoutineViewModel.class)) {
                return (T) new ModifyRoutineViewModel(
                        appContainer.editRoutineUseCase,
                        appContainer.getRoutineByIdUseCase
                );
            } else if (modelClass.isAssignableFrom(CreateRoutineViewModel.class)) {
                return (T) new CreateRoutineViewModel(
                        appContainer.createRoutineUseCase
                );
            }
            else if (modelClass.isAssignableFrom(SelectRoutineViewModel.class)) {
                return (T) new SelectRoutineViewModel(
                        appContainer.setRoutinesToDayUseCase,
                        appContainer.getRoutinesFromDayUseCase
                        );
            }
            else if (modelClass.isAssignableFrom(RoutinesDayViewModel.class)) {
                return (T) new RoutinesDayViewModel(
                        appContainer.getRoutinesFromDayUseCase,
                        appContainer.setRoutinesToDayUseCase,
                        appContainer.getDoneExercisesUseCase
                );
            }
            else if (modelClass.isAssignableFrom(CopyExerciseViewModel.class)) {
                return (T) new CopyExerciseViewModel(
                        appContainer.duplicateExerciseUseCase
                );
            }
            else if (modelClass.isAssignableFrom(CopyRoutineViewModel.class)) {
                return (T) new CopyRoutineViewModel(
                        appContainer.duplicateRoutineUseCase
                );
            }
            else if (modelClass.isAssignableFrom(RateSerieViewModel.class)) {
                return (T) new RateSerieViewModel(
                        appContainer.rateSerieUseCase
                );
            }
            else if (modelClass.isAssignableFrom(RateExerciseViewModel.class)) {
                return (T) new RateExerciseViewModel(
                        appContainer.getExerciseByIdUseCase,
                        appContainer.editExerciseUseCase,
                        appContainer.rateExerciseUseCase
                );
            }
            else if (modelClass.isAssignableFrom(RateRoutineViewModel.class)) {
                return (T) new RateRoutineViewModel(
                        appContainer.editRoutineUseCase,
                        appContainer.getRoutineByIdUseCase,
                        appContainer.getDoneExercisesUseCase,
                        appContainer.rateExerciseUseCase
                );
            }
            else if(modelClass.isAssignableFrom(ProfileViewModel.class)){
                return (T) new ProfileViewModel(
                        appContainer.getClientByIdUseCase,
                        appContainer.logOutUseCase
                );
            }

            else {

            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
            }
        }
    }
}
