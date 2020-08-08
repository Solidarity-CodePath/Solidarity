package com.example.solidarity.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.solidarity.Event;
import com.example.solidarity.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private TextInputEditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivEventImage;
    private Button btnSubmit;
    private TextInputEditText etTitle;
    private EditText etLocation;
    private DatePicker dpDate;

    public String photoFileName = "photo.jpg";
    File photoFile;



    public ComposeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etDescription = view.findViewById(R.id.etDescription);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivEventImage = view.findViewById(R.id.ivEventImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etTitle = view.findViewById(R.id.etTitle);
        etLocation = view.findViewById(R.id.etLocation);
        dpDate = view.findViewById(R.id.dpDate);


        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String title = etTitle.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String location = etLocation.getText().toString();
                if (location.isEmpty()) {
                    Toast.makeText(getContext(), "Location cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date date = getDateFromDatePicker(dpDate);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date currentTime = cal.getTime();

                if (!currentTime.before(date)) {
                    Toast.makeText(getContext(), "Cannot create event prior to current date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoFile == null || ivEventImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveEvent(description, title, location, date, currentUser, photoFile);
                Toast.makeText(getContext(), "Successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }


    private void launchCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivEventImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    private void saveEvent(String description, String title, String location, Date date, ParseUser currentUser, File photoFile) {
        Event event = new Event();
        event.setDescription(description);
        ParseFile file = new ParseFile(photoFile);
        event.setImage(file);
        event.setAuthor(currentUser);
        event.setTitle(title);
        event.setLocation(location);
        event.setEventDate(date);

        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {

                if(e != null) {
                    Log.e(TAG, "Error while saving parsefile", e);
                    Toast.makeText(getContext(), "Error while saving parsefile!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                etDescription.setText("");
                etTitle.setText("");
                etLocation.setText("");
                dpDate.setFirstDayOfWeek(1);
                ivEventImage.setImageResource(0);
            }
        });

    }


}