package com.example.plantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupFullName, signupEmail, signupUsername, signupPassword, signupNumber,
            signupInterest, signupDay, signupMonth, signupYear;
    Spinner countrySpinner, statesRegionSpinner;
    RadioGroup genderGroup;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupFullName = findViewById(R.id.signup_fullname_form);
        signupEmail = findViewById(R.id.signup_email_form);
        signupUsername = findViewById(R.id.signup_username_form);
        signupPassword = findViewById(R.id.signup_password_form);
        signupNumber = findViewById(R.id.signup_number_form);
        signupInterest = findViewById(R.id.signup_interest_form);
        signupDay = findViewById(R.id.signup_day_form);
        signupMonth = findViewById(R.id.signup_month_form);
        signupYear = findViewById(R.id.signup_year_form);
        countrySpinner = findViewById(R.id.countrySpinner);
        statesRegionSpinner = findViewById(R.id.states_regionSpinner);
        genderGroup = findViewById(R.id.radiogroup);
        signupButton = findViewById(R.id.signup_SignUp_button);

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // Initialize the country spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, R.layout.custom_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Set listener on countrySpinner
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateRegionsSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optionally, clear regions Spinner or set a default placeholder
                ArrayAdapter<CharSequence> defaultAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
                        R.array.empty_region, R.layout.custom_spinner_item);
                defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                statesRegionSpinner.setAdapter(defaultAdapter);
            }
        });

        // Set listener on signupButton
        signupButton.setOnClickListener(v -> {
            // Retrieve user input
            String fullName = signupFullName.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String username = signupUsername.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String number = signupNumber.getText().toString().trim();
            String country = countrySpinner.getSelectedItem().toString();
            String region = statesRegionSpinner.getSelectedItem().toString();
            String day = signupDay.getText().toString().trim();
            String month = signupMonth.getText().toString().trim();
            String year = signupYear.getText().toString().trim();
            String interest = signupInterest.getText().toString().trim();

            String birthday = day + "/" + month + "/" + year;

            // Get selected gender
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";

            // Validate user input
            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() ||
                    number.isEmpty() || country.equals("Select Country") || region.equals("Select Region") ||
                    day.isEmpty() || month.isEmpty() || year.isEmpty() || gender.isEmpty() || interest.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create a new user object to store in the database
                HelperClass helperClass = new HelperClass(fullName, email, username, password,
                        country, region, number, birthday, gender, interest); // Updated to use birthday

                // Save user data to the database under the username as the key
                reference.child(username).setValue(helperClass)
                        .addOnSuccessListener(aVoid -> {
                            // Show success message on successful data entry
                            Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to login activity
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // Show error message if data entry fails
                            Toast.makeText(SignupActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Toggle password visibility
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signupPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // If the password is currently hidden, show it
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    togglePasswordVisibility.setImageResource(R.drawable.ic_eye_on); // Change to eye icon for visible
                } else {
                    // If the password is currently visible, hide it
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    togglePasswordVisibility.setImageResource(R.drawable.ic_eye_off); // Change to eye icon for hidden
                }
                // Move the cursor to the end of the password field
                signupPassword.setSelection(signupPassword.getText().length());
            }
        });
    }

    private void updateRegionsSpinner(int countryPosition) {
        int regionArrayId;

        if (countryPosition == 0) { // "Select Country" item
            // Set the statesRegionSpinner to be disabled and empty
            ArrayAdapter<CharSequence> emptyAdapter = ArrayAdapter.createFromResource(this,
                    R.array.empty_region, R.layout.custom_spinner_item);
            emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statesRegionSpinner.setAdapter(emptyAdapter);
            statesRegionSpinner.setEnabled(false);
        } else {
            // Enable the statesRegionSpinner and update it with the correct regions
            statesRegionSpinner.setEnabled(true);

            switch (countryPosition) {
                case 1: // Brunei
                    regionArrayId = R.array.brunei_regions;
                    break;
                case 2: // Cambodia
                    regionArrayId = R.array.cambodia_regions;
                    break;
                case 3: // East Timor
                    regionArrayId = R.array.east_timor_regions;
                    break;
                case 4: // Indonesia
                    regionArrayId = R.array.indonesia_regions;
                    break;
                case 5: // Laos
                    regionArrayId = R.array.laos_regions;
                    break;
                case 6: // Malaysia
                    regionArrayId = R.array.malaysia_regions;
                    break;
                case 7: // Philippines
                    regionArrayId = R.array.philippines_regions;
                    break;
                case 8: // Singapore
                    regionArrayId = R.array.singapore_regions;
                    break;
                case 9: // Thailand
                    regionArrayId = R.array.thailand_regions;
                    break;
                case 10: // Vietnam
                    regionArrayId = R.array.vietnam_regions;
                    break;
                default:
                    // Set to empty or placeholder
                    regionArrayId = R.array.empty_region;
                    break;
            }

            // Log the regionArrayId to verify it's correct
            Log.d("SignupActivity", "Selected country position: " + countryPosition + ", Region array ID: " + regionArrayId);

            // Create an ArrayAdapter for regions
            ArrayAdapter<CharSequence> regionAdapter = ArrayAdapter.createFromResource(this,
                    regionArrayId, R.layout.custom_spinner_item);
            regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statesRegionSpinner.setAdapter(regionAdapter);
        }
    }
}
