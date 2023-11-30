package com.team25.neety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText username_box;
    private Button continue_button;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        username_box = findViewById(R.id.username_edittext);
        continue_button = findViewById(R.id.continue_button);
        TextView info_text = findViewById(R.id.notification_text);

        continue_button.setOnClickListener(v -> {
            String username = username_box.getText().toString();

            DocumentReference userDocRef = usersRef.document(username);
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document with username exists
                        Log.d("Firestore", "Document exists!");
                        login(username);
                    } else {
                        // Document with username does not exist
                        continue_button.setText("Register");
                        info_text.setText("Your username hasn't been registered yet!");

                        continue_button.setOnClickListener(v1 -> {
                            // Create new username on database
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);

                            userDocRef.set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Document successfully written!");
                                        login(username);
                                    })
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
                        });

                    }
                } else {
                    Log.d("Firestore", "Failed to get document: ", task.getException());
                }
            });
        });
    }

    private void login(String username){
        // Save username to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("username", username);
        myEdit.apply();

        // Start MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
