package com.moxun.flowlayoutlib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowLayout component.
 * Created by moxun on 15/9/7.
 */
public class FlowLayout extends ViewGroup implements FlowItemAdapter.FlowLayoutDataSetListener {

    private int dirtyMark;
    private boolean isDirty;
    private List<List<Integer>> dirtySets = new ArrayList<>();
    private OnClickListener onClickListener;
    private FlowItemAdapter adapter;
    private Animation insertAnim, removeAnim;
    private OnItemClickListener itemClickListener;
    private static final int DEFAULT_ANIM_DURATION = 300;
    public static final AnimationSet DEFAULT_INSERT_ANIMATION, DEFAULT_REMOVE_ANIMATION;

    static {
        DEFAULT_INSERT_ANIMATION = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        DEFAULT_INSERT_ANIMATION.addAnimation(alphaAnimation);
        DEFAULT_INSERT_ANIMATION.addAnimation(scaleAnimation);
        DEFAULT_INSERT_ANIMATION.setDuration(DEFAULT_ANIM_DURATION);

        DEFAULT_REMOVE_ANIMATION = new AnimationSet(true);
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(1, 0);
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        DEFAULT_REMOVE_ANIMATION.addAnimation(alphaAnimation1);
        DEFAULT_REMOVE_ANIMATION.addAnimation(scaleAnimation1);
        DEFAULT_REMOVE_ANIMATION.setDuration(DEFAULT_ANIM_DURATION);
    }

    public FlowLayout(Context context) {
        super(context);
        init();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(FlowLayout.this, v, indexOfChild(v), v.getId());
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        itemClickListener = onItemClickListener;
    }

    public void setAdapter(FlowItemAdapter adapter) {
        this.adapter = adapter;
        adapter.setDataSetListener(this);
        initChildFromAdapter();
    }

    public void setItemInsertAnimation(Animation insertAnim) {
        this.insertAnim = insertAnim;
    }

    public void setItemRemoveAnimation(Animation removeAnim) {
        this.removeAnim = removeAnim;
    }

    public interface OnItemClickListener {
        void onItemClick(ViewGroup parent, View view, int position, long id);
    }

