package com.example.myfirstapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListView extends ListView {

    public CustomListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 0;
    }
}
