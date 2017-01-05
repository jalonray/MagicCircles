package pool;

/**
 * Created by JayRay on 04/01/2017.
 * Info:
 */

public class BasicPool extends AbstractPool<Object> {

    private final Class type;

    public BasicPool(Class type) {
        super();
        this.type = type;
    }

    @Override
    protected Object newInstance() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
