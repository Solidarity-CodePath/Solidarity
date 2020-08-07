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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.solidarity.fragments.EventsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends EventsFragment {

    public static final String TAG = "ProfileFragment";
    public static final int GET_FROM_GALLERY = 3;
    CircleImageView profImage;
    private SwipeRefreshLayout swipeContainer;
    private final int REQUEST_CODE = 20;


    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseUser currentUser;

    private RecyclerView rvEvents;
    protected EventsAdapter adapter;
    private CardView cardEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout flayout = view.findViewById(R.id.fLayout);
        profImage = view.findViewById(R.id.ivProfileImage);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        rvEvents = view.findViewById(R.id.rvEvents);
        cardEvents = view.findViewById(R.id.cardEvents);

        flayout.removeView(cardEvents);


        profImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                return false;
            }
        });

        currentUser = ParseUser.getCurrentUser();
        ParseFile pImage = currentUser.getParseFile("profileImage");
        if (pImage != null) {
            Glide.with(getContext()).load(pImage.getUrl()).into(profImage);
        } else {
            profImage.setBackgroundResource(R.drawable.camera);
        }

        allEvents = new ArrayList<>();
        adapter = new EventsAdapter(getContext(), allEvents, this);
        rvEvents.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvEvents.setLayoutManager(linearLayoutManager);

        queryEvents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            System.out.println("Entering onActvityResult in ProfileFragment");
            queryEvents();
            adapter.notifyDataSetChanged();
        }

        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                profImage.setBackgroundResource(0);
                profImage.setImageBitmap(bitmap);
                photoFile = new File(getContext().getCacheDir(), "profileImage");
                photoFile.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                saveUser(currentUser, photoFile);

            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found!");
                e.printStackTrace();
            } catch (IOException e) {
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
        if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            getContext().startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void queryEvents() {
        Date currentTime = Calendar.getInstance().getTime();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_AUTHOR);
        query.whereEqualTo(Event.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.whereGreaterThan("date", currentTime);
        query.addDescendingOrder(Event.KEY_CREATED);

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                adapter.clear();
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

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
