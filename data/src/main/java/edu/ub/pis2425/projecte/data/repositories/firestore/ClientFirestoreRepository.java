package edu.ub.pis2425.projecte.data.repositories.firestore;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.firestore.DayFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.firestore.ExerciseFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.firestore.WeekFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.ClientMapper;
import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.entities.Exercise;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.ExerciseId;
import edu.ub.pis2425.projecte.domain.valueobjects.RoutineId;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;
import edu.ub.pis2425.projecte.features.repositories.ClientRepository;
import edu.ub.pis2425.projecte.features.repositories.ExerciseRepository;
import edu.ub.pis2425.projecte.features.repositories.RoutineRepository;
import edu.ub.pis2425.projecte.features.repositories.WeekRepository;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import edu.ub.pis2425.projecte.data.dtos.mappers.WeekMapper;
import edu.ub.pis2425.projecte.data.dtos.mappers.DayMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ClientFirestoreRepository implements ClientRepository {
    private static final String CLIENTS_COLLECTION_NAME = "clients";
    private final FirebaseFirestore db;
    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final WeekRepository weekRepository;
    private final DayRepository dayRepository;

    public ClientFirestoreRepository(
            ExerciseRepository exerciseRepository,
            RoutineRepository routineRepository,
            WeekRepository weekRepository,
            DayRepository dayRepository
    ) {
        db = FirebaseFirestore.getInstance();
        this.exerciseRepository = exerciseRepository;
        this.routineRepository = routineRepository;
        this.weekRepository = weekRepository;
        this.dayRepository = dayRepository;
    }

    @SuppressLint("CheckResult")
    public Completable add(Client client) {
        return Completable.create(emitter -> {
            // 1. Prepare the initial client data
            ClientFirestoreDto clientDto = ClientMapper.INSTANCE.clientToClientFirestoreDto(client);
            DocumentReference clientRef = db.collection(CLIENTS_COLLECTION_NAME).document(client.getId().toString());

            // 2. Create a list to track all week operations
            List<Completable> weekOperations = new ArrayList<>();

            // 3. For each week, delegate to weekRepository
            for (Week week : client.getWeeks()) {
                weekOperations.add(weekRepository.add(week));
            }

            // 4. After all weeks are processed, then save the client with their IDs
            Completable.concat(weekOperations)
                    .andThen(Completable.create(innerEmitter -> {
                        // Now all weeks and days have their IDs set, collect week IDs
                        List<String> weekIds = client.getWeeks().stream()
                                .map(week -> week.getId().toString())
                                .collect(Collectors.toList());
                        clientDto.setWeeks(weekIds);

                        // Save the client with week references
                        clientRef.set(clientDto)
                                .addOnSuccessListener(unused -> innerEmitter.onComplete())
                                .addOnFailureListener(e -> innerEmitter.onError(new Throwable("Error adding client", e)));
                    }))
                    .subscribe(
                            () -> emitter.onComplete(),
                            error -> emitter.onError(error)
                    );
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public Observable<Client> getById(ClientId id) {
        return Observable.create(emitter -> {
            Task<DocumentSnapshot> task = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(id.toString())
                    .get();

            task.addOnFailureListener(exception -> emitter.onError(new Throwable("Database error")))
                    .addOnSuccessListener(ds -> {
                        if (!ds.exists()) {
                            emitter.onError(new Throwable("Client not found"));
                            return;
                        }

                        ClientFirestoreDto dto = ds.toObject(ClientFirestoreDto.class);
                        Client baseClient = ClientMapper.clientFirestoreDtoToClientWithoutExercises(dto);

                        List<ExerciseId> exerciseIds = dto.getExercises() == null ? List.of() :
                                dto.getExercises().stream().map(ExerciseId::new).collect(Collectors.toList());
                        List<RoutineId> routineIds = dto.getRoutines() == null ? List.of() :
                                dto.getRoutines().stream().map(RoutineId::new).collect(Collectors.toList());
                        List<WeekId> weekIds = dto.getWeeks() == null ? List.of() :
                                dto.getWeeks().stream().map(WeekId::new).collect(Collectors.toList());

                        // Recuperar el dayRoutineMap
                        Map<String, List<String>> dayRoutineMapDto = dto.getDayRoutineMap();
                        @SuppressLint("CheckResult") Observable<HashMap<Day, List<Routine>>> dayRoutineMapObservable = Observable.create(innerEmitter -> {
                            if (dayRoutineMapDto == null || dayRoutineMapDto.isEmpty()) {
                                innerEmitter.onNext(new HashMap<>());
                                innerEmitter.onComplete();
                                return;
                            }

                            List<Observable<Day>> dayObservables = dayRoutineMapDto.keySet().stream()
                                    .map(dayId -> dayRepository.getById(new DayId(dayId)).firstElement().toObservable())
                                    .collect(Collectors.toList());

                            List<Observable<List<Routine>>> routineListObservables = dayRoutineMapDto.values().stream()
                                    .map(routineIds2 -> {
                                        List<RoutineId> ids = routineIds2.stream().map(RoutineId::new).collect(Collectors.toList());
                                        return routineRepository.getById(ids).firstElement().toObservable();
                                    })
                                    .collect(Collectors.toList());

                            Observable.zip(
                                    Observable.zip(dayObservables, days -> days),
                                    Observable.zip(routineListObservables, routines -> routines),
                                    (days, routines) -> {
                                        HashMap<Day, List<Routine>> dayRoutineMap = new HashMap<>();
                                        for (int i = 0; i < days.length; i++) {
                                            dayRoutineMap.put((Day) days[i], (List<Routine>) routines[i]);
                                        }
                                        return dayRoutineMap;
                                    }
                            ).subscribe(innerEmitter::onNext, innerEmitter::onError, innerEmitter::onComplete);
                        });

                        Observable.zip(
                                exerciseRepository.getById(exerciseIds),
                                routineRepository.getById(routineIds),
                                weekRepository.getById(weekIds),
                                dayRoutineMapObservable,
                                (exercises, routines, weeks, dayRoutineMap) -> new Client(
                                        baseClient.getId(),
                                        baseClient.getPassword(),
                                        exercises,
                                        routines,
                                        weeks,
                                        dayRoutineMap
                                )
                        ).subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
                    });
        });
    }

    @Override
    public Observable<Boolean> update(ClientId id, OnUpdateListener onUpdateListener) {
        return Observable.create(emitter -> {
            DocumentReference docRef = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(id.toString());

            db.runTransaction(transaction -> {
                        DocumentSnapshot ds = transaction.get(docRef);
                        if (!ds.exists()) return false;

                        ClientFirestoreDto dto = ds.toObject(ClientFirestoreDto.class);
                        Client client = ClientMapper.clientFirestoreDtoToClientWithoutExercises(dto);

                        List<ExerciseId> exerciseIds = dto.getExercises() == null ? List.of() :
                                dto.getExercises().stream().map(ExerciseId::new).collect(Collectors.toList());
                        List<RoutineId> routineIds = dto.getRoutines() == null ? List.of() :
                                dto.getRoutines().stream().map(RoutineId::new).collect(Collectors.toList());
                        List<WeekId> weekIds = dto.getWeeks() == null ? List.of() :
                                dto.getWeeks().stream().map(WeekId::new).collect(Collectors.toList());

                        List<Exercise> exercises = exerciseRepository.getById(exerciseIds).blockingFirst();
                        List<Routine> routines = routineRepository.getById(routineIds).blockingFirst();
                        List<Week> weeks = weekRepository.getById(weekIds).blockingFirst();

                        client.setExercises(exercises);
                        client.setRoutines(routines);
                        client.setWeeks(weeks);

                        onUpdateListener.update(client);

                        ClientFirestoreDto updatedDto = ClientMapper.INSTANCE.clientToClientFirestoreDto(client);
                        transaction.set(docRef, updatedDto);

                        return true;
                    }).addOnFailureListener(emitter::onError)
                    .addOnSuccessListener(success -> {
                        if (!success) emitter.onError(new Throwable("Client not found"));
                        else {
                            emitter.onNext(true);
                            emitter.onComplete();
                        }
                    });
        });
    }

    public Completable addExercise(ClientId clientId, ExerciseId exerciseId) {
        return Completable.create(emitter -> {
            DocumentReference docRef = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(clientId.toString());

            db.runTransaction(transaction -> {
                        DocumentSnapshot ds = transaction.get(docRef);
                        if (!ds.exists()) {
                            throw new IllegalStateException("Client not found");
                        }

                        ClientFirestoreDto clientDto = ds.toObject(ClientFirestoreDto.class);
                        List<String> exerciseIds = clientDto.getExercises();
                        if (exerciseIds == null) {
                            exerciseIds = new ArrayList<>();
                        }

                        if (!exerciseIds.contains(exerciseId.toString())) {
                            exerciseIds.add(exerciseId.toString());
                        }

                        clientDto.setExercises(exerciseIds);
                        transaction.set(docRef, clientDto);
                        return null;
                    })
                    .addOnFailureListener(emitter::onError)
                    .addOnSuccessListener(unused -> emitter.onComplete());
        });
    }

    @Override
    public Completable removeExercise(ClientId clientId, ExerciseId exerciseId) {
        return Completable.create(emitter -> {
            db.collection(CLIENTS_COLLECTION_NAME)
                    .document(clientId.toString())
                    .get()
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error getting client data", e)))
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            emitter.onError(new Throwable("Client not found"));
                            return;
                        }

                        ClientFirestoreDto dto = documentSnapshot.toObject(ClientFirestoreDto.class);
                        if (dto == null || dto.getExercises() == null) {
                            emitter.onError(new Throwable("Malformed client data"));
                            return;
                        }

                        List<String> exerciseIds = dto.getExercises();
                        boolean removed = exerciseIds.removeIf(id -> id.equals(exerciseId.toString()));

                        if (!removed) {
                            emitter.onComplete();
                            return;
                        }

                        db.collection(CLIENTS_COLLECTION_NAME)
                                .document(clientId.toString())
                                .set(new ClientFirestoreDto(clientId.toString(), dto.getPassword(), exerciseIds, dto.getRoutines(), dto.getWeeks(), dto.getDayRoutineMap()))
                                .addOnFailureListener(updateEx -> emitter.onError(new Throwable("Error updating exercise list", updateEx)))
                                .addOnSuccessListener(unused -> emitter.onComplete());
                    });
        });
    }

    @Override
    public Completable addBasic(ClientId clientId) {
        List<Completable> completables = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            RoutineId baseRoutineId = new RoutineId(String.valueOf(i));
            completables.add(
                    routineRepository.duplicate(baseRoutineId)
                            .flatMapCompletable(duplicatedRoutine -> {
                                List<Completable> adds = new ArrayList<>();
                                for (Exercise ex : duplicatedRoutine.getExercises()) {
                                    adds.add(addExercise(clientId, ex.getId()));
                                }
                                // Después de añadir ejercicios, asociar la rutina al cliente
                                return Completable.concat(adds)
                                        .andThen(addRoutine(clientId, duplicatedRoutine.getId().toString()));
                            })
            );
        }

        return Completable.concat(completables);
    }

    @Override
    public Completable addRoutine(ClientId clientId, String routineId) {
        if (routineId == null) {
            return Completable.error(new IllegalArgumentException("routineId cannot be null"));
        }
        return Completable.create(emitter -> {
            DocumentReference docRef = db.collection(CLIENTS_COLLECTION_NAME).document(clientId.toString());

            db.runTransaction(transaction -> {
                        DocumentSnapshot ds = transaction.get(docRef);
                        if (!ds.exists()) throw new IllegalStateException("Client not found");

                        ClientFirestoreDto dto = ds.toObject(ClientFirestoreDto.class);
                        List<String> routineIds = dto.getRoutines();
                        if (routineIds == null) routineIds = new ArrayList<>();

                        if (!routineIds.contains(routineId)) {
                            routineIds.add(routineId);
                        }

                        dto.setRoutines(routineIds);
                        transaction.set(docRef, dto);
                        return null;
                    }).addOnFailureListener(emitter::onError)
                    .addOnSuccessListener(unused -> emitter.onComplete());
        });
    }

    @Override
    public Completable removeRoutine(ClientId clientId, String routineId) {
        return Completable.create(emitter -> {
            DocumentReference docRef = db.collection(CLIENTS_COLLECTION_NAME).document(clientId.toString());

            docRef.get()
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error getting client data", e)))
                    .addOnSuccessListener(ds -> {
                        if (!ds.exists()) {
                            emitter.onError(new Throwable("Client not found"));
                            return;
                        }

                        ClientFirestoreDto dto = ds.toObject(ClientFirestoreDto.class);
                        if (dto == null || dto.getRoutines() == null) {
                            emitter.onError(new Throwable("Malformed client data"));
                            return;
                        }

                        List<String> routines = dto.getRoutines();
                        boolean removed = routines.removeIf(id -> id.equals(routineId));

                        if (!removed) {
                            emitter.onComplete();
                            return;
                        }

                        dto.setRoutines(routines);
                        docRef.set(dto)
                                .addOnFailureListener(updateEx -> emitter.onError(new Throwable("Error updating routine list", updateEx)))
                                .addOnSuccessListener(unused -> emitter.onComplete());
                    });
        });
    }

    @Override
    public Completable setRoutinesForDay(ClientId clientId, String dayId, List<String> routineId) {
        return Completable.create(emitter -> {
            DocumentReference clientRef = db.collection(CLIENTS_COLLECTION_NAME).document(clientId.toString());

            db.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(clientRef);
                        if (!snapshot.exists()) {
                            throw new IllegalStateException("Client not found");
                        }

                        ClientFirestoreDto clientDto = snapshot.toObject(ClientFirestoreDto.class);
                        Map<String, List<String>> dayRoutineMap = clientDto.getDayRoutineMap();
                        if (dayRoutineMap == null) {
                            dayRoutineMap = new HashMap<>();
                        }

                        // Sobrescribe la lista de rutinas para el día especificado
                        dayRoutineMap.put(dayId, routineId);

                        clientDto.setDayRoutineMap(dayRoutineMap);

                        transaction.set(clientRef, clientDto);
                        return null;
                    }).addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}