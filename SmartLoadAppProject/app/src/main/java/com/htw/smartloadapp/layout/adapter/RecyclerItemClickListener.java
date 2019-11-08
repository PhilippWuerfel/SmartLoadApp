package com.htw.smartloadapp.layout.adapter;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    public interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private final OnRecyclerClickListener listener;
    private final GestureDetectorCompat gestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && RecyclerItemClickListener.this.listener != null) {
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    RecyclerItemClickListener.this.listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && RecyclerItemClickListener.this.listener != null) {
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    RecyclerItemClickListener.this.listener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });

    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if(gestureDetector != null) {
            boolean result = gestureDetector.onTouchEvent(e);
            return result;
        } else {
            return false;
        }
    }
}
