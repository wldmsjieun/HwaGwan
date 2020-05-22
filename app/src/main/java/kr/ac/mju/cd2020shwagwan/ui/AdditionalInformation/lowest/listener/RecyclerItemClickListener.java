package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private static final String TAG = RecyclerItemClickListener.class.getName();
    private OnItemClickListener rclOnItemClickListener;
    private GestureDetector rclGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        rclOnItemClickListener = listener;
        rclGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View rclChildView =  view.findChildViewUnder(e.getX(), e.getY());
        if (rclChildView != null && rclOnItemClickListener != null && rclGestureDetector.onTouchEvent(e)) {
            rclOnItemClickListener.onItemClick(rclChildView, view.getChildAdapterPosition(rclChildView));
            return true;
        }
        return false;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
