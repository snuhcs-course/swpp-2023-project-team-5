package com.imPine.imPineThankYou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;

import com.imPine.imPineThankYou.model.Plant;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;
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

public class MakePlantActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    // Constants for permission request codes
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 101;
    private ImageView imageView;
    private String currentPhotoPath;
    private Uri imageUri;
    private RelativeLayout loadingPanel;

    private int avatar = 0;
    private ImageView lastSelectedAvatar = null;
    private int isPressed = 0;

    private void handleAvatarClick(ImageView clickedAvatar, int avatarValue) {
        if (lastSelectedAvatar != null) {
            // Reset the background of the previously selected avatar
            lastSelectedAvatar.setBackgroundResource(R.drawable.avatar_border);
        }

        // Highlight the clicked avatar
        clickedAvatar.setBackgroundResource(R.drawable.avatar_border_selected);
        lastSelectedAvatar = clickedAvatar;

        // Update the avatar variable
        this.avatar = avatarValue;
    }

    // Method to check and request permission
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, perform your operation
            dispatchTakePictureIntent();
        }
    }

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
            Uri savedImageUri = Uri.fromFile(image);

            // Log the path of the saved image
            Log.d("MakePlantActivityPic", "Saved image path: " + savedImageUri.getPath());

            return savedImageUri;
        } catch (IOException e) {
            Log.e("MakePlantActivityPic", "Error saving image", e);
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

    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_plant_page);

        // Initialize the loadingPanel
        loadingPanel = findViewById(R.id.loadingPanel);


        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        // Set default avatar
        ImageView firstAvatar = findViewById(R.id.avatar1);
        handleAvatarClick(firstAvatar, 0); // Highlight the first avatar


        // Set click listeners for the avatars
        findViewById(R.id.avatar1).setOnClickListener(v -> handleAvatarClick((ImageView) v, 0));
        findViewById(R.id.avatar2).setOnClickListener(v -> handleAvatarClick((ImageView) v, 1));
        findViewById(R.id.avatar3).setOnClickListener(v -> handleAvatarClick((ImageView) v, 2));
        findViewById(R.id.avatar4).setOnClickListener(v -> handleAvatarClick((ImageView) v, 3));
        findViewById(R.id.avatar5).setOnClickListener(v -> handleAvatarClick((ImageView) v, 4));
        findViewById(R.id.avatar6).setOnClickListener(v -> handleAvatarClick((ImageView) v, 5));
        findViewById(R.id.avatar7).setOnClickListener(v -> handleAvatarClick((ImageView) v, 6));
        findViewById(R.id.avatar8).setOnClickListener(v -> handleAvatarClick((ImageView) v, 7));
        findViewById(R.id.avatar9).setOnClickListener(v -> handleAvatarClick((ImageView) v, 8));


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
                        isPressed = 1;
                        try {
                            setPic();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(this, "Take a picture or choose from gallery to proceed!", Toast.LENGTH_SHORT).show();
                        isPressed = 0;
                    } else {
                        Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
                        isPressed = 0;
                    }
                });


        findViewById(R.id.btn_take_picture).setOnClickListener(v -> {
            showImageSourceDialog();
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            EditText nameEditText = findViewById(R.id.editPlantName);
            String plantName = nameEditText.getText().toString().trim();
            EditText heightEditText = findViewById(R.id.editHeight);
            String heightString = heightEditText.getText().toString().trim();

            // Check if any of the fields are empty or if the imageUri is null
            if (plantName.isEmpty() || heightString.isEmpty() || imageUri == null || isPressed == 0) {
                String message;
                if (isPressed == 0) {
                    message = "Try attaching a picture again!";
                }
                else {
                    message = "Please fill in all fields and attach a picture.";
                    if (plantName.isEmpty() || heightString.isEmpty()) {
                        message = "Please fill in all fields!";
                    } else if (imageUri == null) {
                        message = "Please attach a picture of your pineapple!";
                    }
                }
                Toast.makeText(MakePlantActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                // If fields are filled and an image is taken, continue with the save process
                try {
                    int plantHeight = Integer.parseInt(heightString);
                    Plant plant = new Plant(plantName, plantHeight);
                    plant.setAvatar(avatar);
                    Log.e("MakePlantActivityPic", "imageUri: " + imageUri.toString());
                    showProgressBar(); // Show the ProgressBar before starting the network request
                    getAuthToken(new AuthTokenCallback() {
                        @Override
                        public void onTokenReceived(String authToken) {
                            createPlant(plant, authToken, imageUri);
                        }

                        @Override
                        public void onTokenError(Exception e) {
                            Log.e("MakePlantActivity", "Authentication error", e);
                            hideProgressBar(); // Hide progress bar if there is an error
                        }
                    });
                } catch (NumberFormatException e) {
                    String message = "Please enter the height without any decimal points!";
                    Toast.makeText(MakePlantActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
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
    private void createPlant(Plant plant, String authToken, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] fileBytes = getBytesFromInputStream(inputStream);
            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) {
                mimeType = "image/jpeg";  // Default MIME type
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

            ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);
            Log.d("MakePlantActivity", "Attempting to create plant at URL: " + RetrofitClient.getBaseUrl() + "/api/plant/");
            Log.d("MakePlantActivity", "Token: " + authToken);

            RequestBody name = RequestBody.create(MultipartBody.FORM, plant.getName());
            RequestBody height = RequestBody.create(MultipartBody.FORM, String.valueOf(plant.getHeight()));
            RequestBody av= RequestBody.create(MultipartBody.FORM, String.valueOf(plant.getAvatar()));

            Call<ResponseBody> call = apiService.createPlant(authToken, name, height, av, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgressBar(); // Hide the ProgressBar on response
                    if (response.isSuccessful()) {
                        Toast.makeText(MakePlantActivity.this, "Plant saved!", Toast.LENGTH_SHORT).show();
                        navigateToHomePage();
                    } else {
                        handleResponseError(response);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgressBar(); // Hide the ProgressBar on response
                    Toast.makeText(MakePlantActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MakePlantActivity", "Network error", t);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MakePlantActivity", "Error: " + e.getMessage());
        }
    }



    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            launchCamera();
        }
    }
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("DiaryNewActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.imPine.imPineThankYou.fileprovider", photoFile);
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
        Log.d("MakePlantActivityPic", "createImageFile - Path: " + currentPhotoPath);
        return image;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, int orientation) throws IOException {
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

        // Get the orientation of the image
        ExifInterface ei = new ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        bitmap = rotateImageIfRequired(bitmap, orientation);

        imageView.setImageBitmap(bitmap);
    }


    // Handling the user response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission is needed to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
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

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    isPressed = 1;
                    Uri selectedImage = result.getData().getData();
                    Toast.makeText(MakePlantActivity.this, "Processing image, please wait...", Toast.LENGTH_SHORT).show();
                    try {
                        Bitmap bitmap = getCorrectlyOrientedBitmap(selectedImage);
                        imageView.setImageBitmap(bitmap);
                        imageUri = saveImage(bitmap);
                    } catch (IOException e) {
                        Log.e("MakePlantActivity", "Error selecting image from gallery", e);
                    }
                }
                else{
                    isPressed = 0;
                    Toast.makeText(MakePlantActivity.this, "Take a picture or choose from gallery to proceed!", Toast.LENGTH_SHORT).show();
                    imageUri = null;
                }
            }
    );

    private void dispatchChoosePictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            galleryLauncher.launch(intent);
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Picture", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                requestCameraPermission();
            } else {
                dispatchChoosePictureIntent(); // Directly launch gallery intent
            }
        });
        builder.show();
    }


    private Bitmap getCorrectlyOrientedBitmap(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // Getting EXIF data from the Uri
        ExifInterface ei = new ExifInterface(getRealPathFromURI(imageUri));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            default:
                return bitmap;
        }
    }
}
