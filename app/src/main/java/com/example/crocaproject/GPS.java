package com.example.crocaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPS extends AppCompatActivity {

    private Bitmap[] capturedImages; // مصفوفة لتخزين الصور التي تم التقاطها
    public int num_car;
    private TextView locationTextView;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        num_car = getIntent().getIntExtra("num_car", num_car);

        // استلام البيانات التي تم التقاطها من النشاط "ccamera"
        Parcelable[] parcelableImages = getIntent().getParcelableArrayExtra("capturedImages");

        // تحويل البيانات إلى مصفوفة Bitmap[]
        if (parcelableImages != null) {
            capturedImages = new Bitmap[parcelableImages.length];
            for (int i = 0; i < parcelableImages.length; i++) {
                capturedImages[i] = (Bitmap) parcelableImages[i];
            }
        }

        // إعداد عنصر TextView لعرض الموقع
        locationTextView = findViewById(R.id.locationTextView);

        // إعداد FusedLocationProviderClient للحصول على الموقع
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // قم بالتحقق من وجود إذن للوصول إلى الموقع
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // تعديل الدالة next(View view) لإضافة الموقع الحالي
    public void next(View view) {
        // نقل المصفوفة capturedImages والموقع الحالي إلى النشاط "all_data"
        Intent intent = new Intent(GPS.this, all_data.class);
        intent.putExtra("capturedImages", capturedImages);
        intent.putExtra("currentLocation", locationTextView.getText().toString());

        Intent previousIntent = getIntent();
        intent.putExtra("number1", previousIntent.getStringExtra("number1"));
        intent.putExtra("number2", previousIntent.getStringExtra("number2"));
        intent.putExtra("number3", previousIntent.getStringExtra("number3"));
        intent.putExtra("number4", previousIntent.getStringExtra("number4"));
        intent.putExtra("addCount", previousIntent.getIntExtra("addCount", 0));
        intent.putExtra("num_car", num_car);
        intent.putExtra("locationTextView", previousIntent.getStringExtra("locationTextView"));

        startActivity(intent);
    }

    public void backk(View view) {
        Intent intent = new Intent(GPS.this, ccamera.class);
        startActivity(intent);
    }

        // تعديل الدالة current_location(View view) للحصول على الموقع الحالي
        public void current_location(View view) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        StringBuilder locationText = new StringBuilder();

                                        if (address.getAdminArea() != null) {
                                            locationText.append(address.getAdminArea()).append(", ");
                                        }

                                        if (address.getLocality() != null) {
                                            locationText.append(address.getLocality()).append(", ");
                                        }
                                        if (address.getSubLocality() != null) {
                                            locationText.append(address.getSubLocality()).append(", ");
                                        }

                                        if (address.getThoroughfare() != null) {
                                            locationText.append(address.getThoroughfare()).append(", ");
                                        }

                                        if (address.getFeatureName() != null) {
                                            locationText.append(address.getFeatureName());
                                        }
                                        locationTextView.setText(locationText.toString());
                                    } else {
                                        locationTextView.setText("Unable to retrieve location.");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    locationTextView.setText("Error getting location: " + e.getMessage());
                                }
                            } else {
                                locationTextView.setText("Unable to retrieve location.");
                            }
                        })
                        .addOnFailureListener(e -> locationTextView.setText("Error getting location: " + e.getMessage()));
            }
        }
    }