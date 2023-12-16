package com.example.safeguardapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private ImageView backLoginSignup;
    private EditText editUsername, editEmail, editPassword, editConfirmPassword;
    private Button signupBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        backLoginSignup = findViewById(R.id.backLoginSignup);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        signupBtn = findViewById(R.id.signupBtn);
        progressBar = findViewById(R.id.progressBar);

        backLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginSignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String username, email, password, confirmPassword, role;
                username = editUsername.getText().toString();
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();
                confirmPassword = editConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    editUsername.setError("Username is required");
                    editUsername.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Email is required");
                    editEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError("Valid email is required");
                    editEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editPassword.setError("Password is required");
                    editPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (password.length() < 6) {
                    editPassword.setError("Password should be at least 6-digits");
                    editPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editConfirmPassword.setError("Password confirmation is required");
                    editConfirmPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                } else if (!password.equals(confirmPassword)) {
                    editConfirmPassword.setError("Password must be the same as above");
                    editConfirmPassword.requestFocus();
                    // Clear entered passwords
                    editPassword.clearComposingText();
                    editConfirmPassword.clearComposingText();
                    progressBar.setVisibility(View.GONE);
                } else {
                    signupUser(username, email, password);
                }
            }
        });
    }

    private void signupUser(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("username", username);
                    userMap.put("role", "user");

                    DatabaseReference dbProfileReference = FirebaseDatabase.getInstance().getReference("Registered Users");

                    dbProfileReference.child(firebaseUser.getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();

                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(SignUpActivity.this, "Failed to sign up. Please try again", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editPassword.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters");
                        editPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editEmail.setError("Your email is invalid or already in used. Kindly re-enter.");
                        editEmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editEmail.setError("User is already registered with this email. Use another email.");
                        editEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}