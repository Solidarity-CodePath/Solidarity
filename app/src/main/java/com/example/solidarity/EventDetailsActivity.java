package com.example.solidarity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.widget.ShareActionProvider;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.solidarity.fragments.EventsFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class EventDetailsActivity extends AppCompatActivity {

    public static final String TAG = "EventDetailsActivity";
    Event event;
    TextView tvTitleDetails;
    ImageView ivImageDetails;
    TextView tvUsernameDetails;
    TextView tvDescriptionDetails;
    TextView tvLocationDetails;
    TextView tvDateDetails;
    Button btnLike;
    Button btnGoing;
    TextView tvLikes;
    TextView tvGoing;
    int currLikes;
    ArrayList<String> UserLikes;
    int currGoing;
    ArrayList<String> goingList;
    ParseUser currentUser;
    int result = RESULT_CANCELED;
    int position;

    protected LocationManager locationManager;
    public String latitude ="";
    public String longitude= "";
    private static final int REQUEST_LOCATION = 1;

    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        tvTitleDetails = findViewById(R.id.tvTitleDetails);
        tvUsernameDetails = findViewById(R.id.tvUsernameDetails);
        ivImageDetails = findViewById(R.id.ivImageDetails);
        tvDescriptionDetails = findViewById(R.id.tvDescriptionDetails);
        tvLocationDetails = findViewById(R.id.tvLocationDetails);
        tvDateDetails = findViewById(R.id.tvDateDetails);
        btnLike = findViewById(R.id.btnLike);
        btnGoing = findViewById(R.id.btnGoing);
        tvLikes = findViewById(R.id.tvLikes);
        tvGoing = findViewById(R.id.tvGoing);
        currentUser = ParseUser.getCurrentUser();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location access not provided");
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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

        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        position = getIntent().getIntExtra("position", -1);

        ParseFile image = event.getImage();
        if (image != null) {
            Glide.with(this).load(event.getImage().getUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    prepareShareIntent(((BitmapDrawable) resource).getBitmap());
                    attachShareIntentAction();
                    return false;
                }
            }).into(ivImageDetails);
        }

        tvTitleDetails.setText(event.getTitle());
        tvUsernameDetails.setText("Created By: " + event.getAuthor().getUsername());
        tvDescriptionDetails.setText(event.getDescription());
        tvLocationDetails.setText(event.getLocation());
        tvDateDetails.setText(Event.getRelativeTimeAgo(event.getEventDate().toString()));

        tvLocationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH,"geo:%s,%s?q=%s", latitude, longitude, tvLocationDetails.getText()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        if (event.getUserLikes() == null) {
            currLikes = 0;
        } else {
            currLikes = event.getUserLikes().size();
            UserLikes = event.getUserLikes();
            if (UserLikes.contains(currentUser.getObjectId())) {
                btnLike.setBackgroundResource(R.drawable.like);
            }
        }
        tvLikes.setText(String.valueOf(currLikes) + " likes");

        if (event.getGoingList() == null) {
            currGoing = 0;
        } else {
            currGoing = event.getGoingList().size();
            goingList = event.getGoingList();
            if (goingList.contains(currentUser.getObjectId())) {
                btnGoing.setBackgroundResource(R.drawable.check_filled);
            }
        }
        tvGoing.setText(String.valueOf(currGoing) + " going");

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserLikes == null) {
                    UserLikes = new ArrayList<>();
                    UserLikes.add(currentUser.getObjectId());
                    event.setLikes(UserLikes);
                    event.saveInBackground();
                    currLikes += 1;
                    tvLikes.setText(String.valueOf(currLikes) + " likes");
                    btnLike.setBackgroundResource(R.drawable.like);
                }

                else if (!UserLikes.contains(currentUser.getObjectId())) {
                    UserLikes.add(currentUser.getObjectId());
                    event.setLikes(UserLikes);
                    event.saveInBackground();
                    currLikes += 1;
                    tvLikes.setText(String.valueOf(currLikes) + " likes");
                    btnLike.setBackgroundResource(R.drawable.like);
                }
                else {
                    UserLikes.remove(currentUser.getObjectId());
                    event.setLikes(UserLikes);
                    event.saveInBackground();
                    currLikes -= 1;
                    tvLikes.setText(String.valueOf(currLikes) + " likes");
                    btnLike.setBackgroundResource(R.drawable.like_empty);
                }
                result = RESULT_OK;
            }
        });

        btnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goingList == null) {
                    goingList = new ArrayList<>();
                    goingList.add(currentUser.getObjectId());
                    event.setGoingList(goingList);
                    event.saveInBackground();
                    currGoing += 1;
                    tvGoing.setText(String.valueOf(currGoing) + " going");
                    btnGoing.setBackgroundResource(R.drawable.check_filled);
                }

                else if (!goingList.contains(currentUser.getObjectId())) {
                    goingList.add(currentUser.getObjectId());
                    event.setGoingList(goingList);
                    event.saveInBackground();
                    currGoing += 1;
                    tvGoing.setText(String.valueOf(currGoing) + " going");
                    btnGoing.setBackgroundResource(R.drawable.check_filled);
                }
                else {
                    goingList.remove(currentUser.getObjectId());
                    event.setGoingList(goingList);
                    event.saveInBackground();
                    currGoing -= 1;
                    tvGoing.setText(String.valueOf(currGoing) + " going");
                    btnGoing.setBackgroundResource(R.drawable.correct);
                }
                result = RESULT_OK;
            }
        });

    }

    public void prepareShareIntent(Bitmap drawableImage) {
        // Fetch Bitmap Uri locally
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            drawableImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri bmpUri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", file);
        // Construct share intent as described above based on bitmap
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, tvTitleDetails.getText().toString() + ", " + tvLocationDetails.getText().toString() + ", " + tvDateDetails.getText().toString());
        shareIntent.setType("image/*");
    }

    // Attaches the share intent to the share menu item provider
    public void attachShareIntentAction() {
        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        attachShareIntentAction();
        // Return true to display menu
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
        intent.putExtra("position", position);
        setResult(result, intent);
        finish();
        super.onBackPressed();
    }
}