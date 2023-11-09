package com.example.imPine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;

import com.example.imPine.model.Plant;
import com.example.imPine.network.ApiInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakePlantActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView imageView;
    private String currentPhotoPath;
    private Uri imageUri;

    private ActivityResultCallback<ActivityResult> cameraResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Log.d("MakePlantActivity", "Image capture successful");
                Bundle extras = result.getData().getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                // Save the bitmap as a file and get the path
                imageUri = saveImage(imageBitmap);
            } else {
                // Handle other cases...
            }
        }
    };

    private Uri saveImage(Bitmap bitmap) {
        // Use the application's cache directory for saving the image
        File imageDir = new File(getCacheDir(), "images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        File image = new File(imageDir, "pineappleProfile.png");
        try (OutputStream out = new FileOutputStream(image)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return Uri.fromFile(image);
        } catch (IOException e) {
            Log.e("MakePlantActivity", "Error saving image", e);
            return null;
        }
    }

    private void getAuthToken(AuthTokenCallback authTokenCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            authTokenCallback.onTokenReceived("Bearer " + idToken);
                        } else {
                            authTokenCallback.onTokenError(task.getException());
                        }
                    });
        } else {
            authTokenCallback.onTokenError(new Exception("User not logged in."));
        }
    }

    public interface AuthTokenCallback {
        void onTokenReceived(String authToken);
        void onTokenError(Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_plant_page);

        LinearLayout mainLayout = findViewById(R.id.mainLayout);

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus(); // Optional: Clear focus from the current EditText
                }
                return false;
            }
        });

        imageView = findViewById(R.id.imageView);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d("MakePlantActivity", "Image capture successful");
                        // No need to get extras, image should be saved to the file
                        try {
                            setPic();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.d("MakePlantActivity", "Image capture cancelled by user");
                    } else {
                        Log.e("MakePlantActivity", "Image capture failed with result code: " + result.getResultCode());
                    }
                });

        findViewById(R.id.btn_take_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            EditText nameEditText = findViewById(R.id.editPlantName);
            String plantName = nameEditText.getText().toString();
            EditText heightEditText = findViewById(R.id.editHeight);
            String heightString = heightEditText.getText().toString();

            Plant plant = (new Plant(plantName, Integer.parseInt(heightString)));
            getAuthToken(new AuthTokenCallback() {
                @Override
                public void onTokenReceived(String authToken) {
                    createPlant(plant, authToken);
                }

                @Override
                public void onTokenError(Exception e) {
                    Log.e("MakePlantActivity", "Authentication error", e);
                }
            });
        });
    }

    private void createPlant(Plant plant, String authToken) {
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
        // Log the URL here
        Log.d("MakePlantActivity", "Attempting to create plant at URL: " + RetrofitClient.getBaseUrl() + "/api/plant/");
        Log.d("MakePlantActivity", "Token: " + authToken);
        Call<Plant> call = apiService.createPlant(authToken, plant);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MakePlantActivity.this, "Plant saved!", Toast.LENGTH_SHORT).show();
                    navigateToHomePage();
                } else {
//                    navigateToHomePage();
                    handleResponseError(response);
                }
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {
                Toast.makeText(MakePlantActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MakePlantActivity", "Network error", t);
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("MakePlantActivity", "Error occurred while creating the image file", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "your.package.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(selectedImage);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void setPic() throws IOException {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        bitmap = rotateImageIfRequired(bitmap, currentPhotoPath);

        imageView.setImageBitmap(bitmap);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomePage() {
//        if (imageUri != null) {
//            Intent intent = new Intent(this, HomePageActivity.class);
//            intent.putExtra("profile_image_path", imageUri.toString());
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Error: Image not saved", Toast.LENGTH_SHORT).show();
//        }
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResponseError(Response<Plant> response) {
        String message = "An error occurred: ";
        if (response.errorBody() != null) {
            try {
                String errorBodyString = response.errorBody().string();
                // If the response is HTML, log the entire response for debugging
                if (errorBodyString.trim().startsWith("<!DOCTYPE html>")) {
                    Log.e("MakePlantActivity", "HTML response received, full error page: " + errorBodyString);
                    message += "HTML response received. Check logs for details.";
                } else {
                    // Try to parse it as JSON
                    JSONObject errorJson = new JSONObject(errorBodyString);
                    message += errorJson.optString("message", "Unknown error");
                }
            } catch (Exception e) {
                Log.e("MakePlantActivity", "Error parsing error body", e);
            }
        } else {
            message += "Unknown error";
        }
        Toast.makeText(MakePlantActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
