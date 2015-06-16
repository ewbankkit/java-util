/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ratelimit;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.pool.impl.GenericObjectPool;

import com.netsol.adagent.util.log.BaseLoggable;

// A rate-limiting pool.
public class RateLimitingObjectPool extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:22 RateLimitingObjectPool.java NSI";
    
    /**
     * How many objects to pass out per second.
     */
    private final int maxPerSecond;
    
    /**
     * The number of objects we have provided during the current second
     * will be reset when an object is requested in a new second after the 
     * current transactionSecond.
     */
    private int numProvidedThisSecond;
    
    /**
     * We must contain this object rather than extend it because
     * we need to have direct control over the object level lock so that
     * multiple threads can safely lock this object.
     */
    private final GenericObjectPool pool;
    
    /**
     * The current second we are tracking open transactions for in milliseconds floored to the second (last 3 numbers are always 0).
     */
    private long transactionSecond = -1L;
    
    /**
     * Constructor. 
     */
    public RateLimitingObjectPool(String logComponent, int maxActive, long maxWaitMillis, int maxPerSecond) {
        super(logComponent);
        
        this.maxPerSecond = maxPerSecond;
        this.pool = new GenericObjectPool(new RateLimitTokenFactory(), maxActive, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, maxWaitMillis);
        
        return;
    }

    /**
     * Borrow an object from the pool.
     * An object will only be return if we have not already passed out maxPerSecond objects
     * during the current transactionSecond.
     */
    public synchronized Object borrowObject(String logTag) throws RateLimitException {
        long currentSecond = RateLimitingObjectPool.getCurrentTimeNoMillis();
        if (this.transactionSecond != currentSecond) {
            // We are in a new second.
            this.transactionSecond = currentSecond;
            this.numProvidedThisSecond = 0;
        }  

        if (this.numProvidedThisSecond >= this.maxPerSecond) {
            // We have already provided our max number of objects in this second
            // so we must sleep until the next second.
            // Since Thread.sleep() is not always going to sleep for the provided 
            // amount of time, we have to do this within a while loop until
            // we reach the new second.
            long newCurrentSecond = RateLimitingObjectPool.getCurrentTimeNoMillis();
            while (this.transactionSecond == newCurrentSecond) {
                long millisToNextSecond = RateLimitingObjectPool.getMillisToNextSecond();
                // we need to sleep until the next second and try again
                try {
                    Thread.sleep(millisToNextSecond);
                }
                catch (Exception e) {
                    this.logWarning(logTag, e);
                    // Ignore.
                }
                newCurrentSecond = RateLimitingObjectPool.getCurrentTimeNoMillis();
            }

            this.transactionSecond = newCurrentSecond;
            this.numProvidedThisSecond = 0; // Set it to 0 now, will increment after getting from the pool.
        } 

        Object borrowedObject;
        try {
            borrowedObject = this.pool.borrowObject();
        }
        catch (Exception e) {
            this.logError(logTag, e);
            throw new RateLimitException("Please retry request later", e);
        } 
        if (borrowedObject == null) {
            throw new RateLimitException("Unexpected error in rate limit logic, object was null");
        }
        
        this.numProvidedThisSecond++;

        return borrowedObject;
    }

    /**
     * Return an object to the pool.
     */
    public void returnObject(String logTag, Object object) throws RateLimitException {
        try {
            this.pool.returnObject(object);
        }
        catch (Exception e) {
            throw new RateLimitException("Unexpected error returning object to pool", e);
        }
        
        return;
    }
    
    /**
     * Close the pool.
     */
    public void close() {
        try {
            this.pool.close();
        }
        catch (Exception e) {
            this.logWarning(null, e);
            // Ignore.
        }
        
        return;
    }

    /**
     * Calculates the amount of time between now and the start of the next second
     * and returns that value so we can sleep for that amount of time.
     */
    private static long getMillisToNextSecond() {
        Calendar futureSecondCal = GregorianCalendar.getInstance(); 
        long nowMillis = futureSecondCal.getTimeInMillis();
        futureSecondCal.add(Calendar.SECOND, 1);
        futureSecondCal.set(Calendar.MILLISECOND, 0);
        long futureMillis = futureSecondCal.getTimeInMillis();
        long sleepTime = (futureMillis - nowMillis) ;
        return sleepTime;  
    }

    /**
     * Returns the current time as a long but sets the milliseconds to 0.
     * For example, instead of 1205438397512 it returns 1205438397000
     * which allows us to use the long value to represent a distinct second
     * in time without caring about what millisecond it is.
     */
    private static long getCurrentTimeNoMillis() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
