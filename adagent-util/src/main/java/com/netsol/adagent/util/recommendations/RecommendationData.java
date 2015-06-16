/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.recommendations;

import java.sql.Connection;

import com.netsol.adagent.util.json.JsonUtil;

/**
 * Common base class for recommendation data classes.
 * 
 * @author Adam S. Vernon
 */
public abstract class RecommendationData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:23 RecommendationData.java NSI";
    
    /** Initilize the recommendation data. */
    public abstract void init(Connection pdbConn, Recommendation recommendation) throws Exception;
    
    /**
     * Convert the recommendation data to a JSON string.
     * @return
     */
    public String toJson() {
        return JsonUtil.toJson(this);
    }
}
