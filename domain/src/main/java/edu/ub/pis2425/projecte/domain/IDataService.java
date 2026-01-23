package edu.ub.pis2425.projecte.domain;

import javax.naming.Context;

import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;

public interface IDataService {
    void initialize();
    ClientId getClientId();
    void setClient(Client client);
    void clearSession();
}