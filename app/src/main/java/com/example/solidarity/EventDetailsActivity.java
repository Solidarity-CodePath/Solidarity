package com.example.solidarity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    TextView tvTitleDetails;
    ImageView ivImageDetails;
    TextView tvUsernameDetails;
    TextView tvDescriptionDetails;
    TextView tvLocationDetails;
    TextView tvDateDetails;

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

    }
}