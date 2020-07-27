package com.example.solidarity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {
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

        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        ParseFile image = event.getImage();
        if (image != null) {
            Glide.with(this).load(event.getImage().getUrl()).into(ivImageDetails);
        }

        tvTitleDetails.setText(event.getTitle());
        tvUsernameDetails.setText(event.getAuthor().getUsername());
        tvDescriptionDetails.setText(event.getDescription());
        tvLocationDetails.setText(event.getLocation());
        tvDateDetails.setText(event.getEventDate().toString());

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
            btnGoing.setBackgroundResource(R.drawable.check_filled);
        }
        tvGoing.setText(String.valueOf(currGoing) + " going");

    }
}