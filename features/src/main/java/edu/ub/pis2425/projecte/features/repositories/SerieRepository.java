package edu.ub.pis2425.projecte.features.repositories;

import java.util.List;

import edu.ub.pis2425.projecte.domain.entities.Serie;
import edu.ub.pis2425.projecte.domain.valueobjects.SerieId;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface SerieRepository {

    Completable add(Serie serie);
    Observable<Serie> getById(SerieId id);
    Observable<List<Serie>> getById(List<SerieId> ids);
    Completable remove(SerieId id);
    Completable update(Serie serie);
}
