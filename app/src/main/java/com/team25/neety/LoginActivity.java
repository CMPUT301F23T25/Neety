package com.team25.neety;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.SignInButton;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    SignInButton btSignIn;
    Button continueBtn, registerBtn;
    TextView usernameAvailabilityTextView;

    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersReference;
    EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        btSignIn = findViewById(R.id.bt_sign_in);
        continueBtn = findViewById(R.id.continue_btn);
        registerBtn = findViewById(R.id.register_btn);
        usernameAvailabilityTextView = findViewById(R.id.username_availability_text);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("708622187336-h5500e4k64ie4knkj3voeprh452m3mpa.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            checkUsernameAvailabilityAndProceed(firebaseUser.getUid());
        }

        btSignIn.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        });

        continueBtn.setOnClickListener(view -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                checkUsernameAvailabilityAndProceed(currentUser.getUid());
            }
        });

        registerBtn.setOnClickListener(view -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String enteredUsername = getUsernameFromEditText();
                saveUsernameToDatabase(currentUser.getUid(), enteredUsername);
                checkUsernameAvailabilityAndProceed(currentUser.getUid());
            }
        });
    }

    private void checkUsernameAvailabilityAndProceed(String uid) {
        String enteredUsername = getUsernameFromEditText();

        usersReference.child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedUsername = dataSnapshot.getValue(String.class);
                    if (enteredUsername.equals(storedUsername)) {
                        // Username exists, proceed to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish(); // Finish the LoginActivity
                    } else {
                        // Username does not match, show text view and make register button visible
                        usernameAvailabilityTextView.setVisibility(View.VISIBLE);
                        registerBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Username does not exist, show text view and make register button visible
                    usernameAvailabilityTextView.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void saveUsernameToDatabase(String uid, String username) {
        usersReference.child(uid).child("username").setValue(username).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Username saved successfully, proceed to main activity
                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish(); // Finish the LoginActivity
            } else {
                // Handle error
                displayToast("Error saving username: " + task.getException().getMessage());
            }
        });
    }

    private String getUsernameFromEditText() {
        usernameEditText = findViewById(R.id.username);
        return usernameEditText.getText().toString();
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
