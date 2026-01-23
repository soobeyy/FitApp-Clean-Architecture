package edu.ub.pis2425.projecte.data.repositories.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.RoutineFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.RoutineMapper;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class RoutineFirestoreRepository implements RoutineRepository {
    private static final String ROUTINE_COLLECTION_NAME = "rutines";

    private final FirebaseFirestore db;
    private final ExerciseRepository exerciseRepository;

    public RoutineFirestoreRepository(ExerciseRepository exerciseRepository) {
        this.db = FirebaseFirestore.getInstance();
        this.exerciseRepository = exerciseRepository;
    }

    public Completable add(Routine routine) {
        return Completable.create(emitter -> {
            RoutineFirestoreDto dto = RoutineMapper.INSTANCE.routineToRoutineFirestoreDto(routine);

            db.collection(ROUTINE_COLLECTION_NAME)
                    .add(dto)
                    .addOnFailureListener(exception -> {
                        emitter.onError(new Throwable("Error adding routine", exception));
                    })
                    .addOnSuccessListener(documentReference -> {
                        String generatedId = documentReference.getId();
                        routine.setId(new RoutineId(generatedId));

                        // Actualiza el campo "id" dentro del mismo documento
                        documentReference.update("id", generatedId)
                                .addOnFailureListener(updateException -> {
                                    emitter.onError(new Throwable("Error updating routine ID", updateException));
                                })
                                .addOnSuccessListener(unused -> {
                                    emitter.onComplete();
                                });
                    });
        });
    }

    public Observable<Routine> getById(RoutineId id) {
        return Observable.create(emitter -> {
            db.collection(ROUTINE_COLLECTION_NAME)
                    .document(id.toString())
                    .get()
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Database error", e)))
                    .addOnSuccessListener(ds -> {
                        if (!ds.exists()) {
                            emitter.onError(new Throwable("Routine not found"));
                            return;
                        }

                        RoutineFirestoreDto dto = ds.toObject(RoutineFirestoreDto.class);
                        Routine routineWithoutExercises = RoutineMapper.routineFirestoreDtoToRoutineWithoutExercises(dto);

                        if (dto.getExercises() == null || dto.getExercises().isEmpty()) {
                            emitter.onNext(routineWithoutExercises);
                            emitter.onComplete();
                            return;
                        }

                        List<ExerciseId> ids = dto.getExercises().stream()
                                .map(ExerciseId::new)
                                .collect(Collectors.toList());

                        exerciseRepository.getById(ids)
                                .subscribe(
                                        exercises -> {
                                            Routine completeRoutine = new Routine(
                                                    routineWithoutExercises.getId(),
                                                    routineWithoutExercises.getName(),
                                                    routineWithoutExercises.getDescription(),
                                                    exercises
                                            );
                                            emitter.onNext(completeRoutine);
                                            emitter.onComplete();
                                        },
                                        emitter::onError
                                );
                    });
        });
    }

    @Override
    public Observable<List<Routine>> getById(List<RoutineId> ids) {
        if (ids == null || ids.isEmpty()) {
            return Observable.just(new ArrayList<>());
        }

        // Para cada ID lanzamos la versión single, y luego recolectamos en lista
        return Observable.fromIterable(ids)
                .flatMap(this::getById)            // llama a getById(RoutineId) que sí carga ejercicios
                .toList()                          // convierte a Single<List<Routine>>
                .toObservable();
    }

    @Override
    public Completable update(Routine routine) {
        return Completable.create(emitter -> {
            String routineId = routine.getId().toString();
            RoutineFirestoreDto routineDto = RoutineMapper.INSTANCE.routineToRoutineFirestoreDto(routine);

            Task<Void> task = db.collection(ROUTINE_COLLECTION_NAME)
                    .document(routineId)
                    .set(routineDto);

            task.addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(exception -> emitter.onError(new Throwable("Error updating routine", exception)));
        });
    }


    @Override
    public Completable delete(RoutineId id) {
        return Completable.create(emitter -> {
            db.collection(ROUTINE_COLLECTION_NAME)
                    .document(id.toString())
                    .delete()
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error deleting routine", e)));
        });
    }

    @Override
    public Observable<Routine> duplicate(RoutineId routineId) {
        return getById(routineId)
                .flatMap(originalRoutine ->
                        // 1. Dupliquemos todos los ejercicios de la rutina
                        Observable.fromIterable(originalRoutine.getExercises())
                                .flatMap(ex -> exerciseRepository.duplicate(ex.getId()))
                                .toList()               // Single<List<Exercise>>
                                .toObservable()         // Observable<List<Exercise>>
                                .flatMap(duplicatedExercises -> {
                                    // 2. Construimos la nueva rutina con los ejercicios duplicados
                                    Routine newRoutine = Routine.createRoutine(originalRoutine.getName(), originalRoutine.getDescription(), duplicatedExercises);

                                    // 3. La persistimos en Firestore
                                    return add(newRoutine)
                                            // 4. Una vez completado el add (que le pone el ID), emitimos la rutina
                                            .andThen(Observable.just(newRoutine));
                                })
                );
    }

}
