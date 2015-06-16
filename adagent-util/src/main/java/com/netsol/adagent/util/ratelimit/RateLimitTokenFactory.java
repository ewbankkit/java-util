/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ratelimit;

import org.apache.commons.pool.BasePoolableObjectFactory;

/* package-private */ class RateLimitTokenFactory extends BasePoolableObjectFactory {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:22 RateLimitTokenFactory.java NSI";
    
    /**
     * Constructor.
     */
    public RateLimitTokenFactory() {
        super();
        
        return;
    }

    /**
     * Return an instance that can be served by the pool.
     */
    @Override
    public Object makeObject() throws Exception {
        return new Object();
    }
}
