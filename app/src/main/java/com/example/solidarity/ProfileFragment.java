package com.example.solidarity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.solidarity.fragments.EventsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ProfileFragment extends EventsFragment {

    public static final String TAG = "ProfileFragment";
    public static final int GET_FROM_GALLERY = 3;
    CircleImageView profImage;

    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        queryEvents();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout flayout = view.findViewById(R.id.fLayout);
        profImage = new CircleImageView(getContext());
        profImage.setImageResource(R.drawable.camera);
        flayout.addView(profImage, 300, 300);

        profImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                profImage.setImageBitmap(bitmap);
                photoFile = new File(selectedImage.getPath());
                currentUser = ParseUser.getCurrentUser();
                
                //saveUser(currentUser, photoFile);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "IOException!");
            }
        }
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

    private void saveUser(ParseUser currentUser, File photoFile) {
        ParseFile file = new ParseFile(photoFile);
        currentUser.put("profileImage", file);

        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {

                if(e != null) {
                    Log.e(TAG, "Error while saving parsefile", e);
                    Toast.makeText(getContext(), "Error while saving parsefile!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
