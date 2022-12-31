package com.ahmetardakavakci.turkeylandmarks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ahmetardakavakci.turkeylandmarks.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        Landmark clickedLandmark = (Landmark) intent.getSerializableExtra("landmark");

        /*
        Singleton singleton = Singleton.getInstance();
        Landmark clickedLandmark = singleton.getSentLandmark();
         */

        binding.nameLandmark.setText(clickedLandmark.name);
        binding.descLandmark.setText(clickedLandmark.desc);
        binding.imageLandmark.setImageResource(clickedLandmark.image);


    }
}