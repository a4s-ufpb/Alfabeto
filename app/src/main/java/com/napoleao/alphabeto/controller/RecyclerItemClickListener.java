package com.napoleao.alphabeto.controller;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Class responsible for implementing the click methods in the RecyclerView.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;
    GestureDetector mGestureDetector;

    /**
     * Method responsible for intercepting the touch event.
     *
     * @param rv
     * @param e
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    /**
     * Method responsible for intercepting the touch event.
     *
     * @param rv
     * @param e
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    /**
     * Method responsible for intercepting the touch event.
     *
     * @param disallowIntercept
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * Interface responsible for implementing the click methods in the RecyclerView.
     */
    public interface OnItemClickListener extends AdapterView.OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    /**
     * Method responsible for intercepting the touch event.
     *
     * @param context
     * @param recyclerView
     * @param listener
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });

    }
}