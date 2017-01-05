package pool;

import android.content.Context;

import com.windywolf.jayray.magiccircle.CircleView;

import java.lang.ref.WeakReference;

/**
 * Created by JayRay on 04/01/2017.
 * Info: a pool to hold all circle view we need.
 */

public class CirclePool extends AbstractPool<CircleView> {
    private WeakReference<Context> contextWeakReference;

    public CirclePool(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public synchronized CircleView getInstance() {
        if (contextWeakReference.get() == null) {
            return null;
        }
        return super.getInstance();
    }

    @Override
    protected CircleView newInstance() {
        return new CircleView(contextWeakReference.get());
    }
}
