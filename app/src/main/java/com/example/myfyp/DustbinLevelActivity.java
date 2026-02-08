package com.example.myfyp;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DustbinLevelActivity extends AppCompatActivity {

    private TextView bin1LevelText, bin2LevelText;
    private ProgressBar bin1ProgressBar, bin2ProgressBar;
    private Button btnUpdate;
    private boolean notified70 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_level);

        requestNotificationPermission();

        bin1LevelText = findViewById(R.id.bin1LevelText);
        bin2LevelText = findViewById(R.id.bin2LevelText);
        bin1ProgressBar = findViewById(R.id.bin1ProgressBar);
        bin2ProgressBar = findViewById(R.id.bin2ProgressBar);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DustbinLevelActivity.this, UpdateActivity.class);
                startActivity(intent);
            }
        });

        DatabaseReference bin1Ref = FirebaseDatabase.getInstance().getReference("dustbins/bin1/trashLevel");
        DatabaseReference bin2Ref = FirebaseDatabase.getInstance().getReference("dustbins/bin2/trashLevel");

        bin1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer level = snapshot.getValue(Integer.class);
                if (level != null) {
                    bin1LevelText.setText("Teratai Bin 1: " + level + "%");
                    ObjectAnimator.ofInt(bin1ProgressBar, "progress", bin1ProgressBar.getProgress(), level).setDuration(500).start();

                    if (level >= 70 && !notified70) {
                        showNotification("Teratai Bin 1", level);
                        notified70 = true;
                    } else if (level < 70) {
                        notified70 = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bin1LevelText.setText("Error loading bin 1");
            }
        });

        bin2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer level = snapshot.getValue(Integer.class);
                if (level != null) {
                    bin2LevelText.setText("Teratai Bin 2: " + level + "%");
                    ObjectAnimator.ofInt(bin2ProgressBar, "progress", bin2ProgressBar.getProgress(), level).setDuration(500).start();

                    if (level >= 70 && !notified70) {
                        showNotification("Teratai Bin 2", level);
                        notified70 = true;
                    } else if (level < 70) {
                        notified70 = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bin2LevelText.setText("Error loading bin 2");
            }
        });
    }

    private void showNotification(String binName, int level) {
        String channelId = "trash_channel_id";
        String channelName = "Trash Notification Channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for Trash Level");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(binName + " Alert")
                .setContentText("Trash level in " + binName + " reached " + level + "%! Please empty it.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(binName.equals("Bin 1") ? 1 : 2, builder.build());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}