package com.zjkd.HealthyHouse.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by liang on 15-10-14.
 */
public class VerticalRulerView extends View {

    public interface OnValueChangeListener {
        public void onValueChange(float value);
    }

    public static final int MOD_TYPE_HALF = 2;
	public static final int MOD_TYPE_ONE = 10;
	private static final int ITEM_HALF_DIVIDER = 10;
	private static final int ITEM_ONE_DIVIDER = 10;
	private static final int ITEM_MAX_HEIGHT = 30;
	private static final int ITEM_MIN_HEIGHT = 15;
	private static final int TEXT_SIZE = 7;

    private float mDensity;
    private int mValue = 50, mMaxValue = 100, mModType = MOD_TYPE_HALF, mLineDivider = ITEM_HALF_DIVIDER;
    // private int mValue = 50, mMaxValue = 500, mModType = MOD_TYPE_ONE,
    // mLineDivider = ITEM_ONE_DIVIDER;

    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    @SuppressWarnings("deprecation")
    public VerticalRulerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;

        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

    }

    public VerticalRulerView(Context context) {
        this(context, null);

    }

    /**
     *
     * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
     *
     * @param defaultValue
     *            初始值
     * @param maxValue
     *            最大值
     * @param model
     *            刻度盘精度：<br>
     *            {@link MOD_TYPE_HALF}<br>
     *            {@link MOD_TYPE_ONE}<br>
     */
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

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }
    public void setValue(int value){
		mValue =  value;
		postInvalidate();
	}
    public void setValueToChange(int what) {
		mValue += what;
		notifyValueChange();
		postInvalidate();
	}
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleLine(canvas);
        // drawWheel(canvas);
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
        linePaint.setColor(Color.BLACK);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.rgb(68, 135, 188));
        textPaint.setTextSize(TEXT_SIZE * mDensity);

        int height = mHeight, drawCount = 0;
        float yPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint),textHeight = TEXT_SIZE * mDensity;

        for (int i = 0; drawCount <= 4 * height; i++) {
            int numSize = String.valueOf(mValue + i).length();

            yPosition = (height / 2 - mMove) + i * mLineDivider * mDensity;
            if (yPosition + getPaddingBottom() < mHeight) {
                if ((mValue + i) % mModType == 0) {
					linePaint.setColor(Color.rgb(68, 135, 188));
                    canvas.drawLine(0, yPosition, mDensity * ITEM_MAX_HEIGHT, yPosition, linePaint);

                    if (mValue + i <= mMaxValue) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(String.valueOf((mValue + i) / 2), mWidth-textWidth*String.valueOf((mValue + i) / 2).length(), yPosition+textHeight/2, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(mValue + i), mWidth-textWidth*String.valueOf(mValue + i).length(), yPosition+textHeight/2, textPaint);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
					linePaint.setColor(Color.rgb(141, 189, 225));
                    canvas.drawLine(0, yPosition, mDensity * ITEM_MIN_HEIGHT, yPosition, linePaint);
                }
            }

            yPosition = (height / 2 - mMove) - i * mLineDivider * mDensity;
            if (yPosition > getPaddingTop()) {
                if ((mValue - i) % mModType == 0) {
					linePaint.setColor(Color.rgb(68, 135, 188));
                    canvas.drawLine(0, yPosition, mDensity * ITEM_MAX_HEIGHT, yPosition, linePaint);

                    if (mValue - i >= 0) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(String.valueOf((mValue - i) / 2),mWidth-textWidth*String.valueOf((mValue - i) / 2).length(), yPosition+textHeight/2, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(mValue - i),mWidth-textWidth*String.valueOf(mValue - i).length(), yPosition+textHeight/2, textPaint);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
					linePaint.setColor(Color.rgb(141, 189, 225));
                    canvas.drawLine(0, yPosition, mDensity * ITEM_MIN_HEIGHT, yPosition, linePaint);
                }
            }

            drawCount += 2 * mLineDivider * mDensity;
        }

        canvas.restore();
    }

    /**
     * 计算没有数字显示位置的辅助方法
     *
     * @param value
     * @param xPosition
     * @param textWidth
     * @return
     */
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
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        // TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
    	int gap = 12, indexWidth = 2, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
        String color = "#66999999";

        canvas.save();

        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(indexWidth);
        redPaint.setColor(Color.RED);
        canvas.drawLine(0, mHeight/2, mWidth, mHeight/2, redPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int yPosition = (int) event.getY();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mScroller.forceFinished(true);

                mLastX = yPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
            	getParent().requestDisallowInterceptTouchEvent(true);
                mMove += (mLastX - yPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            	getParent().requestDisallowInterceptTouchEvent(false);
                countMoveEnd();
                countVelocityTracker(event);
                return false;
            // break;
            default:
                break;
        }

        mLastX = yPosition;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float yVelocity = mVelocityTracker.getYVelocity();
        if (Math.abs(yVelocity) > mMinVelocity) {
            mScroller.fling(0, 0,  0,(int) yVelocity, 0, 0,Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
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

    private void notifyValueChange() {
        if (null != mListener) {
            if (mModType == MOD_TYPE_ONE) {
                mListener.onValueChange(mValue);
            }
            if (mModType == MOD_TYPE_HALF) {
                mListener.onValueChange(mValue / 2f);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrY() == mScroller.getFinalY()) { // over
                countMoveEnd();
            } else {
                int yPosition = mScroller.getCurrY();
                mMove += (mLastX - yPosition);
                changeMoveAndValue();
                mLastX = yPosition;
            }
        }
    }
    @Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(event);
	}
}