    private void initChildFromAdapter() {
        removeAllViews();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            super.addView(adapter.getView(getContext(), this, i));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (itemClickListener != null) {
                child.setOnClickListener(onClickListener);
            }
            if (child.getVisibility() != GONE) {
                Location location = (Location) child.getTag();
                child.layout(location.left, location.top, location.right, location.bottom);
            }
        }
        if (isDirty) {
            startAnim();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int topOffset = getPaddingTop();
        int leftOffset = getPaddingLeft();

        int selfWidth = 0, selfHeight = 0;
        int currentLineWidth = 0, currentLineHeight = 0;

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            Margin margin;
            if (child.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                margin = new Margin(layoutParams);
            } else {
                margin = new Margin();
            }

            int childWidth = Math.max(child.getMeasuredWidth(), getSuggestedMinimumWidth()) + margin.left + margin.right;
            int childHeight = Math.max(child.getMeasuredHeight(), getSuggestedMinimumHeight()) + margin.top + margin.bottom;

            if (currentLineWidth + childWidth > contentWidth - getPaddingLeft() - getPaddingRight()) {
                //需要另起一行
                currentLineWidth = Math.max(currentLineWidth, childWidth);
                selfWidth = Math.max(selfWidth, currentLineWidth);
                currentLineWidth = childWidth;
                selfHeight += currentLineHeight;
                currentLineHeight = childHeight;
                Log.d("new line", "child at" + i + ", max line width " + selfWidth + ", content width" + contentWidth);

                child.setTag(new Location(child, leftOffset, selfHeight + topOffset, child.getMeasuredWidth() + getPaddingLeft(), selfHeight + child.getMeasuredHeight() + topOffset));
            } else {
                //不需要换行
                child.setTag(new Location(child, currentLineWidth + leftOffset, selfHeight + topOffset, currentLineWidth + child.getMeasuredWidth() + topOffset, selfHeight + child.getMeasuredHeight() + topOffset));
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(currentLineHeight, childHeight);
                Log.d("update line width", "child at" + i + ", line width " + currentLineWidth);
            }

            if (i == childCount - 1) {
                selfWidth = Math.max(currentLineWidth, selfWidth) + getPaddingRight() + getPaddingLeft();
                selfHeight += currentLineHeight + getPaddingTop() + getPaddingBottom();
                Log.d("finally", "line width " + selfWidth);
            }

        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? contentWidth : selfWidth,
                heightMode == MeasureSpec.EXACTLY ? contentHeight : selfHeight);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (insertAnim != null) {
            child.startAnimation(insertAnim);
        }
    }

    @Override
    public void addView(View child, int index) {
        if (index == -1) {
            super.addView(child, index);
            return;
        }
        dirtyMark = index + 1;
        isDirty = true;
        markDirty(index - 1);
        super.addView(child, index);
        if (insertAnim != null) {
            child.startAnimation(insertAnim);
        }
    }

    @Override
    public void removeViewAt(int index) {
        dirtyMark = index;
        isDirty = true;
        if (removeAnim != null) {
            getChildAt(index).startAnimation(removeAnim);
        }
        markDirty(index);
        super.removeViewAt(index);
    }

    private void markDirty(int index) {
        dirtySets.clear();
        for (int i = index + 1; i < getChildCount(); i++) {
            ArrayList<Integer> point = new ArrayList<>();
            point.add(getChildAt(i).getLeft());
            point.add(getChildAt(i).getTop());
            dirtySets.add(point);
        }
        Log.d("dirty set", dirtySets.toString());
    }

    private void startAnim() {
        for (int i = dirtyMark; i < getChildCount(); i++) {
            int fromXDelta = dirtySets.get(i - dirtyMark).get(0) - getChildAt(i).getLeft();
            int fromYDelta = dirtySets.get(i - dirtyMark).get(1) - getChildAt(i).getTop();
            if (fromXDelta != 0 || fromYDelta != 0) {
                TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, 0, fromYDelta, 0);
                translateAnimation.setDuration(DEFAULT_ANIM_DURATION);
                Log.d("child " + i, "from:" + dirtySets.get(i - dirtyMark).get(0) + "," + dirtySets.get(i - dirtyMark).get(1) +
                        " translate to:" + getChildAt(i).getLeft() + "," + getChildAt(i).getTop());
                getChildAt(i).startAnimation(translateAnimation);
            }
        }
        isDirty = false;
    }

    @Override
    public void removeView(View view) {
        //super.removeView()内部实现逻辑和super.removeViewAt()一致
        removeViewAt(indexOfChild(view));
    }

    public void setOnItemClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    @Override
    public void onItemInserted(int position) {
        View item = adapter.getView(getContext(), this, position);
        addView(item, position);
    }

    @Override
    public void onItemRemoved(int position) {
        removeViewAt(position);
    }

    @Override
    public void onItemChanged(int position) {
        super.removeViewAt(position);
        View item = adapter.getView(getContext(), this, position);
        super.addView(item, position);
    }

    @Override
    public void onDataSetChanged() {
        initChildFromAdapter();
    }

    private class Margin {
        public int left, top, right, bottom;

        public Margin() {

        }

        public Margin(MarginLayoutParams lp) {
            left = lp.leftMargin;
            right = lp.rightMargin;
            top = lp.topMargin;
            bottom = lp.bottomMargin;
        }
    }

    private class Location {
        public int left, top, right, bottom;

        public Location(View view, int left, int top, int right, int bottom) {
            Margin margin;
            if (view.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                margin = new Margin(layoutParams);
            } else {
                margin = new Margin();
            }
            this.left = left + margin.left;
            this.top = top + margin.top;
            this.right = right + margin.left;
            this.bottom = bottom + margin.top;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Location{");
            sb.append("bottom=").append(bottom);
            sb.append(", left=").append(left);
            sb.append(", top=").append(top);
            sb.append(", right=").append(right);
            sb.append('}');
            return sb.toString();
        }
    }
}
