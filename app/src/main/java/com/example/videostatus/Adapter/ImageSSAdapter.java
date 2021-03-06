package com.example.videostatus.Adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.videostatus.InterFace.InterstitialAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.example.videostatus.R;
import com.example.videostatus.Util.Method;

import java.io.File;
import java.util.List;

public class ImageSSAdapter extends RecyclerView.Adapter<ImageSSAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String type;
    private List<File> imageList;

    public ImageSSAdapter(Activity activity, List<File> imageList, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        this.imageList = imageList;
        this.type = type;
        method=new Method(activity,interstitialAdView);
    }

    @NonNull
    @Override
    public ImageSSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.image_ss_adapter, parent, false);

        return new ImageSSAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSSAdapter.ViewHolder holder, final int position) {

        Glide.with(activity).load("file://" + imageList.get(position).toString()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position,type,"");
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_ss_adapter);

        }
    }
}
