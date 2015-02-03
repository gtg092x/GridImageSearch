package com.yahoo.training.mdrake.gridimagesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;

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

    GoogleImageClient client;
    private ArrayList<ImageResult> imageResults;
    ImageResultsAdapter adapter;
    ProgressBar idBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageResults = new ArrayList<ImageResult>();
        query = new ImageClientQuery();
        readAndApplyPreferences();

        client = new GoogleImageClient(this);
        setupViews();
        adapter  = new ImageResultsAdapter(MainActivity.this,imageResults);

        gvResults.setAdapter(adapter);

    }

    void readAndApplyPreferences(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        query.imageType = preferences.getString("search_type",null);
        query.imageSize = preferences.getString("search_size",null);
        query.imgSite = preferences.getString("search_domain",null);
        query.imageColor = preferences.getString("search_color",null);
    }

    void parseResults(JSONArray json, int currentPageIndex, int totals) throws JSONException {
        if(!query.isEnumeration()){
            adapter.clear();
        }

        adapter.addAll(ImageResult.fromJSONArray(json));

        query.setCursorIndexFromCurrentPage(currentPageIndex,totals);


        Log.v("JSON","ADDED ALL");
    }



    void showLoading(){
        idBar.setVisibility(View.VISIBLE);
    }

    void hideLoading(){
        idBar.setVisibility(View.INVISIBLE);
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
                    if(errorResponse!=null)
                        Log.v("JSON",errorResponse.toString(4));
                    else
                        Log.v("JSON",Integer.toString(statusCode));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    void updateAndLoadQuery(){
        updateQuery();
        if(query.isValid()){
            fetchAndDisplayImages();
        }
    }

    void setupViews(){

        etQuery = (EditText)findViewById(R.id.et_search);
        gvResults = (GridView)findViewById(R.id.gv_results);

        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.search = etQuery.getText().toString();
                updateAndLoadQuery();
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

        idBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    void updateQuery(){

        query.clearCursor();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static final int CONFIG_CODE = 201;
    public static final String CONFIG_EXTRA = "config";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(settingsIntent, CONFIG_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONFIG_CODE) {

            readAndApplyPreferences();
            updateAndLoadQuery();

        }
    }
}
