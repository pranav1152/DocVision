package com.example.docvision;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class OCRText extends AppCompatActivity {
TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r_text);
        tv = findViewById(R.id.ocrtext);
        String text = getIntent().getExtras().getString("ocrtext");
        if(text==null)
            text = "";
        tv.setText(text);
    }
}