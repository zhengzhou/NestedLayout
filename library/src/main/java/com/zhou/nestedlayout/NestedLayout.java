package com.zhou.nestedlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zaynzhou.library.R;


/**
 * 嵌套布局.
 * Created by zhou on 15-7-27.
 */
public final class NestedLayout extends FrameLayout implements NestedScrollingParent{

    private NestedScrollingParentHelper scrollingParentHelper;

    private View headView;
    private NestedScrollingChild body;

    public NestedLayout(Context context) {
        super(context);
        init();
    }

    public NestedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        scrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
            if(layoutParams.viewType == LayoutParams.HEAD){
                headView = v;
            }else if(NestedScrollingChild.class.isInstance(getChildAt(i))
                    && layoutParams.viewType == LayoutParams.BODY){
                body = (NestedScrollingChild) v;
            }
        }
        if(body == null){
            throw new IllegalStateException("nestScroll child not found!");
        }
        //headView = findViewById(R.id.header);
    }

    int getScrollDistance(){
        if(headView != null)
            return headView.getHeight() - headView.getMinimumHeight();
        else
            return 0;
    }

    /**
     * parent 还可以滑动
     * @param dy
     * @return
     */
    boolean isHeadScroll(int dy){
        return (dy > 0 && getScrollY() < getScrollDistance())
        ||(dy < 0 && getScrollY() > 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChild((View) body, widthMeasureSpec,
               MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getScrollDistance(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ((View) body).layout(left, top + headView.getMeasuredHeight(), right,
                top + getScrollDistance() + getHeight());

    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        scrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //是垂直的滚动
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if(isHeadScroll(dy)) {
//            Log.d("tag", getScrollY() + ", " + dy);
//            Log.d("tag", getScrollDistance() +"");

            //处理上下过界限的问题。
            if(dy + getScrollY() > getScrollDistance()){
                dy = getScrollDistance() - getScrollY();
            }else if(dy + getScrollY() <0){
                dy = -getScrollY();
            }
            scrollBy(0, dy);
            consumed[1] += dy;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //headView
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
        scrollingParentHelper.onStopNestedScroll(child);
        int snap = 40;
        if (getScrollY() + snap > getScrollDistance()) {
            scrollTo(0, getScrollDistance());
        } else if (getScrollY() - snap < 0) {
            scrollTo(0, 0);
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public static final int HEAD = 1;
        public static final int BODY = 2;
        public static final int FOOT = 3;

        int viewType = BODY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.NestedLayout_LayoutParams);
            viewType = a.getInt(R.styleable.NestedLayout_LayoutParams_nest_viewType, BODY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams p) {
            super(p);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

    }
}
