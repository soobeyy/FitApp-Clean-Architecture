package edu.ub.pis2425.projecte.data.repositories.firestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.DayFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.DayMapper;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Routine;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class DayFirestoreRepository implements DayRepository {
    private static final String DAY_COLLECTION_NAME = "days";
    private final FirebaseFirestore db;

    public DayFirestoreRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public Completable add(Day day) {
        return Completable.create(emitter -> {
            DayFirestoreDto dto = DayMapper.dayToDayFirestoreDto(day);

            DocumentReference dayRef = db.collection(DAY_COLLECTION_NAME).document(day.getId().toString());
            dayRef.set(dto)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error adding day", e)));
        });
    }


    @Override
    public Observable<Day> getById(DayId id) {
        return Observable.create(emitter -> {
            db.collection(DAY_COLLECTION_NAME)
                    .document(id.toString())
                    .get()
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Database error", e)))
                    .addOnSuccessListener(ds -> {
                        if (!ds.exists()) {
                            emitter.onError(new Throwable("Day not found"));
                            return;
                        }

                        DayFirestoreDto dto = ds.toObject(DayFirestoreDto.class);
                        Day day = DayMapper.dayFirestoreDtoToDay(dto);
                        emitter.onNext(day);
                        emitter.onComplete();
                    });
        });
    }

    @Override
    public Observable<List<Day>> getById(List<DayId> ids) {
        return Observable.create(emitter -> {
            if (ids == null || ids.isEmpty()) {
                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
                return;
            }

            List<String> idStrings = ids.stream()
                    .map(DayId::toString)
                    .collect(Collectors.toList());

            db.collection(DAY_COLLECTION_NAME)
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), idStrings)
                    .get()
                    .addOnSuccessListener(query -> {
                        List<Day> days = new ArrayList<>();
                        List<DayFirestoreDto> dtos = query.toObjects(DayFirestoreDto.class);

                        for (DayFirestoreDto dto : dtos) {
                            Day day = DayMapper.dayFirestoreDtoToDay(dto);
                            days.add(day);
                        }

                        emitter.onNext(days);
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error fetching days", e)));
        });
    }

    @Override
    public Completable addRoutinesToDay(String dayId, List<String> routines) {
        return Completable.create(emitter -> {
            db.collection("days").document(dayId)
                    .update("routines", routines)
                    .addOnSuccessListener(unused -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}