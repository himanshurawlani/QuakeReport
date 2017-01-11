package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Himanshu on 11/01/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String USGS_REQUEST_URL=null;
    public static final String LOG_TAG = EarthquakeLoader.class.getName();

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(LOG_TAG,"ran onStartLoading() !");
    }

    public EarthquakeLoader(Context context, String url){
        super(context);
        USGS_REQUEST_URL=url;
        Log.v(LOG_TAG,"ran EarthquakeLoader() constructor !");
    }
    @Override
    public List<Earthquake> loadInBackground() {
        if(USGS_REQUEST_URL==null) {
            return null;
        }
        Log.v(LOG_TAG,"ran loadInBackground() !");
        return QueryUtils.fetchEarthquakeDetails(USGS_REQUEST_URL);
    }
}
