package edu.ub.pis2425.projecte.data.repositories.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.ExerciseFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.ExerciseMapper;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.SerieId;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.SerieRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ExerciseFirestoreRepository implements ExerciseRepository {
    /* Constants */
    private static final String EXERCISE_COLLECTION_NAME = "exercicis";
    /* Attributes */
    private final FirebaseFirestore db;
    private final DayRepository dayRepository;
    private final SerieRepository serieRepository;

    /**
     * Constructor with dependencies
     */
    public ExerciseFirestoreRepository(DayRepository dayRepository, SerieRepository serieRepository) {
        this.db = FirebaseFirestore.getInstance();
        this.dayRepository = dayRepository;
        this.serieRepository = serieRepository;
    }

    /**
     * Add an exercise to Firebase Cloud Firestore.
     *
     * @param exercise The exercise to add.
     */
    public Completable add(Exercise exercise) {
        return Completable.create(emitter -> {
            ExerciseFirestoreDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseFirestoreDto(exercise);

            db.collection(EXERCISE_COLLECTION_NAME)
                    .add(exerciseDto)
                    .addOnFailureListener(exception -> {
                        emitter.onError(new Throwable("Error adding exercise", exception));
                    })
                    .addOnSuccessListener(documentReference -> {
                        String generatedId = documentReference.getId();
                        exercise.setId(new ExerciseId(generatedId));

                        documentReference.update("id", generatedId)
                                .addOnFailureListener(updateException -> {
                                    emitter.onError(new Throwable("Error updating exercise ID", updateException));
                                })
                                .addOnSuccessListener(unused -> {
                                    emitter.onComplete();
                                });
                    });
        });
    }

    @Override
    public Observable<Exercise> getById(ExerciseId id) {
        return Observable.create(emitter -> {
            Task<DocumentSnapshot> task = db
                    .collection(EXERCISE_COLLECTION_NAME)
                    .document(id.toString())
                    .get();

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Database error"));
            }).addOnSuccessListener(ds -> {
                if (ds.exists()) {
                    ExerciseFirestoreDto exerciseDto = ds.toObject(ExerciseFirestoreDto.class);
                    Exercise exercise = ExerciseMapper.exerciseFirestoreDtoToExerciseWithoutValoracion(exerciseDto);

                    Map<String, List<String>> valoracionDto = exerciseDto.getValoracion();
                    if (valoracionDto == null || valoracionDto.isEmpty()) {
                        emitter.onNext(exercise);
                        emitter.onComplete();
                        return;
                    }

                    List<Observable<List<Serie>>> serieListObservables = valoracionDto.values().stream()
                            .map(serieIds -> {
                                List<SerieId> ids = serieIds.stream().map(SerieId::new).collect(Collectors.toList());
                                return serieRepository.getById(ids).firstElement().toObservable();
                            })
                            .collect(Collectors.toList());

                    // Guardamos las claves (dayIds) en orden para usarlas luego
                    List<String> dayIds = new ArrayList<>(valoracionDto.keySet());

                    Observable.zip(serieListObservables, series -> {
                        HashMap<String, List<Serie>> valoracion = new HashMap<>();
                        for (int i = 0; i < dayIds.size(); i++) {
                            valoracion.put(dayIds.get(i), (List<Serie>) series[i]);
                        }
                        exercise.setValoracion(valoracion);
                        return exercise;
                    }).subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
                } else {
                    emitter.onError(new Throwable("Exercise not found"));
                }
            });
        });
    }


    @Override
    public Observable<List<Exercise>> getById(List<ExerciseId> ids) {
        return Observable.create(emitter -> {
            if (ids == null || ids.isEmpty()) {
                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
                return;
            }

            List<String> idsString = ids.stream().map(ExerciseId::toString).collect(Collectors.toList());

            Task<QuerySnapshot> task = db
                    .collection(EXERCISE_COLLECTION_NAME)
                    .whereIn(FieldPath.documentId(), idsString)
                    .get();

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Error getting exercises"));
            }).addOnSuccessListener(queryDocumentSnapshots -> {
                List<Observable<Exercise>> exerciseObservables = new ArrayList<>();
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                    if (ds.exists()) {
                        exerciseObservables.add(getById(new ExerciseId(ds.getId())));
                    }
                }
                Observable.zip(exerciseObservables, exercises -> {
                    List<Exercise> exerciseList = new ArrayList<>();
                    for (Object ex : exercises) {
                        exerciseList.add((Exercise) ex);
                    }
                    return exerciseList;
                }).subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
            });
        });
    }

    @Override
    public Observable<List<Exercise>> getAll() {
        return Observable.create(emitter -> {
            Task<QuerySnapshot> task = db
                    .collection(EXERCISE_COLLECTION_NAME)
                    .get();

            task.addOnFailureListener(throwable -> {
                emitter.onError(new Throwable("Error getting exercises"));
            }).addOnSuccessListener(querySnapshot -> {
                List<Observable<Exercise>> exerciseObservables = querySnapshot.getDocuments().stream()
                        .map(ds -> getById(new ExerciseId(ds.getId())))
                        .collect(Collectors.toList());

                Observable.zip(exerciseObservables, exercises -> {
                    List<Exercise> exerciseList = new ArrayList<>();
                    for (Object ex : exercises) {
                        exerciseList.add((Exercise) ex);
                    }
                    return exerciseList;
                }).subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
            });
        });
    }

    @Override
    public Observable<List<Exercise>> getByName(String name) {
        return Observable.create(emitter -> {
            String nameLowerCase = name.toLowerCase();
            Task<QuerySnapshot> task = db
                    .collection(EXERCISE_COLLECTION_NAME)
                    .orderBy("nameLowerCase")
                    .startAt(nameLowerCase)
                    .endAt(nameLowerCase + "\uf8ff")
                    .get();

            task.addOnFailureListener(throwable -> {
                emitter.onError(new Throwable("Error getting exercises"));
            }).addOnSuccessListener(queryDocumentSnapshots -> {
                List<Observable<Exercise>> exerciseObservables = queryDocumentSnapshots.getDocuments().stream()
                        .map(ds -> getById(new ExerciseId(ds.getId())))
                        .collect(Collectors.toList());

                Observable.zip(exerciseObservables, exercises -> {
                    List<Exercise> exerciseList = new ArrayList<>();
                    for (Object ex : exercises) {
                        exerciseList.add((Exercise) ex);
                    }
                    return exerciseList;
                }).subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
            });
        });
    }

    @Override
    public Completable remove(ExerciseId id) {
        return Completable.create(emitter -> {
            Task<Void> task = db
                    .collection(EXERCISE_COLLECTION_NAME)
                    .document(id.toString())
                    .delete();

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Error removing exercise"));
            }).addOnSuccessListener(ignored -> {
                emitter.onComplete();
            });
        });
    }

    @Override
    public Completable update(Exercise exercise) {
        return Completable.create(emitter -> {
            String exerciseId = exercise.getId().toString();
            ExerciseFirestoreDto exerciseDto = ExerciseMapper.INSTANCE.exerciseToExerciseFirestoreDto(exercise);

            Task<Void> task = db.collection(EXERCISE_COLLECTION_NAME)
                    .document(exerciseId)
                    .set(exerciseDto);

            task.addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(exception -> emitter.onError(new Throwable("Error updating exercise", exception)));
        });
    }

    @Override
    public Observable<Exercise> duplicate(ExerciseId id) {
        return getById(id)
                .flatMap(original -> {
                    ExerciseFirestoreDto dto = ExerciseMapper.INSTANCE.exerciseToExerciseFirestoreDto(original);
                    dto.setId(null);

                    Exercise duplicate = ExerciseMapper.exerciseFirestoreDtoToExerciseWithoutValoracion(dto);
                    duplicate.setValoracion(original.getValoracion());

                    return add(duplicate)
                            .andThen(Observable.just(duplicate));
                });
    }

    @Override
    public Completable addSerieToExercise(String exerciseId, String dayId, Serie serie) {
        return Completable.create(emitter -> {
            String serieId = serie.getId().toString();
            DocumentReference docRef = db.collection(EXERCISE_COLLECTION_NAME)
                    .document(exerciseId);

            docRef.update("valoracion." + dayId, FieldValue.arrayUnion(serieId))
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(e -> emitter.onError(
                            new Throwable("Error adding serie to exercise", e)
                    ));
        });
    }
}