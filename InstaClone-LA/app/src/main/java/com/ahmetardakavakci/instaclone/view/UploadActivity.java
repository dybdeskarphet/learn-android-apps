package com.ahmetardakavakci.instaclone.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ahmetardakavakci.instaclone.databinding.ActivityUploadBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;

    // launchers
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;

    // image
    Uri imageData;
    Bitmap selectedBitmap;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        // firebase
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        // hide if no image is uploaded
        binding.uploadButton.setVisibility(View.GONE);
        binding.editTextComment.setVisibility(View.GONE);
    }

    public void upload(View view){

        UUID uuid = UUID.randomUUID();
        String imageName = "images/" + uuid + ".jpg";

        if (imageData != null) {

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                StorageReference imageReference = firebaseStorage.getReference(imageName);
                imageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                    // info
                    String downloadUrl = uri.toString();
                    String comment = binding.editTextComment.getText().toString();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();

                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("mail",email);
                    postData.put("downloadurl",downloadUrl);
                    postData.put("comment",comment);
                    postData.put("date", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });

                    System.out.println("Download URL:" + downloadUrl);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    public void selectImg(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission is needed for gallery access!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", view1 -> {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intentToGallery);
        }

    }

    private void registerLauncher(){
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK){
                Intent intentFromResult = result.getData();
                if (intentFromResult != null){
                    imageData = intentFromResult.getData();
                    binding.selectImage.setImageURI(imageData);
                    binding.uploadButton.setVisibility(View.VISIBLE);
                    binding.editTextComment.setVisibility(View.VISIBLE);
                    /*
                    try {
                        if(Build.VERSION.SDK_INT >= 28){
                            ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                            selectedBitmap = ImageDecoder.decodeBitmap(source);
                            binding.selectImage.setImageBitmap(selectedBitmap);
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), imageData);
                            binding.selectImage.setImageBitmap(selectedBitmap);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                     */

                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

            if (result){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intentToGallery);
            } else {
                Toast.makeText(this, "Permission needed!", Toast.LENGTH_SHORT).show();
            }

        });
    }

}