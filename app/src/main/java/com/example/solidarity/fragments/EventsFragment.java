package com.example.solidarity.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.solidarity.EndlessRecyclerViewScrollListener;
import com.example.solidarity.Event;
import com.example.solidarity.EventsAdapter;
import com.example.solidarity.R;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;


import okhttp3.Headers;


public class EventsFragment extends Fragment {

    public static final String TAG = "EventsFragment";
    private RecyclerView rvEvents;
    protected EventsAdapter adapter;
    protected List<Event> allEvents;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    protected LocationManager locationManager;
    AsyncHttpClient client;

    private static final int REQUEST_LOCATION = 1;

    private ArrayList<Integer> distances;

    String latitude ="";
    String longitude= "";

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
        adapter = new EventsAdapter(getContext(), allEvents);

        rvEvents.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvEvents.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreEvents();
            }
        };
        rvEvents.addOnScrollListener(scrollListener);

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

    private void loadMoreEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                for (Event ev: events) {
                    getDist(latitude, longitude, ev.getLocation(), distances, ev);
                }
            }
        });
    }

    protected void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_AUTHOR);
        distances = new ArrayList<>();
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                adapter.clear();
                for (Event ev: events) {
                    getDist(latitude, longitude, ev.getLocation(), distances, ev);
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void getDist(String fromLat, String fromLong, String toAddress,
                        final ArrayList<Integer> distances, final Event event) {
        String URL_4 = "https://maps.googleapis.com/maps/api/distancematrix/json?uni" +
                "ts=imperial&origins=" + fromLat + "," + fromLong + "&destinations=" +
                toAddress + "&key=AIzaSyCeI6luFfONrF0uzgb6YE68es1wnnOEmjE";
        client.get(URL_4, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray dist = (JSONArray) jsonObject.get("rows");
                    JSONObject obj2 = (JSONObject) dist.get(0);
                    JSONArray disting = (JSONArray) obj2.get("elements");
                    JSONObject obj3 = (JSONObject) disting.get(0);
                    JSONObject obj4 = (JSONObject) obj3.get("distance");
                    String distance = (String) obj4.get("text");
                    if (distance.contains(".")) {
                        distance = distance.substring(0, distance.indexOf("."));
                    }
                    distance = distance.replaceAll(",", "");
                    int dis = 0;
                    if (distance.contains(" ")) {
                        dis = Integer.valueOf(distance.substring(0, distance.indexOf(" ")));
                    } else {
                        dis = Integer.valueOf(distance);
                    }
                    if (dis <= 60) {
                        adapter.addEvent(event);
                        adapter.notifyDataSetChanged();
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