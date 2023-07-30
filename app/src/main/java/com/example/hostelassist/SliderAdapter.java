package com.example.hostelassist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    private boolean isMultiScr;
    private Context context;

    public SliderAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }
    public SliderAdapter(boolean isMultiScr, Context context) {
        this.isMultiScr = isMultiScr;
        this.context=context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.image_slider, null);
        //new LinearLayout(container.getContext());
        linearLayout.setId(R.id.item_id);
        switch (position) {
            case 0:
                linearLayout.setBackgroundResource(R.drawable.image_carpenter);
                break;
            case 1:
                linearLayout.setBackgroundResource(R.drawable.image_electrician);
                break;
            case 2:
                linearLayout.setBackgroundResource(R.drawable.image_plumber);
                break;


        }
        container.addView(linearLayout);

        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
    }

}
