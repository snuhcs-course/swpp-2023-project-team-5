package com.example.imPine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.imPine.model.Diary;
import com.example.imPine.network.ApiInterface;
import com.example.imPine.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryNewActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private ApiInterface apiService;
    private boolean isPrivate = false;
    private String category;
    private Button privateButton, publicButton;
    private Spinner categorySpinner;
    private ImageView imageView;

    private Uri imageUri;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private RelativeLayout loadingPanel;

    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_new);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        privateButton = findViewById(R.id.privateButton);
        publicButton = findViewById(R.id.publicButton);
        ImageView lockImage = findViewById(R.id.lock);
        categorySpinner = findViewById(R.id.categorySpinner);
        imageView = findViewById(R.id.imageView);
        // Initialize the loadingPanel
        loadingPanel = findViewById(R.id.loadingPanel);
        // Set the default color for the public button
        publicButton.setBackgroundColor(getResources().getColor(R.color.darkblue));

        setupCategorySpinner();
        setupCameraLauncher();

        // Set up button listeners
        setupButtonListeners(lockImage);

        ScrollView mainLayout = findViewById(R.id.mainLayout);
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

        apiService = RetrofitClient.getClient().create(ApiInterface.class);

        saveButton.setOnClickListener(v -> postNewDiary());
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            setPic();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        Button takePictureButton = findViewById(R.id.btn_take_picture);
        takePictureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(DiaryNewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DiaryNewActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                dispatchTakePictureIntent();
            }
        });
    }

    private void setupButtonListeners(ImageView lockImage) {
        privateButton.setOnClickListener(v -> {
            isPrivate = true;
            lockImage.setImageResource(R.drawable.lock);
            privateButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            publicButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });

        publicButton.setOnClickListener(v -> {
            isPrivate = false;
            lockImage.setImageResource(R.drawable.unlock);
            publicButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            privateButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });
    }

    private void setupCategorySpinner() {
        String[] categories = {"Happy", "Sad", "Angry", "Loving", "Grateful"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.spinner_item_text, categories);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "Happy";
            }
        });
    }

    private void postNewDiary() {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody titlePart = RequestBody.create(MediaType.parse("multipart/form-data"), title);
            RequestBody contentPart = RequestBody.create(MediaType.parse("multipart/form-data"), content);
            RequestBody isPrivatePart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(isPrivate));
            RequestBody categoryPart = RequestBody.create(MediaType.parse("multipart/form-data"), category);

            // Check if an image was captured
            MultipartBody.Part body = null;
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        Diary diary = new Diary.Builder()
                .setTitle(title)
                .setContent(content)
                .setIsPrivate(isPrivate)
                .setCategory(category)
                .build();


        String authToken = AuthLoginActivity.getAuthToken(this);
            Call<ResponseBody> call;
            if (body != null) {
                // Show loading panel
                showProgressBar();
                call = apiService.createDiary(authToken, titlePart, contentPart, isPrivatePart, categoryPart, body);
            } else {
                // Show loading panel
                showProgressBar();
                call = apiService.createDiaryWithoutImage(authToken, diary);
            }
            call.enqueue(new DiaryNewActivity.DiaryCallback());
        }


        private class DiaryCallback implements Callback<ResponseBody> {
            @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                // Hide loading panel
                hideProgressBar();
                Toast.makeText(DiaryNewActivity.this, "Diary saved successfully.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e("DiaryNewActivity", "Failed to save diary: " + response.code() + " " + response.message());
                try {
                    // Log detailed error message if available
                    Log.e("DiaryNewActivity", "Error body: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(DiaryNewActivity.this, "Failed to post diary: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            // Hide loading panel
            hideProgressBar();
            Log.e("DiaryNewActivity", "Error: " + t.getMessage(), t);
            Toast.makeText(DiaryNewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("DiaryNewActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.imPine.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Camera permission is needed to take pictures", Toast.LENGTH_SHORT).show();
        }
    }
}
