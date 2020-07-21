package com.example.solidarity;

import android.os.Bundle;
import android.util.Log;

import com.example.solidarity.fragments.EventsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends EventsFragment {

    public static final String TAG = "ProfileFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryEvents();
    }

    @Override
    protected void queryEvents() {
        super.queryEvents();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.getParseKeyAuthor());
        query.whereEqualTo(Event.getParseKeyAuthor(), ParseUser.getCurrentUser());
        query.addDescendingOrder(Event.getParseKeyCreated());

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();


            }
        });
    }
}
