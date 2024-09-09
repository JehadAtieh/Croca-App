package com.example.crocaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class opstion2 extends AppCompatActivity {

     ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opstion2);

        constraintLayout=findViewById(R.id.constraintLayout);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(opstion2.this, qustion.class);
                startActivity(intent);
            }
        });

    }

    public void regester(View view) {
        Intent intent = new Intent(opstion2.this, history.class);
        startActivity(intent);
    }
}