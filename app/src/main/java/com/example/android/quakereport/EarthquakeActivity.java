/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL="http://earthquake.usgs.gov/fdsnws/event/1/query";
    private ArrayList<Earthquake> earthquakes;
    private EarthquakeAdapter mAdapter;
    private ListView earthquakeListView;
    private TextView emptyView;
    private ProgressBar pb;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        Log.v(LOG_TAG,"EarthquakeActivity onCreate() !");


        /*
        EarthquakeAsyncTask searchEarthquakes =new EarthquakeAsyncTask();
        searchEarthquakes.execute(USGS_REQUEST_URL);
        */

        // Checking Network Status
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        pb= (ProgressBar)findViewById(R.id.loading_spinner);
        emptyView = (TextView) findViewById(R.id.no_earthquakes_found);
        emptyView.setVisibility(View.GONE);

        if(isConnected) {
            // Find a reference to the {@link ListView} in the layout
            earthquakeListView = (ListView) findViewById(R.id.list);
            // Create a new adapter that takes an empty list of earthquakes as input
            mAdapter = new EarthquakeAdapter(EarthquakeActivity.this, new ArrayList<Earthquake>());
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(mAdapter);

            getLoaderManager().initLoader(0, null, EarthquakeActivity.this);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                      Earthquake obj = earthquakes.get(i);
                      String url = obj.getURL();
                      try {
                          Intent in = new Intent(Intent.ACTION_VIEW);
                          in.setData(Uri.parse(url));
                          startActivity(in);
                      } catch (ActivityNotFoundException e) {
                          Toast.makeText(EarthquakeActivity.this, "No application can handle this request."
                                  + " Please install a web browser", Toast.LENGTH_LONG).show();
                          e.printStackTrace();
                      }
                  }
              }
            );
        }else {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No internet connection");
            pb.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "30");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        Log.v(LOG_TAG,"ran onCreateLoader() !");
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquake) {

        pb.setVisibility(View.GONE);

        earthquakeListView.setEmptyView(emptyView);
        emptyView.setVisibility(View.VISIBLE);

        if(earthquake!=null && !earthquake.isEmpty()) {
            mAdapter.clear();
            // Create a new {@link ArrayAdapter} of earthquakes
            mAdapter = new EarthquakeAdapter(EarthquakeActivity.this, (ArrayList) earthquake);
            this.earthquakes=(ArrayList)earthquake;
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(mAdapter);
        }
        Log.v(LOG_TAG,"ran onLoadFinished() !");
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();
        mAdapter = new EarthquakeAdapter(EarthquakeActivity.this, new ArrayList<Earthquake>());
        Log.v(LOG_TAG,"ran onLoaderReset() !");
    }

    /*
    public class EarthquakeAsyncTask extends AsyncTask<String, Void , List<Earthquake>>{

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if(urls.length<1 || urls[0]==null) {
                return null;
            }
            return QueryUtils.fetchEarthquakeDetails(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {

            if(earthquakes!=null && !earthquakes.isEmpty()) {
                mAdapter.clear();
                // Create a new {@link ArrayAdapter} of earthquakes
                mAdapter = new EarthquakeAdapter(EarthquakeActivity.this, (ArrayList) earthquakes);

                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                earthquakeListView.setAdapter(mAdapter);
            }
        }
    }
    */
}
