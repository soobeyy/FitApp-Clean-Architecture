package edu.ub.pis2425.projecte.data.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import edu.ub.pis2425.projecte.domain.entities.Client;
import edu.ub.pis2425.projecte.domain.IDataService;
import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;

public class DataService implements IDataService {
    private static final String PREFS_NAME = "AppPreferences";
    private static final String CLIENT_ID_KEY = "CLIENT_ID";
    private static DataService instance;
    private final SharedPreferences sharedPreferences;
    private ClientId clientId;

    // Constructor privado
    private DataService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Método público para obtener la instancia única
    public static synchronized DataService getInstance(Context context) {
        if (instance == null) {
            instance = new DataService(context);
        }
        return instance;
    }

    @SuppressLint("CheckResult")
    @Override
    public void initialize() {
        String clientId = sharedPreferences.getString(CLIENT_ID_KEY, null);
        if (clientId != null) {
            this.clientId = new ClientId(clientId);
        } else {
            this.clientId = null;
        }
    }

    @Override
    public ClientId getClientId() {
        return clientId;
    }

    @Override
    public void setClient(Client client) {
        this.clientId = client.getId();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (client != null && client.getId() != null) {
            editor.putString(CLIENT_ID_KEY, client.getId().getId());
        } else {
            editor.remove(CLIENT_ID_KEY);
        }
        editor.apply();
    }

    @Override
    public void clearSession() {
        clientId = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CLIENT_ID_KEY);
        editor.apply();
    }
}