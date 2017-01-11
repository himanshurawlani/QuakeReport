package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 09/01/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> e)
    {
        super(context,0,e);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView==null)
        {
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        Earthquake listItem = getItem(position);
        TextView magnitude_text = (TextView) listItemView.findViewById(R.id.magnitude_text_view);
        DecimalFormat formatter = new DecimalFormat("0.0");
        String output = formatter.format(listItem.getMagnitude());
        magnitude_text.setText(output);

        // Setting the proper background color on the magnitude circle.
        // Fetches the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable)magnitude_text.getBackground();
        // Gets the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(listItem.getMagnitude());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);


        String location = listItem.getLocation();
        String locationOffset, primaryLocation;

        if(location.contains(LOCATION_SEPARATOR))
        {
            String[] parts = location.split(LOCATION_SEPARATOR);
            locationOffset=parts[0]+LOCATION_SEPARATOR;
            primaryLocation=parts[1];
        }
        else
        {
            locationOffset="Near the ";
            primaryLocation=location;
        }

        TextView direction_text = (TextView) listItemView.findViewById(R.id.direction_text_view);
        direction_text.setText(locationOffset);
        TextView location_text = (TextView) listItemView.findViewById(R.id.location_text_view);
        location_text.setText(primaryLocation);

        Date dateObject = new Date(listItem.getTimeInMilleSeconds());

        TextView date_text = (TextView) listItemView.findViewById(R.id.date_text_view);
        date_text.setText(DateFormat.getDateInstance().format(dateObject));

        TextView time_text = (TextView) listItemView.findViewById(R.id.time_text_view);
        time_text.setText(DateFormat.getTimeInstance().format(dateObject));

        return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
