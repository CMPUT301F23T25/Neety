package com.team25.neety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
    private Button continue_button, register_button;
    TextView info_text;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private String username;

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
        register_button = findViewById(R.id.register_button);
        info_text = findViewById(R.id.notification_text);

        continue_button.setEnabled(false);
        username_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Perform your action on key press here
                    continue_button.performClick();
                    return true;
                }
                return false;
            }
        });

        username_box.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z0-9!@#$%^&*_-]+")){
                            return src;
                        }
                        return "";
                    }
                }
        });

        username_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that, within s,
                // the count characters beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that, within s,
                // the count characters beginning at start have just replaced old text that had length before.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
                username = s.toString();
                continue_button.setVisibility(View.VISIBLE);
                register_button.setVisibility(View.GONE);

                if (username.isEmpty()) {
                    info_text.setText("Username must be at least 6 characters");
                    continue_button.setEnabled(false);
                    return;
                }

                if (username.length() < 6) {
                    info_text.setText("Username must be at least 6 characters");
                    continue_button.setEnabled(false);
                } else {
                    info_text.setText("Tap Continue to log in");
                    continue_button.setEnabled(true);
                }
            }
        });

        continue_button.setOnClickListener(v -> {
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
                        register_button.setVisibility(View.VISIBLE);
                        continue_button.setVisibility(View.GONE);
                        info_text.setText("Your username hasn't been registered yet!");
                    }
                } else {
                    Log.d("Firestore", "Failed to get document: ", task.getException());
                }
            });
        });

        register_button.setOnClickListener(v -> {
            // Create new username on database
            DocumentReference userDocRef = usersRef.document(username);
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

    private void login(String username){

        // Save username to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("username", username);
        myEdit.apply();

        info_text.setText("Enter your username to continue");
        continue_button.setVisibility(View.VISIBLE);
        register_button.setVisibility(View.GONE);

        // Start MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
