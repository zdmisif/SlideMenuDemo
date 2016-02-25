package com.hzz.slidemenudemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

/**
 * @Description 右滑显示菜单布局
 * @auth zhangdm
 * @date 2016/1/27 9:42
 */
public class SlideMenuLayout extends LinearLayout {

    public static final String TAG = "SlideMenuLayout";

    private ViewDragHelper viewDragHelper;
    private View contentView;
    private View actionView;
    private int dragDistance;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int draggedX;

    /**
     * 点击的按下的点坐标
     */
    private float pointX;
    private float pointY;

    /**
     * 设置点击的范围距离
     */
    private float clickRange = 20;

    private float dragRange = 10;


    public SlideMenuLayout(Context context) {
        this(context, null);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        contentView = getChildAt(0);
        actionView = getChildAt(1);
        actionView.setVisibility(GONE);
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取菜单布局的宽度
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        actionView.measure(width, height);
        dragDistance = actionView.getMeasuredWidth();
        Log.i(TAG, "dragDistance=" + dragDistance);

    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            //当前view是否允许拖动，设置只有contentView才能拖动
            return view == contentView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //view在拖动过程坐标发生变化时会调用此方法，包括两个时间段：手动拖动和自动滚动
            draggedX = left;
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //返回横向坐标左右边界值
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
                return newLeft;
            } else {
                final int minLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound), maxLeftBound);
                return newLeft;
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            //设置横向拖动的最大距离
            return dragDistance;
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //ACTION_UP事件后调用其方法
            boolean settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (draggedX <= -dragDistance / 2) {
                settleToOpen = true;
            } else if (draggedX > -dragDistance / 2) {
                settleToOpen = false;
            }
            final int settleDestX = settleToOpen ? -dragDistance : 0;
            viewDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
            ViewCompat.postInvalidateOnAnimation(SlideMenuLayout.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (viewDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointX = event.getX();
                pointY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float dX = Math.abs(event.getX() - pointX);
                float dY = Math.abs(event.getY() - pointY);
                double distance = Math.sqrt(dX * dX + dY * dY);
                if (distance <= clickRange) {
                    if (listener != null) {
                        listener.onClick(contentView);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float mX = Math.abs(event.getX() - pointX);
                float mY = Math.abs(event.getY() - pointY);
                if (mX > mY && mX > dragRange) {
                    ViewParent viewParent = getParent();
                    while (viewParent != null) {
                        viewParent.requestDisallowInterceptTouchEvent(true);
                        viewParent = viewParent.getParent();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private View.OnClickListener listener;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
