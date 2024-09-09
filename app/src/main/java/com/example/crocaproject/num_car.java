package com.example.crocaproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class num_car extends AppCompatActivity {
    int num_car = 0;
    EditText editText;
    Button nextButton,btnj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_car);

        editText = findViewById(R.id.edit1);
        nextButton = findViewById(R.id.button15);

       // editText.setVisibility(View.GONE);
        //nextButton.setVisibility(View.GONE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // استخراج القيمة المدخلة من EditText وتحويلها إلى عدد صحيح
                String numCarText = editText.getText().toString();
                if (!numCarText.isEmpty()) {
                    num_car = Integer.parseInt(numCarText);
                    // قم بإرسال قيمة num_car إلى الصفحة التالية
                    Intent intent = new Intent(num_car.this, Num_Activity.class);
                    intent.putExtra("num_car", num_car);
                    startActivity(intent);
                } else {
                    // إذا كانت EditText فارغة، قم بعرض رسالة تنبيه
                    Toast.makeText(num_car.this, "الرجاء إدخال عدد المركبات", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void backk(View view) {
        Intent intent = new Intent(num_car.this, qustion.class);
        startActivity(intent);
    }

    public void num1(View view) {
        num_car = 1;
        Toast.makeText(this, "تم اضافة مركبة", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(num_car.this,Num_Activity.class);
        intent.putExtra("num_car", num_car);
        startActivity(intent);
    }

    public void num2(View view) {
        num_car = 2;
        Toast.makeText(this, "تم اضافة مركبتين", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(num_car.this,Num_Activity.class);
        intent.putExtra("num_car", num_car);
        startActivity(intent);
    }

    public void vistable(View view) {
        editText.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
    }
}
