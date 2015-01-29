package com.yahoo.training.mdrake.gridimagesearch;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdrake on 1/28/15.
 */
public class ImageResult implements Parcelable{
    public String fullUrl;
    public String thumbUrl;
    public String title;
    public int width;
    public int height;

    public ImageResult(JSONObject json) throws JSONException{
        fullUrl = json.getString("url");
        thumbUrl = json.getString("tbUrl");
        width = json.getInt("width");
        height = json.getInt("height");
        title = json.getString("title");
    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) throws JSONException {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for(int i =0;i < array.length();i++){
            results.add(new ImageResult(array.getJSONObject(i)));
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ImageResult> CREATOR
            = new Parcelable.Creator<ImageResult>() {
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }

        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };

    private ImageResult(Parcel in) {
        fullUrl = in.readString();
        thumbUrl = in.readString();
        title = in.readString();
        width = in.readInt();
        height = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullUrl);
        dest.writeString(thumbUrl);
        dest.writeString(title);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
