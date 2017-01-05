package com.windywolf.jayray.magiccircle;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by JayRay on 03/01/2017.
 * Info: 顺时针绘制圆环的动画
 */

public class CircleAngleAnimation extends Animation {

    private CircleView mCircle;
    private float oldAngle;
    private float newAngle;

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        mCircle.setAngle(angle);
        mCircle.invalidate();
    }

    public void setCircle(CircleView circleView) {
        this.oldAngle = 1f;
        this.newAngle = circleView.getAngle();
        this.mCircle = circleView;
    }
}
