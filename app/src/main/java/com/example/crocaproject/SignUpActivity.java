package com.example.crocaproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText userName, etEmail, etPassword, rePassword;
    private FirebaseAuth mAuth;

    Button button;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userName);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        button=findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String repass = rePassword.getText().toString();

                // التحقق من ملء جميع الحقول ومطابقة كلمة المرور وإعادتها
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repass)) {
                    showAlert("يرجى ملء جميع الحقول");
                } else if (!password.equals(repass)) {
                    showAlert("كلمات المرور غير متطابقة");
                } else {
                    // إذا تم تلبية جميع الشروط، سجل الحساب
                    registerUser(name, email, password);
                }
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Successful registration
                            showAlert("تم التسجيل بنجاح");
                            goToLoginActivity(); // انتقل إلى صفحة تسجيل الدخول بعد التسجيل
                        } else {
                            // Registration failed
                            showAlert("فشل التسجيل");
                        }
                    }
                });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SignUpActivity.this, Login_Activity.class);
        startActivity(intent);
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تنبيه")
                .setMessage(message)
                .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // لا يوجد أي عمل عند النقر على "موافق"
                    }
                })
                .show();
    }

    public void go_to_Login(View view) {
        Intent intent=new Intent(SignUpActivity.this,Login_Activity.class);
        startActivity(intent);
    }

    public void backk(View view) {
        Intent intent=new Intent(SignUpActivity.this,Login_Activity.class);
        startActivity(intent);
    }
}
