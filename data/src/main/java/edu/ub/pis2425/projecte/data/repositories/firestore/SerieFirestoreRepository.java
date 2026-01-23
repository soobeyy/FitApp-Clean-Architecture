package edu.ub.pis2425.projecte.data.repositories.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.pis2425.projecte.data.dtos.firestore.SerieFirestoreDto;
import edu.ub.pis2425.projecte.data.dtos.mappers.SerieMapper;
import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.SerieId;
import edu.ub.pis2425.projecte.features.repositories.SerieRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class SerieFirestoreRepository implements SerieRepository {
    /* Constants */
    private static final String SERIE_COLLECTION_NAME = "series";
    /* Attributes */
    private final FirebaseFirestore db;

    /**
     * Empty constructor
     */
    public SerieFirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Add a serie to the Firebase CloudFirestore.
     *
     * @param serie The serie to add.
     */
    public Completable add(Serie serie) {
        return Completable.create(emitter -> {
            SerieFirestoreDto serieDto = SerieMapper.INSTANCE.serieToSerieFirestoreDto(serie);

            db.collection(SERIE_COLLECTION_NAME)
                    .add(serieDto)
                    .addOnFailureListener(exception -> {
                        emitter.onError(new Throwable("Error adding serie", exception));
                    })
                    .addOnSuccessListener(documentReference -> {
                        String generatedId = documentReference.getId();
                        serie.setId(new SerieId(generatedId));

                        // Actualiza el campo "id" dentro del mismo documento
                        documentReference.update("id", generatedId)
                                .addOnFailureListener(updateException -> {
                                    emitter.onError(new Throwable("Error updating serie ID", updateException));
                                })
                                .addOnSuccessListener(unused -> {
                                    emitter.onComplete();
                                });
                    });
        });
    }

    @Override
    public Observable<Serie> getById(SerieId id) {
        return Observable.create(emitter -> {
            Task<DocumentSnapshot> task = db
                    .collection(SERIE_COLLECTION_NAME)
                    .document(id.toString())
                    .get();
            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Database error"));
            }).addOnSuccessListener(ds -> {
                if (ds.exists()) {
                    SerieFirestoreDto serieDto = ds.toObject(SerieFirestoreDto.class);
                    Serie serie = SerieMapper.serieToSerieFirestoreDto(serieDto);
                    emitter.onNext(serie);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("Serie not found"));
                }
            });
        });
    }

    @Override
    public Observable<List<Serie>> getById(List<SerieId> ids) {
        return Observable.create(emitter -> {
            if (ids == null || ids.isEmpty()) {
                emitter.onNext(new ArrayList<>());
                emitter.onComplete();
                return;
            }

            List<String> idsString = ids
                    .stream()
                    .map(SerieId::toString)
                    .collect(Collectors.toList());

            Task<QuerySnapshot> task = db
                    .collection(SERIE_COLLECTION_NAME)
                    .whereIn(FieldPath.documentId(), idsString)
                    .get();

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Error getting series"));
            }).addOnSuccessListener(queryDocumentSnapshots -> {
                List<Serie> series = new ArrayList<>();
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                    if (ds.exists()) {
                        SerieFirestoreDto serieDto = ds.toObject(SerieFirestoreDto.class);
                        Serie serie = SerieMapper.serieToSerieFirestoreDto(serieDto);
                        series.add(serie);
                    }
                }
                emitter.onNext(series);
                emitter.onComplete();
            });
        });
    }

    @Override
    public Completable remove(SerieId id) {
        return Completable.create(emitter -> {
            Task<Void> task = db
                    .collection(SERIE_COLLECTION_NAME)
                    .document(id.toString())
                    .delete();

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Error removing serie"));
            }).addOnSuccessListener(ignored -> {
                emitter.onComplete();
            });
        });
    }

    @Override
    public Completable update(Serie serie) {
        return Completable.create(emitter -> {
            String serieId = serie.getId().toString();
            SerieFirestoreDto serieDto = SerieMapper.INSTANCE.serieToSerieFirestoreDto(serie);

            Task<Void> task = db.collection(SERIE_COLLECTION_NAME)
                    .document(serieId)
                    .set(serieDto);

            task.addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(exception -> emitter.onError(new Throwable("Error updating serie", exception)));
        });
    }
}