package com.example.solidarity.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.solidarity.DistanceMatrix;
import com.example.solidarity.Event;
import com.example.solidarity.EventsAdapter;
import com.example.solidarity.R;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;


public class EventsFragment extends Fragment {

    public static final String TAG = "EventsFragment";
    private RecyclerView rvEvents;
    protected EventsAdapter adapter;
    protected List<Event> allEvents;
    private SwipeRefreshLayout swipeContainer;

    protected LocationManager locationManager;
    AsyncHttpClient client;

    private static final int REQUEST_LOCATION = 1;
    private final int REQUEST_CODE = 20;


    public String latitude ="";
    public String longitude= "";
    public static String API_KEY;
    ParseUser currentUser;

    public EventsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEvents = view.findViewById(R.id.rvEvents);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        currentUser = ParseUser.getCurrentUser();

        API_KEY = getString(R.string.gmaps_apikey);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryEvents();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(getContext(), allEvents, this);

        rvEvents.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvEvents.setLayoutManager(linearLayoutManager);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location access not provided");
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationGPS != null) {
            double lat = locationGPS.getLatitude();
            double longi = locationGPS.getLongitude();
            latitude = String.valueOf(lat);
            longitude= String.valueOf(longi);
            Log.i(TAG, "Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
        } else {
            Log.e(TAG, "Unable to find location.");
        }

        client = new AsyncHttpClient();
        queryEvents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Event event = (Event) Parcels.unwrap(data.getParcelableExtra(Event.class.getSimpleName()));
            int position = data.getIntExtra("position", -1);
            adapter.set(position, event);
            adapter.notifyItemChanged(position);
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void queryEvents() {
        Date currentTime = Calendar.getInstance().getTime();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_AUTHOR);
        query.whereGreaterThan("date", currentTime);
        query.findInBackground(new FindCallback<Event>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                adapter.clear();
                ArrayList<Event> sortedEvents = (ArrayList<Event>) sortEvents((ArrayList<Event>) events);
                getDist(latitude, longitude, sortedEvents);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public List<Event> sortEvents(ArrayList<Event> events) {
        // sort events based on their popularity values and return
        Collections.sort(events, Collections.<Event>reverseOrder());
        return events;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDist(String fromLat, String fromLong, final ArrayList<Event> events) {
        ArrayList<String> locations = new ArrayList<>();
        for (Event ev: events) {
            locations.add(ev.getLocation());
        }
        String locationsJoined = String.join("|", locations);
        String api_url = String.format("https://maps.googleapis.com/maps/api/distancematrix/json?uni" +
                "ts=imperial&origins=%s,%s&destinations=%s&key=%s", fromLat, fromLong, locationsJoined, API_KEY);
        client.get(api_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray jsonArray = DistanceMatrix.getJsonArray(jsonObject);
                    List<DistanceMatrix> distanceMatrices = DistanceMatrix.fromJsonArray(jsonArray);

                    for (int i = 0; i < distanceMatrices.size(); i++) {
                        if (distanceMatrices.get(i).getDistance() < currentUser.getInt("radius")) {
                            adapter.addEvent(events.get(i));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    Log.e("TAG", "JsonObject Exception");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure");
            }
        });
    }
}