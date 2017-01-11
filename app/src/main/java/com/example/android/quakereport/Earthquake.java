package com.example.android.quakereport;

/**
 * Created by Himanshu on 09/01/2017.
 */

public class Earthquake {
    private double mMagnitude;
    private String mLocation;
    private Long mTimeInMilliSeconds;
    private String mURL;

    public Earthquake(double magnitude, String location, Long timeInMilliSeconds,String url)
    {
        mMagnitude=magnitude;
        mLocation=location;
        mTimeInMilliSeconds=timeInMilliSeconds;
        mURL=url;
    }

    public double getMagnitude()
    {return mMagnitude;}

    public String getLocation()
    {return mLocation;}

    public Long getTimeInMilleSeconds()
    {return mTimeInMilliSeconds;}

    public String getURL()
    {return mURL;}

    @Override
    public String toString() {
        return "Earthquake{" +
                "mMagnitude=" + mMagnitude +
                ", mLocation='" + mLocation + '\'' +
                ", mTimeInMilliSeconds='" + mTimeInMilliSeconds + '\'' +
                '}';
    }
}
