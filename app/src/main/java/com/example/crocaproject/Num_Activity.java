package com.example.crocaproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class Num_Activity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS_CODE = 2;
    private static final int REQUEST_CODE_SPEECH_INPUT = 3;
    private static final String EXTRA_NUM_CAR = "num_car";
    private int currentEditTextId;

    private Button captureButton, addCarButton;
    private LinearLayout[] carLayouts;
    private EditText[] carEditTexts;

    private TextRecognizer recognizer;

    private int addCount = 0;
    private int numCar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initViews();
        initRecognizer();
        checkPermissions();

        showInstructionsDialog();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        numCar = getIntent().getIntExtra(EXTRA_NUM_CAR, 0);
        updateCarLayoutsVisibility();
    }

    private void initViews() {
        captureButton = findViewById(R.id.button);
        carLayouts = new LinearLayout[]{
                findViewById(R.id.ll),
                findViewById(R.id.ll1),
                findViewById(R.id.ll2),
                findViewById(R.id.ll3)
        };
        carEditTexts = new EditText[]{
                findViewById(R.id.userName),
                findViewById(R.id.userName2),
                findViewById(R.id.userName3),
                findViewById(R.id.userName4)
        };

        for (LinearLayout layout : carLayouts) {
            layout.setVisibility(View.GONE);
        }

        for (final EditText editText : carEditTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // إعادة تنسيق النص وإضافته في EditText
                    String text = s.toString();
                    String formattedText = formatVehicleNumber(text);
                    if (!formattedText.equals(text)) {
                        editText.setText(formattedText);
                        editText.setSelection(editText.getText().length()); // تحديد المؤشر بعد الرقم والرمز "-"
                    }
                }
            });
        }
    }

    private String formatVehicleNumber(String text) {

        String formattedText = text.replaceAll("[^0-9]+", "");

        if (formattedText.length() > 7) {
            formattedText = formattedText.substring(0, 7);
        }

        if (formattedText.length() > 2) {
            formattedText = formattedText.substring(0, 2) + "-" + formattedText.substring(2);
        }

        return formattedText;
    }

    private void initRecognizer() {
        TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();
        recognizer = TextRecognition.getClient(options);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // الأذونات متاحة، يمكن استخدام الكاميرا والتخزين
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSIONS_CODE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void updateCarLayoutsVisibility() {
        for (int i = 0; i < numCar; i++) {
            carLayouts[i].setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    // عرض النص المُرجع في EditText المناسبة
                    String spokenText = result.get(0);
                    EditText editText = findViewById(currentEditTextId);
                    if (editText != null) {
                        // تنسيق النص باستخدام الطريقة المضافة
                        String formattedText = formatVehicleNumber(spokenText);
                        editText.setText(formattedText);
                    }
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                processCapturedImage(imageBitmap);
            }
        }
    }

    private void processCapturedImage(Bitmap imageBitmap) {
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);

        if (recognizer != null) {
            recognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String formattedText = formatVehicleNumber(text.getText());
                            fillEmptyEditText(formattedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("فشل استخراج النص. يرجى المحاولة مرة أخرى.");
                        }
                    });
        } else {
            showToast("خطأ: المتعرف على النص غير مدعوم بشكل صحيح.");
        }
    }

    private void fillEmptyEditText(String text) {
        for (EditText editText : carEditTexts) {
            if (TextUtils.isEmpty(editText.getText())) {
                editText.setText(text);
                return;
            }
        }
        showToast("جميع حقول رقم المركبات ممتلئة");
    }

    public void next(View view) {
        String vehicleNumber1 = carEditTexts[0].getText().toString().trim();
        String vehicleNumber2 = carEditTexts[1].getText().toString().trim();
        String vehicleNumber3 = carEditTexts[2].getText().toString().trim();
        String vehicleNumber4 = carEditTexts[3].getText().toString().trim();

        if (numCar >= 1 && TextUtils.isEmpty(vehicleNumber1)) {
            showAlert("يرجى إدخال رقم المركبة الأولى.");
            return;
        }
        if (numCar >= 2 && TextUtils.isEmpty(vehicleNumber2)) {
            showAlert("يرجى إدخال رقم المركبة الثانية.");
            return;
        }
        if (numCar >= 3 && TextUtils.isEmpty(vehicleNumber3)) {
            showAlert("يرجى إدخال رقم المركبة الثالثة.");
            return;
        }
        if (numCar >= 4 && TextUtils.isEmpty(vehicleNumber4)) {
            showAlert("يرجى إدخال رقم المركبة الرابعة.");
            return;
        }

        StringBuilder vehicleNumbers = new StringBuilder();
        if (!TextUtils.isEmpty(vehicleNumber1)) {
            vehicleNumbers.append("المركبة الأولى: ").append(vehicleNumber1).append("\n");
        }
        if (!TextUtils.isEmpty(vehicleNumber2)) {
            vehicleNumbers.append("المركبة الثانية: ").append(vehicleNumber2).append("\n");
        }
        if (!TextUtils.isEmpty(vehicleNumber3)) {
            vehicleNumbers.append("المركبة الثالثة: ").append(vehicleNumber3).append("\n");
        }
        if (!TextUtils.isEmpty(vehicleNumber4)) {
            vehicleNumbers.append("المركبة الرابعة: ").append(vehicleNumber4).append("\n");
        }

        showConfirmationDialog(vehicleNumbers.toString());
    }

    private void showConfirmationDialog(String vehicleNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد رقم المركبات")
                .setMessage("هل أنت متأكد من أرقام المركبات التالية:\n\n" + vehicleNumbers)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startCameraActivity();
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void startCameraActivity() {
        Intent intent = new Intent(Num_Activity.this, ccamera.class);
        for (int i = 0; i < numCar; i++) {
            intent.putExtra("number" + (i + 1), carEditTexts[i].getText().toString().trim());
        }
        intent.putExtra("addCount", addCount);
        intent.putExtra("num_car", numCar);
        startActivity(intent);
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تنبيه")
                .setMessage(message)
                .setPositiveButton("موافق", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void backk(View view) {
        Intent intent = new Intent(Num_Activity.this, num_car.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // الأذونات تم منحها، يمكنك استخدام الكاميرا والتخزين الآن
            } else {
                showToast("الأذونات ضرورية لاستخدام الكاميرا والتخزين.");
            }
        }
    }

    public void mic(View view) {
        int id = view.getId();
        if (id == R.id.micButton) {
            currentEditTextId = R.id.userName;
        } else if (id == R.id.micButton1) {
            currentEditTextId = R.id.userName2;
        } else if (id == R.id.micButton2) {
            currentEditTextId = R.id.userName3;
        } else if (id == R.id.micButton3) {
            currentEditTextId = R.id.userName4;
        } else {
            currentEditTextId = -1;
        }

        if (currentEditTextId != -1) {
            startSpeechToText();
        } else {
            // Handle the case where the view ID is not recognized
            showToast("خطأ: رقم عنصر غير معروف.");
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "أقلم الآن الكلمة");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "عفوا، جهازك لا يدعم المدخلات الصوتية!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInstructionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعليمات")
                .setMessage("مرحبًا! لإدخال رقم المركبة، يمكنك إما التقاط صورة أو استخدام الميكروفون. يرجى التأكد من منح الأذونات اللازمة.")
                .setPositiveButton("موافق", null)
                .show();
    }
}
