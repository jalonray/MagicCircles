package com.windywolf.jayray.magiccircle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by JayRay on 03/01/2017.
 * Info: draw a circle
 */

public class CircleView extends View {
    private static final String TAG = "CircleView";

    protected static final int START_ANGLE_POINT = -90;
    protected float stroke = 40;
    protected Paint mPaint;
    protected RectF mRect;
    protected float mAngle = 360;
    protected int mColor = Color.BLACK;
    protected float mRadius;
    protected float centerX;
    protected float centerY;
    protected boolean init = false;

    public CircleView(Context context) {
        super(context);
        init(null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.circleView);
            stroke = a.getDimensionPixelOffset(R.styleable.circleView_stroke, 40);
            mAngle = a.getFloat(R.styleable.circleView_angle, 360f);
            mColor = a.getColor(R.styleable.circleView_strokeColor, Color.BLACK);
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(stroke);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColor);
        init = true;
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        Log.d(TAG, "onLayout");
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
//        Log.d(TAG, "params width: " + params.width);
//        Log.d(TAG, "params height: " + params.height);
//        Log.d(TAG, "layout width: " + (right - left));
//        Log.d(TAG, "layout height: " + (bottom - top));
//        if (changed) {
//            float width = right - left - getPaddingLeft() - getPaddingRight();
//            float height = bottom - top - getPaddingTop() - getPaddingBottom();
//            mRadius = (Math.min(width, height) - stroke) / 2;
//            centerX = width / 2 + getPaddingLeft();
//            centerY = height / 2 + getPaddingTop();
//            Log.d(TAG, "width: " + width);
//            Log.d(TAG, "height: " + height);
//            Log.d(TAG, "radius: " + mRadius);
//            Log.d(TAG, "centerX: " + centerX);
//            Log.d(TAG, "centerY: " + centerY);
//            if (mRect == null) {
//                mRect = new RectF(centerX - mRadius, centerY - mRadius,
//                        centerX + mRadius, centerY + mRadius);
//            } else {
//                mRect.left = centerX - mRadius;
//                mRect.top = centerY - mRadius;
//                mRect.right = centerX + mRadius;
//                mRect.bottom = centerY + mRadius;
//            }
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        float width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        float height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        mRadius = (Math.min(width, height) - stroke) / 2;
        centerX = width / 2 + getPaddingLeft();
        centerY = height / 2 + getPaddingTop();
        Log.d(TAG, "width: " + width);
        Log.d(TAG, "height: " + height);
        Log.d(TAG, "radius: " + mRadius);
        Log.d(TAG, "centerX: " + centerX);
        Log.d(TAG, "centerY: " + centerY);
        if (mRect == null) {
            mRect = new RectF(centerX - mRadius, centerY - mRadius,
                    centerX + mRadius, centerY + mRadius);
        } else {
            mRect.left = centerX - mRadius;
            mRect.top = centerY - mRadius;
            mRect.right = centerX + mRadius;
            mRect.bottom = centerY + mRadius;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRect, START_ANGLE_POINT, mAngle, false, mPaint);
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        this.mAngle = angle;
        if (init) {
            postInvalidate();
        }
    }

    public float getRadius() {
        return mRadius;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
        if (init) {
            mPaint.setStrokeWidth(stroke);
            postInvalidate();
        }
    }

    public void setColor(int color) {
        this.mColor = color;
        if (init) {
            mPaint.setColor(color);
            postInvalidate();
        }
    }
}
