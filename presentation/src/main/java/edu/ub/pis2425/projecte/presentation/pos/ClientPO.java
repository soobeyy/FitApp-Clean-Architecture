package edu.ub.pis2425.projecte.presentation.pos;

import android.os.Parcel;
import android.os.Parcelable;

import edu.ub.pis2425.projecte.domain.valueobjects.ClientId;

public class ClientPO implements Parcelable {
    /* Attributes */
    private ClientId id;
    private String email;

    /* Constructors */
    public ClientPO(ClientId id, String email, String photoUrl) {
        this.id = id;
        this.email = email;
    }

    @SuppressWarnings("unused")
    public ClientPO() {
    }

    /* Setters */
    public ClientId getId() { return id; }
    public String getEmail() { return email; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.email);
    }

    public void readFromParcel(Parcel source) {
        this.id = (ClientId) source.readSerializable();
        this.email = source.readString();
    }

    protected ClientPO(Parcel in) {
        this.id = (ClientId) in.readSerializable();
        this.email = in.readString();
    }

    public static final Creator<ClientPO> CREATOR = new Creator<ClientPO>() {
        @Override
        public ClientPO createFromParcel(Parcel source) {
            return new ClientPO(source);
        }

        @Override
        public ClientPO[] newArray(int size) {
            return new ClientPO[size];
        }
    };
}
