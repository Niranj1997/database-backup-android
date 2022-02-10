package com.hustlers.backup.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableUtility {
    /**
     * Converts a parcelable object to byte array
     *
     * @param parcelable object to be converted as byte array
     * @return byte array of a parcelable object
     */
    public static byte[] marshall ( Parcelable parcelable ) {
        Parcel parcel = Parcel.obtain ();
        parcelable.writeToParcel ( parcel, 0 );
        byte[] bytes = parcel.marshall ();
        parcel.recycle ();
        return bytes;
    }

    /**
     * Creates a parcel from byte array
     *
     * @param data byte array to be converted as parcel
     * @return parcel of a byte array
     */
    private static Parcel unMarshall ( byte[] data ) {
        Parcel parcel = Parcel.obtain ();
        parcel.unmarshall ( data, 0, data.length );
        parcel.setDataPosition ( 0 );
        return parcel;
    }

    /**
     * Creates a parcelable object from byte array
     *
     * @param data    byte array to be converted as parcelable object
     * @param creator creator to be used for creating parcelable object
     * @param <T>     object type of the creator
     * @return parcelable object from byte array
     */
    public static <T> T unMarshall ( byte[] data, Parcelable.Creator<T> creator ) {
        Parcel parcel = unMarshall ( data );
        T result = creator.createFromParcel ( parcel );
        parcel.recycle ();
        return result;
    }
}
