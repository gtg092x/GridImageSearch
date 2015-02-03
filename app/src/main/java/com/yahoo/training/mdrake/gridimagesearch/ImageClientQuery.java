package com.yahoo.training.mdrake.gridimagesearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mdrake on 1/28/15.
 */
public class ImageClientQuery implements Parcelable {
    public String search;
    public int pageSize = 8;

    public QueryCursor cursor = null;

    public ImageClientQuery(){}

    public boolean isValid(){
        return search != null && search.trim().length()>0;
    }

    public boolean isEnumeration(){
        return cursor != null;
    }

    public int iterateCursor(){
        if(cursor == null){
            cursor = new QueryCursor(0,0);
        }else if(!hasNext()){
            return -1;
        }
        cursor.index ++;
        return cursor.index;
    }

    public void setPage(int i){
        cursor.index = i;
    }

    public boolean hasNext(){
        return cursor == null ||
                cursor.total > (cursor.index*pageSize);
    }

    public void clearCursor(){
        cursor = null;
    }

    public int getStart(){
        if(cursor == null){
            return 0;
        }
        return cursor.index * pageSize;
    }

    public void setCursorIndexFromCurrentPage(int newIndex, int total){
        cursor = new ImageClientQuery.QueryCursor(newIndex, total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ImageClientQuery> CREATOR
            = new Parcelable.Creator<ImageClientQuery>() {
        public ImageClientQuery createFromParcel(Parcel in) {
            return new ImageClientQuery(in);
        }

        public ImageClientQuery[] newArray(int size) {
            return new ImageClientQuery[size];
        }
    };

    private ImageClientQuery(Parcel in) {
        imageSize = in.readString();
        imageColor = in.readString();
        imageType = in.readString();
        imgSite = in.readString();
        search = in.readString();
        pageSize = in.readInt();

        if(in.dataAvail() > 0){
            cursor = new QueryCursor(in.readInt(),in.readInt());
        }
    }

/*
Size (small, medium, large, extra-large)
Color filter (black, blue, brown, gray, green, etc...)
Type (faces, photo, clip art, line art)
Site (espn.com)
 */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageSize);
        dest.writeString(imageColor);
        dest.writeString(imageType);
        dest.writeString(imgSite);
        dest.writeString(search);
        dest.writeInt(pageSize);
        if(cursor != null) {
            dest.writeInt(cursor.index);
            dest.writeInt(cursor.total);
        }
    }

    String imageSize;
    String imageColor;
    String imageType;
    String imgSite;


    public class QueryCursor{
        public int index = 0;
        public int total = 0;
        public QueryCursor(int currentPageIndex, int total){
            index = currentPageIndex;
            this.total = total;
        }
    }

}
