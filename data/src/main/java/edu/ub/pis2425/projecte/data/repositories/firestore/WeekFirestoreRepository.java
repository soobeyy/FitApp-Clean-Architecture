package edu.ub.pis2425.projecte.data.repositories.firestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.DayFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.firestore.WeekFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.DayMapper;
import edu.ub.pis2425.projecte.data.dtos.mappers.WeekMapper;
import edu.ub.pis2425.projecte.domain.entities.Day;
import edu.ub.pis2425.projecte.domain.entities.Week;
import edu.ub.pis2425.projecte.domain.valueobjects.DayId;
import edu.ub.pis2425.projecte.domain.valueobjects.WeekId;
import edu.ub.pis2425.projecte.features.repositories.DayRepository;
import edu.ub.pis2425.projecte.features.repositories.WeekRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class WeekFirestoreRepository implements WeekRepository {
    private static final String WEEK_COLLECTION_NAME = "weeks";
    private final FirebaseFirestore db;
    private final DayRepository dayRepository;

    public WeekFirestoreRepository(DayRepository dayRepository) {
        this.db = FirebaseFirestore.getInstance();
        this.dayRepository = dayRepository;
    }

    @Override
    public Completable add(Week week) {
        return Completable.create(emitter -> {
            // 2. Create a list to track all day operations
            List<Completable> dayOperations = new ArrayList<>();

            // 3. For each day, delegate to dayRepository
            for (Day day : week.getDays()) {
                dayOperations.add(dayRepository.add(day));
            }

            // 4. After all days are processed, then save the week
            Completable.concat(dayOperations)
                    .andThen(Completable.create(innerEmitter -> {
                        // Now all days have their IDs, prepare week data
                        WeekFirestoreDto weekDto = WeekMapper.weekToWeekFirestoreDto(week);
                        DocumentReference weekRef = db.collection(WEEK_COLLECTION_NAME).document(week.getId().toString());

                        // Collect day IDs
                        List<String> dayIds = week.getDays().stream()
                                .map(day -> day.getId().toString())
                                .collect(Collectors.toList());

                        weekDto.setDays(dayIds);
                        weekDto.setId(week.getId().toString());

                        // Save the week with day references
                        weekRef.set(weekDto)
                                .addOnSuccessListener(unused -> innerEmitter.onComplete())
                                .addOnFailureListener(e -> innerEmitter.onError(new Throwable("Error adding week", e)));
                    }))
                    .subscribe(
                            () -> emitter.onComplete(),
                            error -> emitter.onError(error)
                    );
        });
    }

    @Override
    public Observable<Week> getById(WeekId id) {
        return Observable.create(emitter -> {
            db.collection(WEEK_COLLECTION_NAME)
                    .document(id.toString())
                    .get()
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Database error", e)))
                    .addOnSuccessListener(ds -> {
                        if (!ds.exists()) {
                            emitter.onError(new Throwable("Week not found"));
                            return;
                        }

                        WeekFirestoreDto dto = ds.toObject(WeekFirestoreDto.class);
                        Week weekWithoutDays = WeekMapper.weekFirestoreDtoToWeekWithoutDays(dto);

                        if (dto.getDays() == null || dto.getDays().isEmpty()) {
                            emitter.onNext(weekWithoutDays);
                            emitter.onComplete();
                            return;
                        }

                        List<DayId> dayIds = dto.getDays().stream()
                                .map(DayId::new)
                                .collect(Collectors.toList());

                        dayRepository.getById(dayIds)
                                .subscribe(
                                        days -> {
                                            Week completeWeek = new Week(
                                                    weekWithoutDays.getId(),
                                                    weekWithoutDays.getStartDate(),
                                                    weekWithoutDays.getEndDate(),
                                                    days
                                            );
                                            emitter.onNext(completeWeek);
                                            emitter.onComplete();
                                        },
                                        emitter::onError
                                );
                    });
        });
    }

    @Override
    public Observable<List<Week>> getById(List<WeekId> ids) {
        return Observable.create(emitter -> {
            if (ids == null || ids.isEmpty()) {
                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
                return;
            }

            List<String> idStrings = ids.stream()
                    .map(WeekId::toString)
                    .collect(Collectors.toList());

            db.collection(WEEK_COLLECTION_NAME)
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), idStrings)
                    .get()
                    .addOnSuccessListener(query -> {
                        List<Week> weeks = new ArrayList<>();
                        List<WeekFirestoreDto> dtos = query.toObjects(WeekFirestoreDto.class);

                        for (WeekFirestoreDto dto : dtos) {
                            Week week = WeekMapper.weekFirestoreDtoToWeekWithoutDays(dto);
                            weeks.add(week);
                        }

                        emitter.onNext(weeks);
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> emitter.onError(new Throwable("Error fetching weeks", e)));
        });
    }
}