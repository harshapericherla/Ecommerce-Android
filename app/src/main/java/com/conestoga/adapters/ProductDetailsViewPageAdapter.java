package com.conestoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.conestoga.activities.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductDetailsViewPageAdapter extends PagerAdapter {

    private Context context;
    private List<String> imageList;
    public ProductDetailsViewPageAdapter(Context context,List<String> imageList)
    {
        this.context = context;
        this.imageList = imageList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item,null);
        ImageView imageView = view.findViewById(R.id.product_images);
        String imageUrl = getImageAt(position);
        Picasso.get().load(imageUrl).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    private String getImageAt(int position){
       return imageList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
