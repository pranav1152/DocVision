package com.example.docvision;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleFile extends AppCompatActivity {
    ArrayList<String> arrayList;
    RecyclerView rv;
    ImageView add, pdf;
    SingleFileAdapter adapter;
    int added = 1;
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_file);

        if (!checkAndRequestPermission())
            finish();

        add = findViewById(R.id.add);
        pdf = findViewById(R.id.pdf);
        rv = findViewById(R.id.recyclerview);
        refresh_prefs();

        adapter = new SingleFileAdapter(arrayList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(SingleFile.this, 2));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SingleFile.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                generate_pdf();
            }
        });

    }

    public void refresh_prefs() {
        Gson gson = new Gson();
        SharedPreferences sp = getSharedPreferences("file", MODE_PRIVATE);
        String list = sp.getString("list", "");
        if (!list.equals("")) {
            arrayList = gson.fromJson(list, new TypeToken<List<String>>() {
            }.getType());
        } else {
            arrayList = new ArrayList<>();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generate_pdf() {

        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/DocVision/Docs");
        if(!myDir.exists())
            myDir.mkdirs();
        String fname = "file.pdf";
        File file = new File(myDir, fname);

        new Thread(() -> {
            Bitmap bitmap;
            PdfDocument document = new PdfDocument();
            int height = 1280 + 60;
            int width = 720 + 100;
            int reqH = 1280, reqW = 720;
            int new_width=0, new_height=0;
            for (int i = 0; i < arrayList.size(); i++) {
                Bitmap img = BitmapFactory.decodeFile(root + "/DocVision/Pictures/" + arrayList.get(i) + ".jpg");
                int imheight = img.getHeight();
                int imwidth = img.getWidth();
                new_height = imheight;
                new_width = imwidth;
                if(imheight<height || imwidth>width){ // rotated image
                    new_width = reqW;
                    new_height = imheight*new_width/imwidth;
                    img = Bitmap.createScaledBitmap(img, new_width, new_height, false);
                }

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                canvas.drawBitmap(img, width/2-new_width/2, height/2-new_height/2, null);

                document.finishPage(page);
            }

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SingleFile.this, "PDF Generated", Toast.LENGTH_SHORT).show();
                }
            });

            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID , file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");

            // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) == null) {
                // Show an error
            } else {
                startActivity(intent);
            }

        }).start();
    }

    public boolean checkAndRequestPermission() {
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                listPermissionNeeded.add(permission);
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    //TODO: add on Permission activity result

}

