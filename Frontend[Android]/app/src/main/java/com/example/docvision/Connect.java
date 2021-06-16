package com.example.docvision;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connect {
    Requests requests;
    Retrofit retrofit;
    Context context;

    public Connect(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.43.165:5000/").addConverterFactory(GsonConverterFactory.create()).build();
        requests = retrofit.create(Requests.class);
    }


    public void ops(String op, String filename, Bitmap bitmap) {
        MultipartBody.Part body = uploadBody(bitmap, filename);

        Call<_Response> call = requests.doOP(body, op);
        call.enqueue(new Callback<_Response>() {
            @Override
            public void onResponse(Call<_Response> call, Response<_Response> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    if(op.equals("adaptivethresholding"))
                    ((ImagePreview) context).callback(new _Response(down(filename), true));
                    else
                        ((ImagePreview) context).callback(response.body());
                } else {
                    Toast.makeText(context, "Error :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<_Response> call, Throwable t) {
                Toast.makeText(context, "Error :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private MultipartBody.Part uploadBody(Bitmap bitmap, String filename) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        File f = new File(context.getCacheDir(), filename + ".jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            Toast.makeText(context, "An error occure while reading image :(", Toast.LENGTH_SHORT).show();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Error while creating file :(", Toast.LENGTH_SHORT).show();
        }
        try {
            fos.write(byteArray);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Toast.makeText(context, "Unknown error occured :(", Toast.LENGTH_SHORT).show();
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", f.getName(), reqFile);

        return body;
    }

    public String down(String filename) {
        return retrofit.baseUrl() + "getOP/?filename=" + filename;

    }


}
