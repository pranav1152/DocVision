package com.example.docvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    ImageView capture, ok;
    CardView ccapture, cok;
    boolean ptaken = false;
    ImageView iv;
    static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        capture = findViewById(R.id.capture);
        ok = findViewById(R.id.ok);
        ccapture = findViewById(R.id.ccapture);
        cok = findViewById(R.id.cok);

        cok.setVisibility(View.GONE);
        camera = switchOnCamera();
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);


        capture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (!ptaken) {
                    iv = new ImageView(MainActivity.this);
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            bitmap = precessbytes(data);
                            //bitmap = Helper.adaptiveThresholding(bitmap,5,68);
                            ptaken = true;
                            iv.setImageBitmap(bitmap);
                            frameLayout.removeAllViews();
                            frameLayout.addView(iv);
                            camera.release();
                            showCamera = null;
                            capture.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_replay_24));
                            cok.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    ptaken = false;
                    camera = switchOnCamera();
                    showCamera = new ShowCamera(MainActivity.this, camera);
                    frameLayout.removeAllViews();
                    frameLayout.addView(showCamera);
                    capture.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_camera_alt_24));
                    cok.setVisibility(View.GONE);
                }
            }
        });

        cok.setOnClickListener(v -> {

            Intent i = new Intent(MainActivity.this, ImagePreview.class);
            startActivity(i);
            finish();
        });


    }


    public Camera switchOnCamera() {
        Camera camera;
        camera = android.hardware.Camera.open();
        return camera;
    }

    public Bitmap precessbytes(byte[] data) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        return bitmap;
    }

}