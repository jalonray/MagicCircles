package com.windywolf.jayray.magiccircle;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.Random;

import pool.CirclePool;

import static com.windywolf.jayray.magiccircle.MainActivity.AddCircleHandler.ADD_VIEW;
import static com.windywolf.jayray.magiccircle.MainActivity.AddCircleHandler.REMOVE_VIEW;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final float PI = 3.14159f;
    public RelativeLayout layout;
    private static final float speed = 420;
    public Random random = new Random();
    public AddCircleHandler handler;
    private Thread thread;
    private CircleGenerator generator = null;
    private View mDecorView;
    private boolean hide = true;

    private CirclePool circlePool;
    private ReverseInterpolator interpolator;

    private boolean dark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (RelativeLayout) findViewById(R.id.activity_main);
        handler = new AddCircleHandler(this);
        generator = new CircleGenerator(this);
        mDecorView = getWindow().getDecorView();
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (hide) {
                    showSystemUI();
                } else {
                    hideSystemUI();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("∂");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                hideSystemUI();
                return true;
            }
        });
        item = menu.add("D&B");
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dark = !dark;
                getWindow().setBackgroundDrawable(new ColorDrawable(dark ? Color.BLACK : Color.WHITE));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        generator.setLoop(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        generator.setLoop(true);
        startGenerator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generator.setLoop(false);
    }

    private void startGenerator() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (thread == null) {
                    thread = new Thread(generator);
                }
                thread.start();
                hideSystemUI();
            }
        }, 420);
    }

    public void addCircle(final CircleView circleView) {
        Log.d(TAG, "addCircle: " + circleView.toString());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) circleView.getLayoutParams();
        if (params == null) {
            params = generateParams(circleView);
            circleView.setLayoutParams(params);
        }
        layout.addView(circleView);
        Log.d(TAG, "param width: " + params.width);
        float radius = params.width / 2f;
        Log.d(TAG, "radius: " + radius);
        long duration = (long) (radius * 2 * PI / speed * 1000);
        final AnimationSet set = new AnimationSet(false);
        CircleAngleAnimation angleAnimation = new CircleAngleAnimation();
        angleAnimation.setCircle(circleView);
        angleAnimation.setDuration(duration * 2);
        angleAnimation.setRepeatCount(Animation.INFINITE);
        if (interpolator == null) {
            interpolator = new ReverseInterpolator();
        }
        angleAnimation.setInterpolator(interpolator);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                radius, radius);
        rotateAnimation.setDuration(duration / 3);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        set.addAnimation(angleAnimation);
        set.addAnimation(rotateAnimation);
        set.setRepeatCount(Animation.INFINITE);
        circleView.startAnimation(set);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCircle((CircleView) v);
            }
        });
    }

    public CircleView generateCircle() {
        if (circlePool == null) {
            circlePool = new CirclePool(this);
        }
        return circlePool.getInstance();
    }

    public RelativeLayout.LayoutParams generateParams(CircleView circleView) {
        Log.d(TAG, "generateParams: " + circleView.toString());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) circleView.getLayoutParams();
        int width = layout.getWidth() - dp2px(42);
        int height = layout.getHeight() - dp2px(42);
        float radius = random.nextFloat() * (Math.min(width, height) / 2 - dp2px(42)) + dp2px(42);
        float x = random.nextFloat() * (width - radius * 2) + dp2px(21);
        float y = random.nextFloat() * (height - radius * 2) + dp2px(21);
        Log.d(TAG, "width: " + radius * 2);
        Log.d(TAG, "radius: " + radius);
        Log.d(TAG, "x: " + x);
        Log.d(TAG, "y: " + y);
        if (params == null) {
            params = new RelativeLayout.LayoutParams((int) (radius * 2), (int) (radius * 2));
        } else {
            params.width = (int) (radius * 2);
            params.height = (int) (radius * 2);
        }
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        return params;
    }

    public void removeCircle(CircleView circleView) {
        if (circleView == null) {
            return;
        }
        if (circleView.getTag() != null && circleView.getTag().equals(true)) {
            return;
        }
        Log.e(TAG, "removeCircle: " + circleView.toString());
        circleView.setTag(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f,
                circleView.getRadius(), circleView.getRadius());
        scaleAnimation.setDuration(420);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.25f);
        scaleAnimation.setDuration(420);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        RemoveAnimationListener listener = new RemoveAnimationListener();
        listener.setCircle(circleView);
        listener.setContext(this);
        set.setAnimationListener(listener);
        circleView.startAnimation(set);
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        hide = true;
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        hide = false;
    }

    private static class CircleGenerator implements Runnable {

        private static final String TAG = "CircleGenerator";
        private WeakReference<MainActivity> activityWeakReference = null;
        int width = -1;
        int height = -1;
        boolean loop = true;
        int ultimate = 42;
        int count = 0;

        public CircleGenerator(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Log.d(TAG, "run");
            if (activityWeakReference.get() == null) {
                return;
            }
            MainActivity activity = activityWeakReference.get();
            if (width <= 0 || height <= 0) {
                width = activity.layout.getWidth();
                height = activity.layout.getHeight();
            }
            Random random = activity.random;

            CircleView circleView = activity.generateCircle();
            RelativeLayout.LayoutParams params = activity.generateParams(circleView);
            float radius = params.width / 2;
            float stroke = random.nextFloat() * (radius / 2f - 8.4f) + 8.4f;
            int color = Color.argb(random.nextInt(42) + 213, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            circleView.setStroke(stroke);
            circleView.setColor(color);
            if (circleView.getLayoutParams() == null) {
                circleView.setLayoutParams(params);
            }

            Message msg = activity.handler.obtainMessage();
            msg.what = ADD_VIEW;
            msg.obj = circleView;
            activity.handler.sendMessage(msg);
            count++;
            if (loop && count < ultimate) {
                long sleepTime = (long) (random.nextFloat() * 1580f) + 420;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    run();
                }
            } else {
                loop = false;
            }
        }

        public synchronized void setLoop(boolean loop) {
            Log.d(TAG, "setLoop: " + loop);
            this.loop = loop;
        }

        public synchronized void remove() {
            Log.d(TAG, "remove");
            if (activityWeakReference.get() == null) {
                return;
            }
            MainActivity activity = activityWeakReference.get();
            count--;
            if (count < 0) {
                count = 0;
            }
            if (!loop) {
                activity.startGenerator();
            }
        }
    }

    public static class AddCircleHandler extends Handler {
        private static final String TAG = "AddCircleHandler";
        public static final int ADD_VIEW = 1;
        public static final int REMOVE_VIEW = 2;
        private WeakReference<MainActivity> activityWeakReference = null;

        public AddCircleHandler(MainActivity mainActivity) {
            activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activityWeakReference.get() == null || !(msg.obj instanceof CircleView)) {
                return;
            }
            MainActivity activity = activityWeakReference.get();
            switch (msg.what) {
                case ADD_VIEW:
                    Log.d(TAG, "add view: " + msg.obj.toString());
                    activity.addCircle((CircleView) msg.obj);
                    break;
                case REMOVE_VIEW:
                    Log.d(TAG, "remove view: " + msg.obj.toString());
                    CircleView circleView = (CircleView) msg.obj;
                    Log.e(TAG, "freeCircle: " + circleView.toString());
                    activity.layout.removeView(circleView);
                    resetCircle(circleView);
                    activity.circlePool.freeInstance(circleView);
                    activity.generator.remove();
                    break;
                default:
                    break;
            }
        }

        private void resetCircle(CircleView view) {
            view.setVisibility(View.VISIBLE);
            view.setTag(false);
            view.setAngle(360);
        }
    }

    private static class ReverseInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            if (input <= 0.5) {
                return input * 2;
            } else {
                return 2 - input * 2;
            }
        }
    }

    public static class RemoveAnimationListener implements Animation.AnimationListener {
        private CircleView circleView = null;
        private WeakReference<MainActivity> contextWeakReference = null;

        public void setCircle(CircleView circle) {
            this.circleView = circle;
        }

        public void setContext(MainActivity activity) {
            if (contextWeakReference == null) {
                contextWeakReference = new WeakReference<>(activity);
            }
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            final MainActivity activity = contextWeakReference.get();
            if (activity == null) {
                return;
            }
            if (circleView != null) {
                Log.e(TAG, "onAnimationEnd: " + circleView.toString());
                Message msg = activity.handler.obtainMessage();
                msg.what = REMOVE_VIEW;
                msg.obj = circleView;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleView.setVisibility(View.GONE);
                    }
                });
                activity.handler.sendMessageDelayed(msg, 42);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    public int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(dpValue, TypedValue.COMPLEX_UNIT_DIP,
                getResources().getDisplayMetrics());
    }
}
