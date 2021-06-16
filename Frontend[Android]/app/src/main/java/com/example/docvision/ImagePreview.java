package com.example.docvision;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.docvision.MainActivity.bitmap;

public class ImagePreview extends AppCompatActivity {


    Map<Integer, String> map;
    TabLayout tabLayout;
    SeekBar seek;
    FrameLayout fl;
    ImageView iv, rleft, rright, save;
    Bitmap orig_bitmap;
    Bitmap processedBitmap;
    ProgressBar pb;
    boolean pbvisible = false;
    String filename;
    Context context;
    Connect connect;
    TextView progressNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        tabLayout = findViewById(R.id.tablayout);
        seek = findViewById(R.id.seek);
        progressNumber = findViewById(R.id.progressNumber);
        fl = findViewById(R.id.iv);
        iv = new ImageView(this);
        iv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        rleft = findViewById(R.id.rleft);
        rright = findViewById(R.id.rright);
        save = findViewById(R.id.save);

        fl.addView(iv);
        pb = findViewById(R.id.progress);
        pb.setVisibility(View.GONE);
        context = this;
        processedBitmap = Bitmap.createBitmap(bitmap);
        filename = getalpnum(10);
        connect = new Connect(context);

        iv.setImageBitmap(bitmap);
        map = new HashMap<>();
        map.put(0, "original");
        map.put(1, "grey");
        map.put(2, "bw1");
        map.put(3, "bw2");
        map.put(4, "ocr");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pbvis();
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        processedBitmap = Bitmap.createBitmap(bitmap);
                        iv.setImageBitmap(processedBitmap);
                        pbvis();
                        break;
                    case 1:
                    case 2:
                        ivRunnable(map.get(pos));
                        break;
                    case 3:
                        Helper.ada(bitmap, filename, connect);
                        break;
                    case 4:
                        Helper.ocr(bitmap, filename, connect);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pbvis();
                int val = (progress - 10) * 5;
                progressNumber.setText(String.valueOf(val));
                progressNumber.setX(seekBar.getThumb().getBounds().centerX());
                ivRunnable(val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processedBitmap = rotate(-90);
                iv.setImageBitmap(processedBitmap);
            }
        });
        rright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processedBitmap = rotate(90);
                iv.setImageBitmap(processedBitmap);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _save();
            }
        });

    }

    private Bitmap rotate(int val) {
        Matrix matrix = new Matrix();

        matrix.postRotate(val);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(processedBitmap, processedBitmap.getWidth(), processedBitmap.getHeight(), true);

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    private String getalpnum(int n) {
        String AlphaNumericString = "0123456789" +
                "ABCDEFGHIJKLMNOPQRSUVWXYS" +
                "abcdefghijklmnopqrstuvwxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();

    }

    public void callback(_Response response) {
        pbvis();
        if (response.isurl) {
            String url = response.url;
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(iv);
            new Thread(() -> Glide.get(context).clearDiskCache()).start();
        } else {
            //todo: display text returned by ocr
            Intent i = new Intent(ImagePreview.this, OCRText.class);
            i.putExtra("ocrtext", response.url);
            startActivity(i);
        }
    }


    public void ivRunnable(String op) {
        processedBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        new Thread(() -> {
            switch (op) {
                case "grey":
                    processedBitmap = Helper.toGrey(processedBitmap);
                    break;
                case "bw1":
                    processedBitmap = Helper.threshold(processedBitmap);
                    break;
            }
            runOnUiThread(() -> {
                iv.setImageBitmap(processedBitmap);
                pbvis();
            });
        }).start();

    }

    public void ivRunnable(int val) {
        processedBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        new Thread(() -> {
            processedBitmap = Helper.adjustBrightness(processedBitmap, val);
            runOnUiThread(() -> {
                iv.setImageBitmap(processedBitmap);
                pbvis();
            });
        }).start();

    }

    public void pbvis() {
        if (pbvisible) {
            pb.setVisibility(View.GONE);
            pbvisible = false;
        } else {
            pb.setVisibility(View.VISIBLE);
            pbvisible = true;
        }
    }

    private void _save() {
        Gson gson = new Gson();
        SharedPreferences sp = getSharedPreferences("file", MODE_PRIVATE);
        String list = sp.getString("list", "");
        ArrayList<String> arrayList;
        if (!list.equals("")) {
            arrayList = gson.fromJson(list, new TypeToken<List<String>>() {
            }.getType());
        } else {
            arrayList = new ArrayList<>();
        }
        arrayList.add(filename);
        String json = gson.toJson(arrayList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("list", json);
        editor.apply();


        Bitmap finalBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/DocVision/Pictures");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = filename + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();

        }
        Intent i = new Intent(ImagePreview.this, SingleFile.class);
        startActivity(i);
        finish();


    }

}