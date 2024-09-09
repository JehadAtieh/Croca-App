package com.example.crocaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class history extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private TextView textViewCarsCount;
    private TextView textViewCar1;
    private TextView textViewCar2;
    private TextView textViewCar3;
    private TextView textViewCar4;
    private TextView textViewLocation;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        editTextSearch = findViewById(R.id.editTextText2);
        buttonSearch = findViewById(R.id.button3);
        textViewCarsCount = findViewById(R.id.textView23);
        textViewCar1 = findViewById(R.id.textView12);
        textViewCar2 = findViewById(R.id.textView17);
        textViewCar3 = findViewById(R.id.textView18);
        textViewCar4 = findViewById(R.id.textView19);
        textViewLocation = findViewById(R.id.textView20);
        imageView1 = findViewById(R.id.imageView6);
        imageView2 = findViewById(R.id.imageView7);
        imageView3 = findViewById(R.id.imageView8);
        imageView4 = findViewById(R.id.imageView11);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accidentNumber = editTextSearch.getText().toString();
                searchAccident(accidentNumber);
            }
        });
    }

    private void searchAccident(String accidentNumber) {
        // Search Firestore for accident data
        db.collection("accidents").document("accident_" + accidentNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Accident data found, display it
                            String car1 = documentSnapshot.getString("رقم لوحة المركبة الاولى");
                            String car2 = documentSnapshot.getString("رقم لوحة المركبة الثانية");
                            String car3 = documentSnapshot.getString("رقم لوحة المركبة الثالثة");
                            String car4 = documentSnapshot.getString("رقم لوحة المركبة الرابعة");
                            String location = documentSnapshot.getString("موقع الحادث");

                            textViewCar1.setText("رقم المركبة الاولى: " + car1);
                            textViewCar2.setText("رقم المركبة الثانية: " + car2);
                            textViewCar3.setText("رقم المركبة الثالثة: " + car3);
                            textViewCar4.setText("رقم المركبة الرابعة: " + car4);
                            textViewLocation.setText("موقع الحادث: " + location);
                            textViewCarsCount.setText(""); // Clear previous messages
                        } else {
                            // No data found for accident number
                            textViewCarsCount.setText("لا يوجد بيانات لرقم الحادث: " + accidentNumber);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error searching accident
                        textViewCarsCount.setText("خطأ في البحث عن الحادث: " + e.getMessage());
                    }
                });

        // Search Firebase Storage for accident images
        StorageReference imagesRef = storage.getReference().child("images/accident_" + accidentNumber + "/");
        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        // Display accident images
                        int i = 0;
                        for (StorageReference item : listResult.getItems()) {
                            if (i == 0) {
                                loadImage(item, imageView1);
                            } else if (i == 1) {
                                loadImage(item, imageView2);
                            } else if (i == 2) {
                                loadImage(item, imageView3);
                            } else if (i == 3) {
                                loadImage(item, imageView4);
                            }
                            i++;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error searching accident images
                        textViewCarsCount.append("\nخطأ في البحث عن الصور: " + e.getMessage());
                    }
                });
    }

    private void loadImage(StorageReference imageRef, final ImageView imageView) {
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Load image into ImageView
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error loading image
                        textViewCarsCount.append("\nخطأ في تحميل الصورة: " + e.getMessage());
                    }
                });
    }
}