package com.imPine.imPineThankYou;

import android.Manifest;
import android.app.AlertDialog;
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
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.imPine.imPineThankYou.model.Diary;
import com.imPine.imPineThankYou.model.DiaryGetResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;

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

public class DiaryDetailActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> cameraLauncher;
    private EditText titleEditText, contentEditText;
    private TextView date;
    private Button editButton, deleteButton;
    private ImageView imageView;
    private Spinner categorySpinner;
    private boolean imageChanged = false;
    private String currentPhotoPath;
    private RelativeLayout loadingPanel;
    private Uri imageUri;
    private boolean newPrivate;
    private boolean priv;
    private String category;
    private String newCategory;
    private String title;
    private String newTitle;
    private String content;
    private String newContent;
    private String src;
    private String newSrc;
    private ApiInterface apiService;
    private int diaryId;
    private Button publicButton, privateButton;
    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }
    private int isPressed = 0;

    private ActivityResultCallback<ActivityResult> cameraResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                isPressed = 1;
                // Image captured successfully
                Glide.with(DiaryDetailActivity.this).load(imageUri).into(imageView);
                imageChanged = true; // Set the flag to true as the image has changed
            } else {
                isPressed = 0;
                Toast.makeText(DiaryDetailActivity.this, "Cancelled...Attach a new photo if you want a picture!", Toast.LENGTH_SHORT).show();
                Log.e("DiaryDetailActivity", "Image capture failed or cancelled");
            }
        }
    };


    private void initializeCategorySpinner(String selectedCategory) {
        String[] categories = {"Happy", "Sad", "Angry", "Loving", "Grateful"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.spinner_item_text, categories);
        categorySpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(selectedCategory);
        categorySpinner.setSelection(spinnerPosition);
        this.newCategory = selectedCategory; // Initialize newCategory with the current category

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newCategory = category;
            }
        });
    }

    private void initializePrivacyButton(boolean isPrivate) {
        ImageView lockImage = findViewById(R.id.lock);
        this.newPrivate = isPrivate; // Initialize newPrivate with the current privacy status

        if (isPrivate) {
            privateButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            publicButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            lockImage.setImageResource(R.drawable.lock);
        } else {
            publicButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            privateButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            lockImage.setImageResource(R.drawable.unlock);
        }

        privateButton.setOnClickListener(v -> {
            newPrivate = true;
            lockImage.setImageResource(R.drawable.lock);
            privateButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            publicButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });

        publicButton.setOnClickListener(v -> {
            newPrivate = false;
            lockImage.setImageResource(R.drawable.unlock);
            publicButton.setBackgroundColor(getResources().getColor(R.color.darkblue));
            privateButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_detail);
        ScrollView mainLayout = findViewById(R.id.mainLayout);
        date = findViewById(R.id.dateLabel);

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

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), cameraResultCallback);
        findViewById(R.id.btn_take_picture).setOnClickListener(v -> showImageSourceDialog());
        // Initialize the loadingPanel
        loadingPanel = findViewById(R.id.loadingPanel);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        imageView = findViewById(R.id.imageView);
        categorySpinner = findViewById(R.id.categorySpinner);
        publicButton = findViewById(R.id.publicButton);
        privateButton = findViewById(R.id.privateButton);

        // API Service Initialization
        apiService = RetrofitClient.getClient().create(ApiInterface.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DIARY_ID")) {
            diaryId = intent.getIntExtra("DIARY_ID", 0);
            Log.d("DiaryID", "GOT DiaryID: " + diaryId);
            fetchDiaryDetails(diaryId);
        }
        setupEditButton();
        setupDeleteButton();
    }

    private void fetchDiaryDetails(int diaryId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        apiService.getDiary("Bearer " + authToken, String.valueOf(diaryId)).enqueue(new Callback<DiaryGetResponse>() {
            @Override
            public void onResponse(Call<DiaryGetResponse> call, Response<DiaryGetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("DiaryDetailActivity", "Success");
                    Diary diary = response.body().getDiary();

                    // Logging the diary details
                    Log.d("DiaryDetailActivity", "Title: " + diary.getTitle());
                    Log.d("DiaryDetailActivity", "Content: " + diary.getContent());
                    Log.d("DiaryDetailActivity", "Private: " + diary.getIsPrivate());
                    Log.d("DiaryDetailActivity", "Category: " + diary.getCategory());
                    Log.d("DiaryDetailActivity", "SRC: " + diary.getImage_src());
                    src = diary.getImage_src();
                    if (src!= null) {
                        Log.d("DiaryDetailActivity", "Image URL: " + src);
                        loadImage(imageView, src);
                    }

                    // Set the UI components with the diary details
                    title = diary.getTitle();
                    content = diary.getContent();
                    date.setText(diary.getFormattedCreatedAt());

                    titleEditText.setText(title);
                    contentEditText.setText(content);

                    category = diary.getCategory();
                    priv = diary.getIsPrivate();

                 initializeCategorySpinner(category);
                 initializePrivacyButton(priv);

                } else {
                    Log.e("DiaryDetailActivity", "Failed to load diary. Response code: " + response.code());
                    try {
                        Log.e("DiaryDetailActivity", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("DiaryDetailActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(DiaryDetailActivity.this, "Failed to load diary details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DiaryGetResponse> call, Throwable t) {
                Log.e("DiaryDetailActivity", "Error fetching diary: " + t.getMessage(), t);
                Toast.makeText(DiaryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadImage(ImageView imageView, String imageUrl) {
        // Use Glide
        Glide.with(this).load(imageUrl).into(imageView);
    }

    private void setupEditButton() {
        editButton.setOnClickListener(v -> {
            if (hasChanges()) {
                if (isPressed == 1) {
                    updateDiaryWithImage(imageUri);
                }
                else updateDiaryWithoutImage();
            } else {
                Toast.makeText(this, "No changes to be made", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean hasChanges() {
        newTitle = titleEditText.getText().toString();
        newContent = contentEditText.getText().toString();
        // Compare strings properly using .equals() instead of !=
        boolean titleChanged = !newTitle.equals(title);
        boolean contentChanged = !newContent.equals(content);
        boolean categoryChanged = !newCategory.equals(category);
        boolean privacyChanged = (newPrivate != priv);

        return titleChanged || contentChanged || categoryChanged || privacyChanged || imageChanged;
    }

    private void updateDiaryWithoutImage() {
        String authToken = AuthLoginActivity.getAuthToken(this);
        Diary updatedDiary = new Diary.Builder()
                .setTitle(newTitle)
                .setContent(newContent)
                .setIsPrivate(newPrivate)
                .setCategory(newCategory)
                .build();

        apiService.updateDiaryWithoutImage(
                "Bearer " + authToken,
                updatedDiary.getTitle(),
                updatedDiary.getContent(),
                updatedDiary.getIsPrivate(),
                updatedDiary.getCategory(),
                diaryId
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DiaryDetailActivity.this, "Diary updated successfully", Toast.LENGTH_SHORT).show();
                    // Handle successful update here
                    finish();
                } else {
                    Toast.makeText(DiaryDetailActivity.this, "Failed to update diary", Toast.LENGTH_SHORT).show();
                    finish();
                    // Log error details
                    Log.e("UpdateError", "Response Code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error body is null";
                        Log.e("UpdateError", "Response Error Body: " + errorBody +"Diaryid: " + diaryId);
                    } catch (IOException e) {
                        Log.e("UpdateError", "Error while reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DiaryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Log network or other errors
                finish();
                Log.e("UpdateFailure", "Error: ", t);
            }
        });
    }


    private void updateDiaryWithImage(Uri imageUri) {
        String authToken = AuthLoginActivity.getAuthToken(this);

        // Create RequestBody instances for each part
        RequestBody diaryIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(diaryId));
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), newTitle);
        RequestBody contentPart = RequestBody.create(MediaType.parse("text/plain"), newContent);
        RequestBody isPrivatePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(newPrivate));
        RequestBody categoryPart = RequestBody.create(MediaType.parse("text/plain"), newCategory);

        MultipartBody.Part body = null;
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] fileBytes = getBytesFromInputStream(inputStream);
                String mimeType = getContentResolver().getType(imageUri);
                if (mimeType == null) {
                    mimeType = "image/jpeg";  // Default MIME type
                }
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
            } catch (IOException e) {
                Log.e("EditPlantActivity", "Error reading image file", e);
            }
        }
        showProgressBar();

        // Execute the Retrofit call
        apiService.updateDiaryWithImage("Bearer " + authToken, diaryIdPart, titlePart, contentPart, isPrivatePart, categoryPart, body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DiaryDetailActivity.this, "Diary updated successfully with image", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(DiaryDetailActivity.this, "Failed to update diary with image", Toast.LENGTH_SHORT).show();
                            // Log error details
                            Log.e("UpdateError", "Response Code: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error body is null";
                                Log.e("UpdateError", "Response Error Body: " + errorBody);
                            } catch (IOException e) {
                                Log.e("UpdateError", "Error while reading error body", e);
                            }
                        }
                        hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(DiaryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UpdateFailure", "Error: ", t);
                        hideProgressBar();
                    }
                });
    }


    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Diary")
                    .setMessage("Are you sure you want to delete this diary?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteDiary(diaryId))
                    .setNegativeButton(android.R.string.no, null).show();
        });
    }

    private void deleteDiary(int diaryId) {
        String authToken = AuthLoginActivity.getAuthToken(this);
        RequestBody diaryIdBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "diary_id=" + diaryId);
        apiService.deleteDiaryWithBody("Bearer " + authToken, diaryIdBody).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DiaryDetailActivity.this, "Diary deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Handle error more specifically
                    Log.d("DiaryDelete", "Unsuccessful deletion attempt. Response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("DiaryDelete", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("DiaryDelete", "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure more specifically
                Log.e("DiaryDelete", "Error deleting diary: " + t.getMessage(), t);
            }
        });
    }


    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                        "com.imPine.imPineThankYou.fileprovider", photoFile);
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

    private Bitmap getCorrectlyOrientedBitmap(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        ExifInterface ei = new ExifInterface(getContentResolver().openInputStream(imageUri));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        return rotateImageIfRequired(bitmap, orientation);
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

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    isPressed = 1;
                    Uri selectedImageUri = result.getData().getData();
                    Toast.makeText(DiaryDetailActivity.this, "Processing image, please wait...", Toast.LENGTH_SHORT).show();
                    try {
                        Bitmap bitmap = getCorrectlyOrientedBitmap(selectedImageUri);
                        imageView.setImageBitmap(bitmap);
                        imageUri = saveImage(bitmap); // Save the image and update the URI
                        imageChanged = true; // Set the flag to true as the image has changed
                    } catch (IOException e) {
                        Log.e("DiaryNewActivity", "Error selecting image from gallery", e);
                    }
                }
                else {
                    isPressed = 0;
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
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else {
                dispatchChoosePictureIntent();
            }
        });
        builder.show();
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
}
