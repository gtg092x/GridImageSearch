package com.yahoo.training.mdrake.gridimagesearch;

/**
 * Created by mdrake on 1/28/15.
 */
public class ImageClientQuery {
    public String search;
    public int pageSize = 8;

    public QueryCursor cursor = null;

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

    public class QueryCursor{
        public int index = 0;
        public int total = 0;
        public QueryCursor(int currentPageIndex, int total){
            index = currentPageIndex;
            this.total = total;
        }
    }

}
