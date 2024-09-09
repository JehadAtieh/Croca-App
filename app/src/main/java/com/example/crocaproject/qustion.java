package com.example.crocaproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class qustion extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qustion);
    }

    public void not_accept(View view) {
        Intent intent = new Intent(qustion.this, num_car.class);
        startActivity(intent);
    }

    public void back(View view) {
        Intent intent = new Intent(qustion.this, opstion2.class);
        startActivity(intent);
    }

    public void yse(View view) {
        EmergencyCall();
    }

    private void EmergencyCall() {
        String phoneNumber = "tel:911";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            startCall(phoneNumber);
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "حدث خطأ أثناء محاولة الاتصال. يرجى المحاولة مرة أخرى.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EmergencyCall();
            } else {
                Toast.makeText(this, "الإذن بإجراء المكالمات مطلوب.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
