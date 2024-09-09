package com.example.crocaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Activity extends AppCompatActivity {

    EditText etEmail, etPassword;
    FirebaseAuth mAuth;
    ImageView imageView;
    Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.userName);
        etPassword = findViewById(R.id.password);
        button=findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // تحقق مما إذا كانت كلمة المرور والبريد الإلكتروني غير فارغة
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    loginUser(email, password);
                } else {
                    // إذا كان أحد الحقول فارغًا، قم بعرض Toast لإبلاغ المستخدم بذلك
                    Toast.makeText(Login_Activity.this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // نجاح تسجيل الدخول
                        Toast.makeText(Login_Activity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login_Activity.this, opstion2.class));
                        finish();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // إذا فشل تسجيل الدخول، قم بعرض Toast لإعلام المستخدم
                            Toast.makeText(Login_Activity.this, "فشل تسجيل الدخول. الرجاء التحقق من المعلومات.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View view) {
        Intent intent=new Intent(Login_Activity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void password(View view) {
        Intent intent=new Intent(Login_Activity.this, Edit_Password.class);
        startActivity(intent);
    }
}
