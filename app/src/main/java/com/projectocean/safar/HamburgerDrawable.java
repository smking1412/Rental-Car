package com.projectocean.safar;

import android.content.Context;
import android.graphics.Canvas;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

public class HamburgerDrawable extends DrawerArrowDrawable {

    public HamburgerDrawable(Context context){
        super(context);
        setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        setBarLength(90.0f);
        setBarThickness(10.0f);
        setGapSize(20.0f);
    }
}