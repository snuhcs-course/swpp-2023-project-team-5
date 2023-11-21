package com.example.imPine;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;

import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private EditText nameEditText, heightEditText, lastWateredEditText;
    private Spinner statusSpinner;

    private void showProgressBar() {
        loadingPanel.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        loadingPanel.setVisibility(View.GONE);
    }

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

        findViewById(R.id.btn_take_picture).setOnClickListener(v -> takePicture());
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
        boolean hasImageChanged = imageUri != null && !existingPlant.getImage().equals(imageUri.toString());

        if (hasNameChanged || hasHeightChanged || hasStatusChanged || hasLastWateredChanged || hasAvatarChanged || hasImageChanged) {
            Log.d("EditPlantActivity", "Changes detected. Updating plant information.");
            editPlant(Integer.toString(existingPlant.getPlant_id()), newName, newHeight, newStatus, newLastWatered, Integer.toString(currentAvatar), userId);
        } else {
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
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
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), fileBytes);
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
}