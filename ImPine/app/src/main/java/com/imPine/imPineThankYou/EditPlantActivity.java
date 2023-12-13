package com.imPine.imPineThankYou;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;

import androidx.core.content.res.ResourcesCompat;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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

import android.Manifest;

import com.bumptech.glide.Glide;
import com.imPine.imPineThankYou.model.Plant;
import com.imPine.imPineThankYou.model.PlantResponse;
import com.imPine.imPineThankYou.model.UserResponse;
import com.imPine.imPineThankYou.network.ApiInterface;
import com.imPine.imPineThankYou.network.RetrofitClient;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
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
    private boolean imageChanged = false;

    private EditText nameEditText, heightEditText, lastWateredEditText;
    private Spinner statusSpinner;

    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }

    private int isPressed = 1;
    private Bitmap rotateImageIfRequired2(Bitmap img, int orientation) throws IOException {
        switch (orientation) {
            case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage2(img, 90);
            case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage2(img, 180);
            case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage2(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage2(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void setPic2() throws IOException {
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
        androidx.exifinterface.media.ExifInterface ei = new androidx.exifinterface.media.ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL);

        bitmap = rotateImageIfRequired2(bitmap, orientation);

        imageView.setImageBitmap(bitmap);
    }


    private ActivityResultCallback<ActivityResult> cameraResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                try {
                    setPic2();
                    imageChanged = true; // Set flag to true as image has changed
                } catch (IOException e) {
                    Log.e("EditPlantActivity", "Error occurred while loading image from file", e);
                    Toast.makeText(EditPlantActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }

                isPressed = 1;
                Log.d("EditPlantActivity", "Image capture successful");
            } else {
                isPressed = 0;
                Log.d("EditPlantActivity", "Camera action cancelled or failed");
                Toast.makeText(EditPlantActivity.this, "Cancelled taking picture", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private ImageView lastSelectedAvatar = null;
    private int currentAvatar = 0; // Default value, update it based on the intent extra

    private void highlightAvatar(ImageView avatar, int avatarValue) {
        if (lastSelectedAvatar != null) {
            lastSelectedAvatar.setBackgroundResource(R.drawable.avatar_border); // Reset previous avatar
        }
        avatar.setBackgroundResource(R.drawable.avatar_border_selected); // Highlight new avatar
        lastSelectedAvatar = avatar;
        currentAvatar = avatarValue; // Update the current avatar value
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_plant_page);


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
        // Initialize the loadingPanel
        loadingPanel = findViewById(R.id.loadingPanel);

        EditText editLastWatered = findViewById(R.id.editLastWatered);

        ImageView avatar1 = findViewById(R.id.avatar1);
        ImageView avatar2 = findViewById(R.id.avatar2);
        ImageView avatar3 = findViewById(R.id.avatar3);
        ImageView avatar4 = findViewById(R.id.avatar4);
        ImageView avatar5 = findViewById(R.id.avatar5);
        ImageView avatar6 = findViewById(R.id.avatar6);
        ImageView avatar7 = findViewById(R.id.avatar7);
        ImageView avatar8 = findViewById(R.id.avatar8);
        ImageView avatar9 = findViewById(R.id.avatar9);

        avatar1.setOnClickListener(v -> highlightAvatar(avatar1, 0));
        avatar2.setOnClickListener(v -> highlightAvatar(avatar2, 1));
        avatar3.setOnClickListener(v -> highlightAvatar(avatar3, 2));
        avatar4.setOnClickListener(v -> highlightAvatar(avatar4, 3));
        avatar5.setOnClickListener(v -> highlightAvatar(avatar5, 4));
        avatar6.setOnClickListener(v -> highlightAvatar(avatar6, 5));
        avatar7.setOnClickListener(v -> highlightAvatar(avatar7, 6));
        avatar8.setOnClickListener(v -> highlightAvatar(avatar8, 7));
        avatar9.setOnClickListener(v -> highlightAvatar(avatar9, 8));

        // Highlight the current avatar based on the value you get from intent
        currentAvatar = getIntent().getIntExtra("avatar", 0); // Get the current avatar value from the intent
        switch (currentAvatar) {
            case 0: highlightAvatar(avatar1, 0); break;
            case 1: highlightAvatar(avatar2, 1); break;
            case 2: highlightAvatar(avatar3, 2); break;
            case 3: highlightAvatar(avatar4, 3); break;
            case 4: highlightAvatar(avatar5, 4); break;
            case 5: highlightAvatar(avatar6, 5); break;
            case 6: highlightAvatar(avatar7, 6); break;
            case 7: highlightAvatar(avatar8, 7); break;
            case 8: highlightAvatar(avatar9, 8); break;
        }

        editLastWatered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Calendar to get current year, month, and day
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditPlantActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Format the date and set it to the EditText
                                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                                editLastWatered.setText(selectedDate);
                            }
                        }, year, month, day);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        statusSpinner = findViewById(R.id.spinnerStatus);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.status_options)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.short_stack));
                return textView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);


        initUI();
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), cameraResultCallback);

        findViewById(R.id.btn_take_picture).setOnClickListener(v -> showImageSourceDialog());
        findViewById(R.id.btn_save).setOnClickListener(v -> checkForChangesAndSave());
    }

    private void initUI() {
        imageView = findViewById(R.id.imageView);
        loadingPanel = findViewById(R.id.loadingPanel);

        // Initialize the EditTexts and Spinners
        nameEditText = findViewById(R.id.editPlantName);
        heightEditText = findViewById(R.id.editHeight);
        lastWateredEditText = findViewById(R.id.editLastWatered);
        statusSpinner = findViewById(R.id.spinnerStatus);
//        avatarSpinner = findViewById(R.id.spinnerAvatar);

        // Extracting the intent values
        Intent intent = getIntent();
        if (intent != null) {
            // Get the values passed from HomePageActivity
            String pineappleName = intent.getStringExtra("pineappleName");
            int height = intent.getIntExtra("height", 0);
            String imageURL = intent.getStringExtra("imageURL");
            String lastWatered = intent.getStringExtra("lastWatered");
            String status = intent.getStringExtra("status");
            String avatar = intent.getStringExtra("avatar");

            // Set the extracted values to the views
            nameEditText.setText(pineappleName);
            heightEditText.setText(String.valueOf(height));
            Glide.with(this).load(imageURL).into(imageView);

            // Assuming you have methods to set spinner values
            setSpinnerToValue(statusSpinner, status);
            lastWateredEditText.setText(lastWatered);
        }
    }

    // Utility method to set spinner value based on the string
    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equals(value)) {
                spinner.setSelection(position);
                break;
            }
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
                    compareAndEditPlant(response.body().getPlants().get(0), userId);
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

    private void compareAndEditPlant(Plant existingPlant, String userId) {
        String newName = nameEditText.getText().toString().trim();
        String newHeight = heightEditText.getText().toString().trim();
        String newStatus = ((TextView) statusSpinner.getSelectedView()).getText().toString();
        String newLastWatered = lastWateredEditText.getText().toString();

        boolean hasNameChanged = !existingPlant.getName().equals(newName);
        boolean hasHeightChanged = !String.valueOf(existingPlant.getHeight()).equals(newHeight);
        boolean hasStatusChanged = !existingPlant.getStatus().equals(newStatus);
        boolean hasLastWateredChanged = !existingPlant.getLast_watered().equals(newLastWatered);
        boolean hasAvatarChanged = existingPlant.getAvatar() != currentAvatar;
        boolean hasImageChanged = imageChanged; // Use the flag instead of comparing URIs


        if (hasNameChanged || hasHeightChanged || hasStatusChanged || hasLastWateredChanged || hasAvatarChanged || isPressed == 1) {
            Log.d("EditPlantActivity", "Changes detected. Updating plant information.");
            editPlant(Integer.toString(existingPlant.getPlant_id()), newName, newHeight, newStatus, newLastWatered, Integer.toString(currentAvatar), userId);
            HomePageActivity.avatarFromHome = currentAvatar;
        } else {
            if (isPressed == 0) {
                Toast.makeText(EditPlantActivity.this, "Attach photo again!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
            HomePageActivity.avatarFromHome = currentAvatar;
            navigateToHomePage();
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
    private void editPlant(String plantId, String newName, String newHeight, String newStatus, String newLastWatered, String newAvatar, String userId) {
        if (newName.trim().isEmpty() || newHeight.trim().isEmpty()) {
            Toast.makeText(EditPlantActivity.this, "Name and Height cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        String authToken = AuthLoginActivity.getAuthToken(this);
        ApiInterface apiService = RetrofitClient.getClient().create(ApiInterface.class);

        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), newName);
        RequestBody heightBody = RequestBody.create(MediaType.parse("multipart/form-data"), newHeight);
        RequestBody statusBody = RequestBody.create(MediaType.parse("multipart/form-data"), newStatus);
        RequestBody lastWateredBody = RequestBody.create(MediaType.parse("multipart/form-data"), newLastWatered);
        RequestBody avatarBody = RequestBody.create(MediaType.parse("multipart/form-data"), newAvatar);
        RequestBody userIdBody = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody plantIdBody = RequestBody.create(MediaType.parse("multipart/form-data"), plantId);

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

        // Include all the RequestBody objects in the API call
        Call<ResponseBody> call = apiService.editPlant(authToken, nameBody, heightBody, statusBody, lastWateredBody, avatarBody, userIdBody, plantIdBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditPlantActivity.this, "Plant updated successfully", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                    navigateToHomePage();
                } else {
                    handleResponseError(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressBar();
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
                takePicture();
            } else {
                dispatchChoosePictureIntent();
            }
        });
        builder.show();
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    isPressed = 1;
                    Uri selectedImageUri = result.getData().getData();
                    Toast.makeText(EditPlantActivity.this, "Processing image, please wait...", Toast.LENGTH_SHORT).show();
                    try {
                        Bitmap bitmap = getCorrectlyOrientedBitmap(selectedImageUri);
                        imageView.setImageBitmap(bitmap);
                        rotateImage(bitmap,90);
                        imageUri = saveImage(bitmap); // Save the image and update the URI
                        imageChanged = true; // Set flag to true as image has changed
                    } catch (IOException e) {
                        Log.e("EditPlantActivity", "Error selecting image from gallery", e);
                        imageChanged = false;
                    }
                } else {
                    isPressed = 0;
                    Toast.makeText(EditPlantActivity.this, "Image selection cancelled", Toast.LENGTH_SHORT).show();
                    imageChanged = false;
                }
            }
    );


    private Bitmap getCorrectlyOrientedBitmap(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

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

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
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



}