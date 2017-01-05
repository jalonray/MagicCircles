package pool;

import com.windywolf.jayray.magiccircle.CircleView;

import java.util.ArrayList;

/**
 * Created by JayRay on 04/01/2017.
 * Info:
 */

public abstract class AbstractPool<T> {
    protected final ArrayList<T> freeStack;

    public AbstractPool() {
        freeStack = new ArrayList<>();
    }

    public synchronized T getInstance() {
        if (freeStack.isEmpty()) {
            return newInstance();
        } else {
            return freeStack.remove(freeStack.size() - 1);
        }
    }

    public synchronized void freeInstance(T object) {
        if (!freeStack.contains(object)) {
            freeStack.add(object);
        }
    }

    abstract protected T newInstance();
}
