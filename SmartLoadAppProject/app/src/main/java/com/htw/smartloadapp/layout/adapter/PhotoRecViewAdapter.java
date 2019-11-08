package com.htw.smartloadapp.layout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htw.smartloadapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class PhotoRecViewAdapter extends RecyclerView.Adapter<PhotoRecViewAdapter.PhotoImageViewHolder> {
    private static final String TAG = "PhotoRecViewAdapter";
    private ArrayList<File> uploadPhotoList;
    private Context context;

    public PhotoRecViewAdapter(Context context, ArrayList<File> uploadPhotoList) {
        this.context = context;
        this.uploadPhotoList = uploadPhotoList;
    }

    @NonNull
    @Override
    public PhotoImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item_photomaker, viewGroup, false);
        return new PhotoImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoImageViewHolder photoImageViewHolder, final int position) {
        File photoFile = uploadPhotoList.get(position);
        Log.d(TAG, "onBindViewHolder: New File " + photoFile + " added at position " + position);
        Picasso.get().load(photoFile)
                .error(R.drawable.baseline_image_black_48dp)
                .placeholder(R.drawable.baseline_image_black_48dp)
                .into(photoImageViewHolder.imageView);

        photoImageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ((uploadPhotoList != null) && uploadPhotoList.size() != 0) ? uploadPhotoList.size() : 0;
    }

    public void loadNewData(ArrayList<File> newPhotos){
        uploadPhotoList = newPhotos;
        // call registered observer
        notifyDataSetChanged();
    }

    public File getPhotoFile(int position){
        return ((uploadPhotoList != null) && (uploadPhotoList.size() != 0) ? uploadPhotoList.get(position) : null);
    }

    static class PhotoImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "PhotoImageViewHolder";
        ImageView imageView = null;

        public PhotoImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.ivPhotoItemRecyclerView);
        }
    }
}
