package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.NoteRealm;

public class NoteDto implements Parcelable{
    private String customerId;
    private String noteId;
    private String date;
    private String data;

    public NoteDto(String noteId, String date, String data) {
        this.noteId = noteId;
        this.date = date;
        this.data = data;
    }

    public NoteDto(Parcel parcel) {
        this.customerId = parcel.readString();
        this.noteId = parcel.readString();
        this.date = parcel.readString();
        this.data = parcel.readString();
    }

    public NoteDto(NoteRealm realm) {
        this.customerId = realm.getCustomer().getCustomerId();
        this.noteId = realm.getNoteId();
        this.date = realm.getDate().toString();
        this.data = realm.getData();
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.noteId);
        dest.writeString(this.date);
        dest.writeString(this.data);
    }

    public static final Creator<NoteDto> CREATOR = new Creator<NoteDto>() {
        @Override
        public NoteDto createFromParcel(Parcel in) {
            return new NoteDto(in);
        }

        @Override
        public NoteDto[] newArray(int size) {
            return new NoteDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getDate() {
        return date;
    }

    public String getData() {
        return data;
    }

    //endregion ================== Getters =========================
}
