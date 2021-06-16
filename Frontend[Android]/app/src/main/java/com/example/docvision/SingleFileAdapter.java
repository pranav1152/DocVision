package com.example.docvision;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class SingleFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<String> data;
    private final Context context;

    public SingleFileAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageview = LayoutInflater.from(context).inflate(R.layout.indv_list_item, parent, false);
        return new ItemViewHolder(imageview);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String completePath = Environment.getExternalStorageDirectory() + "/DocVision/Pictures/" + data.get(position) + ".jpg";
        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(context)
                .load(imageUri)
                .into(((ItemViewHolder) holder).iv);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.image);
        }
    }
}
