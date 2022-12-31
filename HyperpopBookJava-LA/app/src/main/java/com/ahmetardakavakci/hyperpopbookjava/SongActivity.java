package com.ahmetardakavakci.hyperpopbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.ahmetardakavakci.hyperpopbookjava.databinding.ActivitySongBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SongActivity extends AppCompatActivity {

    private ActivitySongBinding binding;
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        database = this.openOrCreateDatabase("Songs",MODE_PRIVATE,null);

        if (info.equals("new_song")) {
            binding.nameText.setText("");
            binding.yearText.setText("");
            binding.artistText.setText("");
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.selectImage.setImageResource(R.drawable.selectimage);
        } else {
            int songId = intent.getIntExtra("songId",0);
            binding.nameText.setEnabled(false);
            binding.yearText.setEnabled(false);
            binding.artistText.setEnabled(false);
            binding.saveButton.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM songs where id = ?", new String[] {String.valueOf(songId)});
                int songIx = cursor.getColumnIndex("name");
                int artistIx = cursor.getColumnIndex("artist");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    binding.nameText.setText(cursor.getString(songIx));
                    binding.artistText.setText(cursor.getString(artistIx));
                    binding.yearText.setText(cursor.getString(yearIx));
                    binding.selectImage.setImageBitmap(bitmap);
                }

                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("selectImage: Permission is not granted");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                System.out.println("selectImage: Permission is denied and should be asked again about it");
                Snackbar.make(view,"Permission is needed for gallery access!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("selectImage: 'Give permission' button is clicked");
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                System.out.println("selectImage: Permission is not granted but haven't been denied");
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {

            System.out.println("selectImage: No need for permissions.");
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intentToGallery);

        }

    }

   public Bitmap minimizeImage(Bitmap image, int maximumSize){

       int width = image.getWidth();
       int height = image.getHeight();

       float bitmapRatio = (float) width / (float) height;

       if (bitmapRatio > 1) {
           width = maximumSize;
           height = (int) (width / bitmapRatio);
       } else {
           height = maximumSize;
           width = (int) (height * bitmapRatio);
       }

       return Bitmap.createScaledBitmap(image,width,height,true);

   }

    public void saveImage(View view){
        String name = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String releaseYear = binding.yearText.getText().toString();

        Bitmap smallImage = minimizeImage(selectedImage, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS songs(id INTEGER PRIMARY KEY, name VARCHAR, artist VARCHAR, year VARCHAR, image BLOB)");

            String sqlInsertString = "INSERT INTO songs (name, artist, year, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlInsertString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artistName);
            sqLiteStatement.bindString(3,releaseYear);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(SongActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void registerLauncher() {

        galleryLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        // binding.selectImage.setImageURI(imageData);

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(SongActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.selectImage.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(SongActivity.this.getContentResolver(),imageData);
                                binding.selectImage.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher =
		registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    // permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryLauncher.launch(intentToGallery);
                } else {
                    // permission denied
                    Toast.makeText(SongActivity.this,"Permission denied!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

}
