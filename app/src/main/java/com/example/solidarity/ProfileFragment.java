package com.example.solidarity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

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
        setHasOptionsMenu(true);
        queryEvents();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_logout, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void queryEvents() {
        super.queryEvents();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_AUTHOR);
        query.whereEqualTo(Event.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.addDescendingOrder(Event.KEY_CREATED);

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
