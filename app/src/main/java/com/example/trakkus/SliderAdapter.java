//Amritpal Singh
package com.example.trakkus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    // initilize the veriables
    int[] images;

    // create constructor
    public SliderAdapter(int[] images) {

        this.images = images;
    }


    @Override
    public SliderAdapter.Holder onCreateViewHolder(ViewGroup parent) {
        // Initilize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        // return View
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapter.Holder viewHolder, int position) {
        // set Images on Image View;
        viewHolder.imageView.setImageResource(images[position]);


    }

    @Override
    public int getCount() {

        return images.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder {

        // declare image view
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            // Initilize Image View
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
