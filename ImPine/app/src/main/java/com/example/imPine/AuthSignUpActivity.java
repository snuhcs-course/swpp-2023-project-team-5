package com.example.imPine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AuthSignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextPasswordConfirm, editTextUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editTextUsername = findViewById(R.id.editTextUsername);


        findViewById(R.id.buttonSignUp).setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String user = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || user.isEmpty()) {
            // Handle empty fields
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(passwordConfirm)) {
            editTextPasswordConfirm.setError("Passwords do not match");
            editTextPasswordConfirm.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully registered the user
                        Toast.makeText(AuthSignUpActivity.this, "Sign Up Successful.", Toast.LENGTH_SHORT).show();

                        // Navigate back to the login page
                        Intent intent = new Intent(AuthSignUpActivity.this, AuthLoginActivity.class);
                        intent.putExtra("email", email); // Pass email to AuthLoginActivity
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        }
//                        // if email is poorly formatted
//                        catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
//                            editTextEmail.setError("Invalid Email");
//                            editTextEmail.requestFocus();
//                       }
//                        // if email is already in use
//                        catch (FirebaseAuthUserCollisionException existEmail) {
//                            editTextEmail.setError("Email already exists");
//                            editTextEmail.requestFocus();
//                        }
                        // if the password is too weak
//                        catch (FirebaseAuthWeakPasswordException weakPassword) {
//                            editTextPassword.setError("Password too short or weak");
//                            editTextPassword.requestFocus();
//                        }
                        // any other exceptions, handle or log them accordingly
                        catch (Exception e) {
                            Log.e("FirebaseAuth", "Sign Up Error", e);
                            Toast.makeText(AuthSignUpActivity.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}