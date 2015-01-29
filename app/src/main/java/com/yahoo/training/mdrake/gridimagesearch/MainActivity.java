package com.yahoo.training.mdrake.gridimagesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private EditText etQuery;
    private GridView gvResults;
    ImageClientQuery query;
    ImageClientQuery lastQuery;
    GoogleImageClient client;
    private ArrayList<ImageResult> imageResults;
    ImageResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageResults = new ArrayList<ImageResult>();
        query = new ImageClientQuery();
        lastQuery = null;
        client = new GoogleImageClient(this);
        setupViews();
        adapter  = new ImageResultsAdapter(MainActivity.this,imageResults);

        gvResults.setAdapter(adapter);

    }

    void parseResults(JSONArray json, int currentPageIndex, int totals) throws JSONException {
        if(!query.isEnumeration()){
            adapter.clear();
        }

        adapter.addAll(ImageResult.fromJSONArray(json));

        query.setCursorIndexFromCurrentPage(currentPageIndex,totals);


        Log.v("JSON","ADDED ALL");
    }

    ProgressDialog barProgressDialog = null;

    void showLoading(){
        barProgressDialog = new ProgressDialog(MainActivity.this);
        barProgressDialog.setTitle("Searching ...");

        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();
    }

    void hideLoading(){
        if(barProgressDialog != null){
            barProgressDialog.hide();
        }
    }

    void fetchAndDisplayImages(){
        showLoading();
        client.getImages(query, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.v("JSON","SUCCESS");
                hideLoading();
                try {
                    JSONObject responseData = json.getJSONObject("responseData");
                    JSONObject cursor = responseData.getJSONObject("cursor");
                    parseResults(responseData.getJSONArray("results"),
                            cursor.getInt("currentPageIndex"),
                            cursor.getInt("estimatedResultCount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                Log.v("JSON","FAIL");
                hideLoading();
                try {
                    Log.v("JSON",errorResponse.toString(4));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    void setupViews(){

        etQuery = (EditText)findViewById(R.id.et_search);
        gvResults = (GridView)findViewById(R.id.gv_results);

        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuery();
                if(query.isValid()){
                    fetchAndDisplayImages();
                }
            }
        });


        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                query.setPage(page);
                fetchAndDisplayImages();
            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent largeView = new Intent(MainActivity.this, ImageDisplayLarge.class);

                ImageResult result = imageResults.get(position);

                largeView.putExtra("result",result);

                startActivity(largeView);

            }
        });
    }

    void updateQuery(){
        query.search = etQuery.getText().toString();
        query.clearCursor();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
