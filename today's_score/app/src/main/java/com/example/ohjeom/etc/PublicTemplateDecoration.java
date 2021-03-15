package com.example.ohjeom.etc;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class PublicTemplateDecoration extends RecyclerView.ItemDecoration {

    private final int divWidth;
    private final int divHeight;

    public PublicTemplateDecoration(int divWidth,int divHeight)
    {
        this.divWidth = divWidth;
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = divWidth;
        outRect.left = divWidth;
        outRect.top = divHeight;
        outRect.bottom = divHeight;
    }
}
