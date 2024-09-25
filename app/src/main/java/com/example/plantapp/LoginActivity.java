package com.example.plantapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    EditText loginUsername, loginPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username_form);
        loginPassword = findViewById(R.id.login_password_form);
        loginButton = findViewById(R.id.login_LogIn_button);

        loginUsername.setPadding(
                loginUsername.getPaddingLeft(),
                loginUsername.getPaddingTop(),
                loginUsername.getPaddingRight() + 50,
                loginUsername.getPaddingBottom()
        );
        loginPassword.setPadding(
                loginPassword.getPaddingLeft(),
                loginPassword.getPaddingTop(),
                loginPassword.getPaddingRight() + 100,
                loginPassword.getPaddingBottom()
        );

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs and check user credentials if valid
                if (validateUsername() && validatePassword()) {
                    checkUser();
                }
            }
        });

        // Adjust padding based on system window insets (like the status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up sign up button click listener
        TextView signUpButton = findViewById(R.id.signuptextbutton);
        signUpButton.setOnClickListener(v -> {
            // Start SignupActivity when sign-up text is clicked
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Set up toggle password visibility button
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        togglePasswordVisibility.setOnClickListener(v -> {
            // Toggle password visibility
            if (isPasswordVisible) {
                loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off);
            } else {
                loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_on);
            }
            loginPassword.setSelection(loginPassword.getText().length());
            isPasswordVisible = !isPasswordVisible; // Update visibility state
        });
    }


    private Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            // Show error if username is empty
            loginUsername.setError("Username cannot be empty.");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            // Show error if password is empty
            loginPassword.setError("Password cannot be empty.\n" +
                    "           ");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Method pang check ng user sa Database
    private void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        // Reference to the "users" node in the Firebase database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the user exists
                if (snapshot.exists()) {
                    String passwordFromDB = "";
                    // Retrieve the password for the found user
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        passwordFromDB = userSnapshot.child("password").getValue(String.class);
                    }

                    // Compare entered password with the password from the database
                    if (Objects.equals(passwordFromDB, userPassword)) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Start WelcomeActivity on successful login
                        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    } else {
                        // Show error if passwords do not match
                        loginPassword.setError("Invalid Credentials!");
                        loginPassword.requestFocus();
                    }
                } else {
                    // Show error if user does not exist
                    loginUsername.setError("User does not exist!");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Show error message if the database query fails
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
