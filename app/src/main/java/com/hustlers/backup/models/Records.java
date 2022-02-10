package com.hustlers.backup.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Records implements Parcelable {

    public static final Creator<Records> CREATOR = new Creator<Records> () {
        @Override
        public Records createFromParcel ( Parcel in ) {
            return new Records ( in );
        }

        @Override
        public Records[] newArray ( int size ) {
            return new Records[size];
        }
    };

    public List<Record> getRecords () {
        return records;
    }

    private final List<Record> records;

    protected Records ( Parcel in ) {
        records = new ArrayList<> ();
        in.readTypedList ( records, Record.CREATOR );
    }

    public Records ( List<Record> records ) {
        this.records = records;
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel ( Parcel dest, int flags ) {
        dest.writeTypedList ( records );
    }
}
