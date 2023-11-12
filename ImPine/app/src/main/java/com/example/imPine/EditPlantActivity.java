package com.example.imPine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;

import com.bumptech.glide.Glide;
import com.example.imPine.model.Plant;
import com.example.imPine.model.PlantResponse;
import com.example.imPine.model.UserResponse;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EditPlantActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView imageView;
    private String currentPhotoPath;
    private Uri imageUri;
    private RelativeLayout loadingPanel;
    private EditText nameEditText, heightEditText;
    private TextView nameTextView, heightTextView;
    private ImageView pineappleImageView;

    private ActivityResultCallback<ActivityResult> cameraResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                // Image captured successfully
                // Update ImageView with the image stored at `imageUri`
                Glide.with(EditPlantActivity.this).load(imageUri).into(imageView);
            } else {
                Log.e("EditPlantActivity", "Image capture failed or cancelled");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_plant_page);

        initUI();
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), cameraResultCallback);

        findViewById(R.id.btn_take_picture).setOnClickListener(v -> takePicture());
        findViewById(R.id.btn_save).setOnClickListener(v -> checkForChangesAndSave());
    }

    private void initUI() {
        nameEditText = findViewById(R.id.editPlantName);
        heightEditText = findViewById(R.id.editHeight);
        imageView = findViewById(R.id.imageView);
        loadingPanel = findViewById(R.id.loadingPanel);

        nameTextView = findViewById(R.id.editPlantName); // Ensure this ID exists in your layout
        heightTextView = findViewById(R.id.editHeight); // Ensure this ID exists in your layout

        // Extracting the intent values
        Intent intent = getIntent();
        if (intent != null) {
            String pineappleName = intent.getStringExtra("pineappleName");
            int height = intent.getIntExtra("height", 0);
            String imageURL = intent.getStringExtra("imageURL");

            nameTextView.setText(pineappleName); // Set text here
            heightTextView.setText(String.valueOf(height)); // Set text here
            Glide.with(this).load(imageURL).into(imageView);
        }
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.imPine.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException ex) {
            Log.e("EditPlantActivity", "Error occurred while creating the image file", ex);
        }
        return image;
    }

    private Uri saveImage(Bitmap bitmap) {
        File imageDir = new File(getCacheDir(), "images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        File image = new File(imageDir, "updated_plant_image.png");
        try (OutputStream out = new FileOutputStream(image)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return Uri.fromFile(image);
        } catch (IOException e) {
            Log.e("EditPlantActivity", "Error saving image", e);
            return null;
        }
    }

    private void checkForChangesAndSave() {
        getUserDetails();
    }

    private void getUserDetails() {
        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        apiService.getUser(authToken).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    getPlantDetails(response.body().getUser().getId());
                    Log.e("EditPlantActivity", "UserDetails: userID: " + response.body().getUser().getId());
                } else {
                    Log.e("EditPlantActivity", "Error getting user details");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("EditPlantActivity", "Failure getting user details", t);
            }
        });
    }

    private void getPlantDetails(String userId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        apiService.getUserPlants(authToken, userId).enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getPlants().isEmpty()) {
                    compareAndEditPlant(response.body().getPlants().get(0));
                    Log.e("EditPlantActivity", "getPlantDetails: Plant_id" + response.body().getPlants().get(0).getName() + "// " + response.body().getPlants().get(0).getPlant_id());
                } else {
                    Log.e("EditPlantActivity", "No plants found for user");
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e("EditPlantActivity", "Failure getting plant details", t);
            }
        });
    }

    private void compareAndEditPlant(Plant existingPlant) {
        String newName = nameEditText.getText().toString().trim();
        String newHeight = heightEditText.getText().toString().trim();

        boolean hasNameChanged = !existingPlant.getName().equals(newName);
        boolean hasHeightChanged = !String.valueOf(existingPlant.getHeight()).equals(newHeight);
        boolean hasImageChanged = imageUri != null && !existingPlant.getImage().equals(imageUri.toString());

        if (hasNameChanged || hasHeightChanged || hasImageChanged) {
            // Log the details
            Log.d("EditPlantActivity", "Editing Plant with ID: " + existingPlant.getPlant_id());
            Log.d("EditPlantActivity", "New Name: " + newName);
            Log.d("EditPlantActivity", "New Height: " + newHeight);
            Log.d("EditPlantActivity", "New Image Path: " + (imageUri != null ? imageUri.toString() : "null"));

            // Call the method to edit the plant
            editPlant(Integer.toString(existingPlant.getPlant_id()), newName, newHeight, imageUri);
        } else {
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
        }
    }


    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private void editPlant(String plantId, String newName, String newHeight, Uri imageUri) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);

        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), newName);
        RequestBody height = RequestBody.create(MediaType.parse("multipart/form-data"), newHeight);
        RequestBody plantIdBody = RequestBody.create(MediaType.parse("multipart/form-data"), plantId);

        MultipartBody.Part body = null;
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] fileBytes = getBytesFromInputStream(inputStream);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), fileBytes);
                body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
            } catch (IOException e) {
                Log.e("EditPlantActivity", "Error reading image file", e);
            }
        }

        Call<ResponseBody> call = apiService.editPlant(authToken, name, height, plantIdBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditPlantActivity.this, "Plant updated successfully", Toast.LENGTH_SHORT).show();
                    navigateToHomePage();
                } else {
                    handleResponseError(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("EditPlantActivity", "Error updating plant", t);
            }
        });
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResponseError(Response<ResponseBody> response) {
        String message = "An error occurred: ";
        if (response.errorBody() != null) {
            try {
                String errorBodyString = response.errorBody().string();
                Log.e("EditPlantActivity", "Error Body: " + errorBodyString); // Log the error body

                // Check if the response is JSON or HTML
                if (errorBodyString.trim().startsWith("<!DOCTYPE html>")) {
                    message += "HTML response received. Check logs for details.";
                } else {
                    JSONObject errorJson = new JSONObject(errorBodyString);
                    message += errorJson.optString("message", "Unknown error!!!");
                }
            } catch (Exception e) {
                Log.e("EditPlantActivity", "Error parsing error body", e);
            }
        } else {
            message += "Unknown error. Response code: " + response.code(); // Include response code
        }
        Toast.makeText(EditPlantActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
