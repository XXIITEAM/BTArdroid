package com.ip.jmc.btardroid;

import android.os.Parcel;
import android.os.Parcelable;

public class GetData implements Parcelable {
    private String prenom;
    private String pseudo;

    protected GetData(Parcel in) {
        prenom = in.readString();
        pseudo = in.readString();
    }

    public static final Parcelable.Creator<GetData> CREATOR = new Creator<GetData>() {
        @Override
        public GetData createFromParcel(Parcel in) {
            return new GetData(in);
        }

        @Override
        public GetData[] newArray(int size) {
            return new GetData[size];
        }
    };

    public GetData(String sebastien, String seb) {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(prenom);
        dest.writeString(pseudo);
    }
}
