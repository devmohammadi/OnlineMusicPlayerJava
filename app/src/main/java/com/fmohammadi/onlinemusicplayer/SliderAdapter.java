package com.fmohammadi.onlinemusicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SlidersItems> slidersItems;

    public SliderAdapter (List<SlidersItems> slidersItems){
        this.slidersItems = slidersItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            imageView.setImageResource(slidersItems.getImage());
        }
    }
}
