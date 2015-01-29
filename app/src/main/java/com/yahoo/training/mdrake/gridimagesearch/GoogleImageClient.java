package com.yahoo.training.mdrake.gridimagesearch;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by mdrake on 1/28/15.
 */
public class GoogleImageClient {
    private final String SERVICE_BASE;
    private final String SERVICE_VERSION;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public GoogleImageClient(Context context){
        SERVICE_BASE = context.getResources().getString(R.string.image_search_base);
        SERVICE_VERSION = context.getResources().getString(R.string.image_search_version);
    }

    RequestParams getRequestParamsFromQuery(ImageClientQuery query){
        RequestParams params = new RequestParams();

        params.add("q",query.search);
        params.add("start",Integer.toString(query.getStart()));
        params.add("rsz",Integer.toString(query.pageSize));

        return params;
    }

    public void getImages(ImageClientQuery query, AsyncHttpResponseHandler handler){
        RequestParams params = getRequestParamsFromQuery(query);
        params.add("v",SERVICE_VERSION);

        client.get(SERVICE_BASE, params, handler);
    }


}
