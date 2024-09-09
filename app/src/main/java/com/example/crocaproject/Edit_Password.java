package com.example.crocaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Random;

public class Edit_Password extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private Button sendVerificationCodeButton;
    private boolean isCodeVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextText);


        sendVerificationCodeButton = findViewById(R.id.button6);

        sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(Edit_Password.this, "يرجى إدخال البريد الإلكتروني", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Edit_Password.this, "تم ارسال رابط تغيير كلمة السر الى بريدك.", Toast.LENGTH_SHORT).show();

                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(Edit_Password.this, "البريد الإلكتروني غير موجود.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Edit_Password.this, "حدث خطأ أثناء إرسال رابط تغيير كلمة السر الى بريدك.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }


    private String generateRandomVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public void backk(View view) {
        Intent intent=new Intent(Edit_Password.this,Login_Activity.class);
        startActivity(intent);
    }
}