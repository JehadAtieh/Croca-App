package com.example.crocaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ccamera extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS_CODE = 2;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private Button captureButton;
    public int num_car; // تم الاحتفاظ بالمتغير num_car كمتغير عام

    private Bitmap[] capturedImages = new Bitmap[4]; // مصفوفة لتخزين الصور التي تم التقاطها

    private int imageCount = 0; // لتتبع عدد الصور التي تم التقاطها

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ccamera);

        captureButton = findViewById(R.id.button);
        imageView1 = findViewById(R.id.imageView15);
        imageView2 = findViewById(R.id.imageView9);
        imageView3 = findViewById(R.id.imageView16);
        imageView4 = findViewById(R.id.imageView17);

        // نقل قيمة num_car من النشاط السابق إلى هذا النشاط
        num_car = getIntent().getIntExtra("num_car", num_car);

        // التحقق من وجود أذونات الكاميرا والتخزين
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // لديك الأذونات المطلوبة، يمكنك استخدام الكاميرا والتخزين هنا
        } else {
            // ليست لديك الأذونات، قم بطلبها من المستخدم
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // إطلاق الكاميرا والتقاط صورة
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // تقليل دقة الصورة مع الحفاظ على الجودة العالية
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

                    // تقليل الدقة بناءً على حجم الصورة
                    options.inSampleSize = calculateInSampleSize(options, 800, 800); // العرض والارتفاع المستهدفين
                    options.inJustDecodeBounds = false;
                    Bitmap reducedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

                    // تحديد الـ ImageView الذي سيتم عرض الصورة عليه بناءً على عدد الصور التي تم التقاطها
                    ImageView imageViewToSet = null;
                    switch (imageCount) {
                        case 0:
                            imageViewToSet = imageView1;
                            break;
                        case 1:
                            imageViewToSet = imageView2;
                            break;
                        case 2:
                            imageViewToSet = imageView3;
                            break;
                        case 3:
                            imageViewToSet = imageView4;
                            break;
                    }

                    if (imageViewToSet != null) {
                        // التحقق من عدم فراغ ImageView قبل وضع الصورة
                        if (imageViewToSet.getDrawable() == null) {
                            imageViewToSet.setImageBitmap(reducedBitmap);
                            capturedImages[imageCount] = reducedBitmap;
                            imageCount++;
                        } else {
                            Toast.makeText(this, "الـ ImageView ممتلئة بالفعل.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    public void backk(View view) {
        Intent intent = new Intent(ccamera.this, Num_Activity.class);
        startActivity(intent);
    }

    public void clear() {
        imageView1.setImageDrawable(null);
        imageView2.setImageDrawable(null);
        imageView3.setImageDrawable(null);
        imageView4.setImageDrawable(null);
    }

    public void goToGPS(View view) {
        // نقل الصور التي تم التقاطها إلى النشاط "GPS"
        Intent intent = new Intent(ccamera.this, GPS.class);
        intent.putExtra("capturedImages", capturedImages);

        Intent previousIntent = getIntent();
        intent.putExtra("number1", previousIntent.getStringExtra("number1"));
        intent.putExtra("number2", previousIntent.getStringExtra("number2"));
        intent.putExtra("number3", previousIntent.getStringExtra("number3"));
        intent.putExtra("number4", previousIntent.getStringExtra("number4"));
        intent.putExtra("addCount", previousIntent.getIntExtra("addCount", 0));
        intent.putExtra("num_car", num_car);

        startActivity(intent);
        clear();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
