package com.hustlers.backup.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Record implements Parcelable {

    public static final Creator<Record> CREATOR = new Creator<Record> () {
        @Override
        public Record createFromParcel ( Parcel in ) {
            return new Record ( in );
        }

        @Override
        public Record[] newArray ( int size ) {
            return new Record[size];
        }
    };

    @PrimaryKey
    @NonNull
    private String uniqueId = "";
    private String name;

    protected Record ( Parcel in ) {
        uniqueId = in.readString ();
        name = in.readString ();
    }

    @Ignore
    public Record ( @NonNull String uniqueId, String name ) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public Record () {
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel ( Parcel parcel, int flags ) {
        parcel.writeString ( uniqueId );
        parcel.writeString ( name );
    }

    @NotNull
    public String getUniqueId () {
        return uniqueId;
    }

    public void setUniqueId ( @NotNull String uniqueId ) {
        this.uniqueId = uniqueId;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    @Override
    public boolean equals ( @Nullable Object obj ) {
        if (obj instanceof Record) {
            Record record = (Record) obj;
            return record.uniqueId.equals ( uniqueId );
        }
        return false;
    }

    @Override
    @Ignore
    public String toString () {
        return "[key='" + uniqueId + '\'' + ", value='" + name + '\'' + "]";
    }
}
