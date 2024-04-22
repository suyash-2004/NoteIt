package com.example.noteit_new.RecyclerTouchListener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    /* loaded from: classes.dex */
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        this.clickListener = clickListener;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.example.noteit.RecyclerTouchListener.RecyclerTouchListener.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                ClickListener clickListener2;
                View findChildViewUnder = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (findChildViewUnder == null || (clickListener2 = clickListener) == null) {
                    return;
                }
                clickListener2.onLongClick(findChildViewUnder, recyclerView.getChildAdapterPosition(findChildViewUnder));
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View findChildViewUnder = rv.findChildViewUnder(e.getX(), e.getY());
        if (findChildViewUnder == null || this.clickListener == null || !this.gestureDetector.onTouchEvent(e)) {
            return false;
        }
        this.clickListener.onClick(findChildViewUnder, rv.getChildAdapterPosition(findChildViewUnder));
        return false;
    }
}
