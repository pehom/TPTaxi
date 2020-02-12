package com.android.pehominc.tptaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverSignInActivity extends AppCompatActivity {
    private final static String TAG = "myLog";
    private TextInputLayout emailTextInput;
    private TextInputLayout nameTextInput;
    private TextInputLayout passwordTextInput;
    private TextInputLayout confirmPasswordTextInput;

    private Button loginSignUpButton;
    private TextView toggleLoginTextView;
    private Boolean isLoginModeActive = false;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference driversDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_in);
        emailTextInput = findViewById(R.id.emailTextInput);
        nameTextInput = findViewById(R.id.nameTextInput);
        passwordTextInput = findViewById(R.id.passwordTextInput);
        confirmPasswordTextInput = findViewById(R.id.confirmPasswordTextInput);
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        toggleLoginTextView = findViewById(R.id.toggleLogInTextView);

        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null)  {
            startActivity(new Intent(DriverSignInActivity.this, DriverMapsActivity.class));
        }

        database = FirebaseDatabase.getInstance();
        driversDatabaseReference = database.getReference().child("drivers");

        loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSignUpUser(emailTextInput.getEditText().getText().toString().trim(), passwordTextInput.getEditText().getText().toString().trim());

            }
        });


    }

    private void loginSignUpUser(String email, String password) {
        if (isLoginModeActive) {
            if (!validateEmail()  | !validatePassword()) return;
            else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("currentUserName", "signInWithEmail:success");



                                    Intent intent = new Intent(DriverSignInActivity.this, DriverMapsActivity.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(DriverSignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }


        } else {
            if (!validateEmail() | !validateName() | !validatePassword() | !validateConfirmPassword()) return;

            else {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                 //   FirebaseUser user = auth.getCurrentUser();
                                 //   createUser(user);
                                    Toast.makeText(DriverSignInActivity.this, "Successful  reg", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(DriverSignInActivity.this, DriverMapsActivity.class);

                                 //   intent.putExtra("userName", nameTextInput.getEditText().getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(DriverSignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                    //   updateUI(null);
                                }
                            }
                        });
            }

        }


    }

    private void createUser(FirebaseUser user) {
        Driver driver = new Driver();
        driver.setEmail(user.getEmail());
        driver.setId(user.getUid());
        driver.setName(nameTextInput.getEditText().getText().toString().trim());
       driversDatabaseReference.child(user.getUid()).setValue(driver);

    }



    public void toggleLoginSignUp(View view) {
        if (isLoginModeActive) {
            isLoginModeActive = false;
            loginSignUpButton.setText("Sign Up");
            toggleLoginTextView.setText("tap to log in");
            nameTextInput.setVisibility(View.VISIBLE);
            confirmPasswordTextInput.setVisibility(View.VISIBLE);
        } else {
            isLoginModeActive = true;
            loginSignUpButton.setText("Log in");
            toggleLoginTextView.setText("tap to Sign up");

           // passwordTextInput.setVisibility(View.VISIBLE);
            nameTextInput.setVisibility(View.GONE);
            confirmPasswordTextInput.setVisibility(View.GONE);
        }

    }

    private  boolean validateEmail() {
        String emailInput = emailTextInput.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            emailTextInput.setError("Please input your email");
            return false;
        } else {
            emailTextInput.setError("");
            return true;
        }
    }

    private  boolean validateName() {
        String nameInput = nameTextInput.getEditText().getText().toString().trim();
        if (nameInput.isEmpty()) {
            nameTextInput.setError("Please input your name");
            return false;
        } else if (nameInput.length() > 20) {
            nameTextInput.setError("Name length should be less than 20 ");
            return false;}
        else{
            nameTextInput.setError("");
            return true;
        }
    }

    private  boolean validatePassword() {
        String passwordInput = passwordTextInput.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            passwordTextInput.setError("Please input your password");
            return false;
        } else if (passwordInput.length() < 7) {
            passwordTextInput.setError("password length should be more than 6 ");
            return false;}

        else{
            passwordTextInput.setError("");
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        String passwordInput = passwordTextInput.getEditText().getText().toString().trim();
        String confirmPasswordInput = confirmPasswordTextInput.getEditText().getText().toString().trim();
        if (confirmPasswordInput.isEmpty()){
            confirmPasswordTextInput.setError("Please confirm your password");
            return false;
        }
        else if (!passwordInput.equals(confirmPasswordInput)) {
            passwordTextInput.setError("passwords have to match");
            return false;
        } else {
            passwordTextInput.setError("");
            return true;
        }
    }
}
