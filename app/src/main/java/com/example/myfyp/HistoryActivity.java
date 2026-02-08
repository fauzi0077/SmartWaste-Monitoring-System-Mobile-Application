package com.example.myfyp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> historyList;
    private DatabaseReference historyRef;
    private String username = "guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        // Read username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        username = prefs.getString("username", "guest");

        // Firebase path: cleaningRecord/username
        historyRef = FirebaseDatabase.getInstance()
                .getReference("cleaningRecord")
                .child(username);

        // Load data
        loadHistory();
    }

    private void loadHistory() {
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear(); // Clear previous data

                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        String status = recordSnapshot.child("status").getValue(String.class);
                        Object timestampObj = recordSnapshot.child("timestamp").getValue();

                        String formattedTime = "";

                        if (timestampObj instanceof Long) {
                            // If using ServerValue.TIMESTAMP (correct way)
                            long timestamp = (Long) timestampObj;
                            formattedTime = formatTimestamp(timestamp);
                        } else if (timestampObj instanceof String) {
                            // If using string timestamp (old version of your code)
                            formattedTime = (String) timestampObj;
                        }

                        String record = "Status: " + status + "\nTime: " + formattedTime;
                        historyList.add(record);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HistoryActivity.this, "No cleaning history found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
}
