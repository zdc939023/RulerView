package com.zdc.rulerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * 自定义刻度尺View
 */
public class RulerView extends View {
    public static final int MOD_TYPE_HALF = 2;
    public static final int MOD_TYPE_ONE = 10;
    private static final int ITEM_HALF_DIVIDER = 10;
    private static final int ITEM_ONE_DIVIDER = 10;
    private static final int ITEM_MAX_HEIGHT = 20;
    private static final int ITEM_MIN_HEIGHT = 10;
    private static final int TEXT_SIZE = 12;
    private float mDensity;
    private int mValue = 50, mMaxValue = 100, mModType = MOD_TYPE_HALF,
            mLineDivider = ITEM_HALF_DIVIDER;
    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    public int getValue() {
        return mValue;
    }
    public void setValue(int value){
        mValue=value;
        notifyValueChange();
        postInvalidate();
    }

    public void setMaxValue(int maxValue){
        this.mMaxValue=maxValue;
    }

    public void setValueToChange(int what){
        mValue+=what;
        notifyValueChange();
        postInvalidate();
    }


    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth=getWidth();
        mHeight=getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }


    public void initViewParam(int defaultValue, int maxValue, int model) {
        switch (model) {
            case MOD_TYPE_HALF:
                mModType = MOD_TYPE_HALF;
                mLineDivider = ITEM_HALF_DIVIDER;
                mValue = defaultValue * 2;
                mMaxValue = maxValue * 2;
                break;
            case MOD_TYPE_ONE:
                mModType = MOD_TYPE_ONE;
                mLineDivider = ITEM_ONE_DIVIDER;
                mValue = defaultValue;
                mMaxValue = maxValue;
                break;

            default:
                break;
        }
        invalidate();

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }

    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.rgb(141, 189, 225));

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.rgb(68, 135, 188));
        textPaint.setTextSize(TEXT_SIZE * mDensity);

        int width = mWidth, drawCount = 0;
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

        for (int i = 0; drawCount <= 4 * width; i++) {
            int numSize = String.valueOf(mValue + i).length();
            // 前
            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth) {
                if ((mValue + i) % mModType == 0) {
                    linePaint.setColor(Color.rgb(68, 135, 188));
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition,
                            mDensity * ITEM_MAX_HEIGHT, linePaint);

                    if (mValue + i <= mMaxValue) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(
                                        String.valueOf((mValue + i) / 2),
                                        countLeftStart(mValue + i, xPosition,
                                                textWidth),
                                        getHeight() - textWidth, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(mValue + i),
                                        xPosition - (textWidth * numSize / 2),
                                        getHeight() - textWidth, textPaint);
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    linePaint.setColor(Color.rgb(141, 189, 225));
                    // linePaint.setColor(Color.rgb(68, 135, 188));
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition,
                            mDensity * ITEM_MIN_HEIGHT, linePaint);
                }
            }
            // 后
            xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
            if (xPosition > getPaddingLeft()) {
                if ((mValue - i) % mModType == 0) {
                    linePaint.setColor(Color.rgb(68, 135, 188));
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition,
                            mDensity * ITEM_MAX_HEIGHT, linePaint);

                    if (mValue - i >= 0) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(
                                        String.valueOf((mValue - i) / 2),
                                        countLeftStart(mValue - i, xPosition,
                                                textWidth),
                                        getHeight() - textWidth, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(mValue - i),
                                        xPosition - (textWidth * numSize / 2),
                                        getHeight() - textWidth, textPaint);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
                    linePaint.setColor(Color.rgb(141, 189, 225));
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition,
                            mDensity * ITEM_MIN_HEIGHT, linePaint);
                }
            }
            drawCount += 2 * mLineDivider * mDensity;
        }
        canvas.restore();
    }

    private float countLeftStart(int value, float xPosition, float textWidth) {
        float xp = 0f;
        if (value < 20) {
            xp = xPosition - (textWidth * 1 / 2);
        } else {
            xp = xPosition - (textWidth * 2 / 2);
        }
        return xp;
    }
    /**
     * 绘制中间红线
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas){
        int  indexWidth = 2;
        canvas.save();
        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(indexWidth);
        redPaint.setColor(Color.RED);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, redPaint);
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int xPositioin= (int) event.getX();
        if (mVelocityTracker==null)
            mVelocityTracker=VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX=xPositioin;
                mMove=0;
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                mMove+=(mLastX-xPositioin);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker(event);
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
                default:break;
        }
        mLastX=xPositioin;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        }
    }

    private void notifyValueChange() {
        if (null != mListener) {
            if (mModType == MOD_TYPE_ONE) {
                mListener.valueChange(mValue);
            }
            if (mModType == MOD_TYPE_HALF) {
                mListener.valueChange(mValue / 2f);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }


    private void countMoveEnd() {
        int roundMove = Math.round(mMove / (mLineDivider * mDensity));
        mValue = mValue + roundMove;
        mValue = mValue <= 0 ? 0 : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }


    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (mLineDivider * mDensity));
        if (Math.abs(tValue) > 0) {
            mValue += tValue;
            mMove -= tValue * mLineDivider * mDensity;
            if (mValue <= 0 || mValue > mMaxValue) {
                mValue = mValue <= 0 ? 0 : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    private onValueChangeListener mListener;
    /**
     * 值变化的回调监听
     */
    public interface onValueChangeListener{
        void valueChange(float value);
    }
    public void setOnValueChangeListener(onValueChangeListener listener){
        this.mListener=listener;
    }
}
