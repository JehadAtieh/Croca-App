package com.example.crocaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class all_data extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv_num, textView20, textView7;
    ImageView imageView1, imageView2, imageView3, imageView4;

    int num_car;
    Bitmap[] capturedImages;

    long lastAccidentNumber = 1;

    String numplate1, numplate2, numplate3, numplate4, location;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);

        tv1 = findViewById(R.id.textView12);
        tv2 = findViewById(R.id.textView17);
        tv3 = findViewById(R.id.textView18);
        tv4 = findViewById(R.id.textView19);
        tv_num = findViewById(R.id.textView23);
        textView20 = findViewById(R.id.textView20);
        textView7 = findViewById(R.id.textView7);

        imageView1 = findViewById(R.id.imageView6);
        imageView2 = findViewById(R.id.imageView7);
        imageView3 = findViewById(R.id.imageView8);
        imageView4 = findViewById(R.id.imageView11);

        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.GONE);
        tv4.setVisibility(View.GONE);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        num_car = getIntent().getIntExtra("num_car", num_car);
        tv_num.setText("عدد المركبات المشتركة في الحادث: " + num_car);

        Parcelable[] parcelableImages = getIntent().getParcelableArrayExtra("capturedImages");
        capturedImages = new Bitmap[parcelableImages.length];
        for (int i = 0; i < parcelableImages.length; i++) {
            capturedImages[i] = (Bitmap) parcelableImages[i];
        }

        if (capturedImages != null) {
            if (capturedImages.length > 0) {
                imageView1.setImageBitmap(capturedImages[0]);
            }
            if (capturedImages.length > 1) {
                imageView2.setImageBitmap(capturedImages[1]);
            }
            if (capturedImages.length > 2) {
                imageView3.setImageBitmap(capturedImages[2]);
            }
            if (capturedImages.length > 3) {
                imageView4.setImageBitmap(capturedImages[3]);
            }
        }

        String number1 = getIntent().getStringExtra("number1");
        String number2 = getIntent().getStringExtra("number2");
        String number3 = getIntent().getStringExtra("number3");
        String number4 = getIntent().getStringExtra("number4");
        location = getIntent().getStringExtra("currentLocation");
        textView20.setText("موقع الحادث: " + location);

        if (!TextUtils.isEmpty(number1)) {
            tv1.setText("المركبة الأولى: " + number1);
            tv_num.setText("عدد المركبات المشتركة في الحادث: 1");
            tv1.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(number2)) {
            tv2.setText("المركبة الثانية: " + number2);
            tv_num.setText("عدد المركبات المشتركة في الحادث: 2");
            tv2.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(number3)) {
            tv3.setText("المركبة الثالثة: " + number3);
            tv_num.setText("عدد المركبات المشتركة في الحادث: 3");
            tv3.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(number4)) {
            tv4.setText("المركبة الرابعة: " + number4);
            tv_num.setText("عدد المركبات المشتركة في الحادث: 4");
            tv4.setVisibility(View.VISIBLE);
        }

        // تخزين أرقام لوحات السيارات لاحقًا عند الضغط على زر الإرسال
        numplate1 = number1;
        numplate2 = number2;
        numplate3 = number3;
        numplate4 = number4;

        // Load the last accident number from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("meta_data").document("lastAccidentNumber")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long lastAccidentNumber = documentSnapshot.getLong("lastAccidentNumber");
                        if (lastAccidentNumber != null) {
                            this.lastAccidentNumber = lastAccidentNumber;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error reading last accident number", e);
                });
    }

    public void send(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // تحديث قيمة canWrite إلى true
        db.collection("meta_data").document("canWriteVar")
                .update("canWrite", true)
                .addOnSuccessListener(aVoid -> {
                    // تم تحديث canWrite إلى true، تابع العملية بعد التحديث
                    Toast.makeText(all_data.this, "جاري إرسال البلاغ...", Toast.LENGTH_SHORT).show();
                    saveAccidentData(db);
                })
                .addOnFailureListener(e -> {
                });
        Intent intent=new Intent(all_data.this, public_rules.class);
        startActivity(intent);
    }

    private void saveAccidentData(FirebaseFirestore db) {
        Map<String, Object> accidentData = new HashMap<>();
        accidentData.put("موقع الحادث", location);

        if (!TextUtils.isEmpty(numplate1)) {
            accidentData.put("رقم لوحة المركبة الاولى", numplate1);
        }
        if (!TextUtils.isEmpty(numplate2)) {
            accidentData.put("رقم لوحة المركبة الثانية", numplate2);
        }
        if (!TextUtils.isEmpty(numplate3)) {
            accidentData.put("رقم لوحة المركبة الثالثة", numplate3);
        }
        if (!TextUtils.isEmpty(numplate4)) {
            accidentData.put("رقم لوحة المركبة الرابعة", numplate4);
        }

        // Increase the last accident number by 1
        lastAccidentNumber += 1;

        // Update the value in Firestore
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("lastAccidentNumber", lastAccidentNumber);

        db.collection("meta_data").document("lastAccidentNumber")
                .set(updatedData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Last accident number updated successfully");

                    // Save accident data
                    db.collection("accidents").document("accident_" + lastAccidentNumber)
                            .set(accidentData)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(all_data.this, "تم ارسال بيانات الحادث بنجاح", Toast.LENGTH_SHORT).show();

                                // Upload the images to Firebase Storage
                                for (int i = 0; i < capturedImages.length; i++) {
                                    Bitmap image = capturedImages[i];
                                    String imageName = "image_" + (i + 1) + "_accident_" + lastAccidentNumber + ".jpg";
                                    uploadImageToFirebase(image, imageName, lastAccidentNumber);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(all_data.this, "حدث خطأ أثناء ارسال بيانات الحادث", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating last accident number", e);
                });
    }

    // Upload images to Firebase Storage
    private void uploadImageToFirebase(Bitmap image, String imageName, long num) {
        if (image == null) {
            Log.e("Firebase", "Image is null");
            return;
        }

        // Log to check if the method is being called
        Log.d("Firebase", "Uploading image: " + imageName);

        // تحديد المسار لحفظ الصور مع إضافة رقم الحادث
        String imagePath = "images/accident_" + num + "/" + imageName;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        if (data == null) {
            Log.e("Firebase", "Image data is null");
            return;
        }

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle failure
            Log.e("Firebase", "Error uploading image " + imageName + ": " + exception.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            // Handle success
            Log.d("Firebase", "Image uploaded successfully: " + imageName);
        });
    }

}
