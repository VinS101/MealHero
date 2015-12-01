package dltone.com.mealhero;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wgarcia on 11/30/2015.
 */
public class UniqueID
{
    private final static AtomicInteger ai = new AtomicInteger(11);
    public static int getID()
    {
        return ai.incrementAndGet();
    }
}
