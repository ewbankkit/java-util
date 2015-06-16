/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents a Interceptor lead search terms
 */
public class InterceptorLeadSearchTerms extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:54 InterceptorLeadSearchTerms.java NSI";

    // Auto-generated. Regenerate if there are any relevant changes.
    private static final long serialVersionUID = 256271890393641955L;

    private String prodInstId;
    private Integer searchEngineId;
    private String searchTerms;

    /**
     * Constructor.
     */
    public InterceptorLeadSearchTerms() {
        super();

        return;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return this.prodInstId;
    }

    public void setSearchEngineId(Integer searchEngineId) {
        this.searchEngineId = searchEngineId;
    }

    public Integer getSearchEngineId() {
        return this.searchEngineId;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSearchTerms() {
        return this.searchTerms;
    }
}
