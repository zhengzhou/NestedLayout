package com.zhou.nestedlayout;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

/**
 * 嵌套布局.
 * Created by zhou on 15-7-27.
 */
public final class NestedLayout extends FrameLayout implements NestedScrollingParent{

    private NestedScrollingParentHelper scrollingParentHelper;

    private View headView;

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
        headView = findViewById(R.id.header);
    }

    int getScrollDistance(){
        return headView.getHeight() - 185;
    }

    boolean isHeadScroll(int dy){
        return (dy > 0 && getScrollY() < getScrollDistance())
        ||(dy < 0 && getScrollY() > 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            if (NestedScrollingChild.class.isInstance(getChildAt(i))) {
                measureChild(getChildAt(i), widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(getMeasuredHeight()- getScrollDistance(), MeasureSpec.EXACTLY));
                break; // only contain one NestedScrollChild view.
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < getChildCount(); i++) {
            if (NestedScrollingChild.class.isInstance(getChildAt(i))) {
                getChildAt(i).layout(left, top + headView.getMeasuredHeight(), right,
                        top + getScrollDistance() + getHeight());
                break; // only contain one NestedScrollChild view.
            }
        }

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
        Logger.i("dx:%d, dy:%d, ScrollY:%d", dx, dy, getScrollY());
        if(isHeadScroll(dy)) {
            scrollBy(0, dy);
            consumed[1] += dy;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Logger.i("dxConsumed:%d, dyConsumed:%d, dxUnconsumed:%d, dyUnconsumed:%d",dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        //headView
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        /*if(isHeadScroll((int)velocityY)){
            if(velocityY > 0){
                scrollTo(0, getScrollDistance());
            }else{
                scrollTo(0, 0);
            }
            return true;
        }*/
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
        Logger.i("");
        scrollingParentHelper.onStopNestedScroll(child);
        if (getScrollY() > getScrollDistance()) {
            scrollTo(0, getScrollDistance());
        } else if (getScrollY() < 0) {
            scrollTo(0, 0);

        }
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return super.generateLayoutParams(attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return super.generateLayoutParams(p);
    }
}
