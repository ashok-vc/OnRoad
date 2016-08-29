package onroad.travel.com.onroad.java;

/**
 * Created by cbhpl on 29/8/16.
 */
public class CommonMethods {

    public static String createSlug(long id)
    {
        return "onRoad"+(id % 100000);
    }
}
