package com.fmohammadi.onlinemusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SlidersItems> slidersItems;
    Context mContext;

    public SliderAdapter (List<SlidersItems> slidersItems){
        this.slidersItems = slidersItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new SliderViewHolder((LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_items , parent , false)));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImageView(slidersItems.get(position));
    }

    @Override
    public int getItemCount() {
        return slidersItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images_Slider);
        }

        void setImageView (SlidersItems slidersItems){
            Glide.with(mContext)
                    .load(slidersItems.getImageurl())
                    .override(300 , 300)
                    .into(imageView);
        }
    }
}
