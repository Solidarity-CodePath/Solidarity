package com.example.solidarity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileActivity";
    ParseUser currentUser;
    CircleImageView editProfileImage;
    EditText editUsername;
    EditText editEmail;
    Button btnSave;

    public static final int GET_FROM_GALLERY = 3;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editProfileImage = findViewById(R.id.editProfileImage);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSaveProfile);
        currentUser = ParseUser.getCurrentUser();
        ParseFile pImage = currentUser.getParseFile("profileImage");
        if (pImage != null) {
            Glide.with(this).load(pImage.getUrl()).into(editProfileImage);
        } else {
            editProfileImage.setBackgroundResource(R.drawable.camera);
        }

        editUsername.setText(currentUser.getUsername());

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser(currentUser, photoFile);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                editProfileImage.setBackgroundResource(0);
                editProfileImage.setImageBitmap(bitmap);
                photoFile = new File(this.getCacheDir(), "profileImage");
                photoFile.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException!");
            }
        }
    }


    private void saveUser(ParseUser currentUser, File photoFile) {
            if (photoFile != null) {
                ParseFile file = new ParseFile(photoFile);
                currentUser.put("profileImage", file);

                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e != null) {
                            Log.e(TAG, "Error while saving parsefile", e);
                            Toast.makeText(EditProfileActivity.this, "Error while saving parsefile!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (!editUsername.getText().equals("")) {
                currentUser.setUsername(editUsername.getText().toString());
            }
            if (!editEmail.getText().equals("")) {
                currentUser.setEmail(editEmail.getText().toString());
            }

            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving updated user", e);
                        Toast.makeText(EditProfileActivity.this, "Error while saving updated user!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(EditProfileActivity.this, "Save Successful!", Toast.LENGTH_SHORT).show();

                }
            });
    }
}