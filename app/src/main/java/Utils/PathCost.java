package Utils;

import java.lang.reflect.Array;
import java.security.Policy;
import java.util.ArrayList;

/**
 * Created by rupesh on 15/08/15.
 */
public class PathCost {
    long cost;
    ArrayList<Pixels> pixelPath;
    PathCost(long cost, ArrayList<Pixels> pixelPath){
        this.cost = cost;
        this.pixelPath = pixelPath;
    }

}
