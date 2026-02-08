package com.example.myfyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    private CheckBox checkBoxCleaned;
    private TextView statusText;
    private Button btnHistory, btnBack;
    private DatabaseReference updateRef;
    private String username = "guest"; // fallback value if no username found

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Link views
        checkBoxCleaned = findViewById(R.id.checkBoxCleaned);
        statusText = findViewById(R.id.statusText);
        btnHistory = findViewById(R.id.btnHistory);

        // ✅ Read username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        username = prefs.getString("username", "guest");

        // ✅ Firebase path: cleaningRecord/username
        updateRef = FirebaseDatabase.getInstance()
                .getReference("cleaningRecord")
                .child(username);

        // ✅ Cleaning checkbox listener
        checkBoxCleaned.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // When checked: Save "Cleaned" status with timestamp
                String timestamp = getCurrentTime();
                HashMap<String, Object> record = new HashMap<>();
                record.put("status", "Cleaned");
                record.put("timestamp", timestamp);

                // ✅ PUSH to Firebase (multiple records!)
                updateRef.push()
                        .setValue(record)
                        .addOnSuccessListener(aVoid -> statusText.setText("✅ Cleaning recorded at:\n" + timestamp))
                        .addOnFailureListener(e -> statusText.setText("❌ Failed to record cleaning."));
            }
        });

        // ✅ History button → open HistoryActivity
        btnHistory.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

    }

    // Helper method to get current date and time
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
