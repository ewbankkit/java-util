/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Represents a search engine.
 */
public class SearchEngine extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:06 SearchEngine.java NSI";
    
    // Auto-generated. Regenerate if there are any relevant changes.
    private static final long serialVersionUID = 149527095958253256L;
    
    private String httpReferrerRegex;
    private Pattern httpReferrerRegexPattern;
    private int searchEngineId;
    private String searchEngineName;
    private int vendorId;
    
    /**
     * Constructor.
     */
    public SearchEngine() {
        super();
        
        return;
    }

    public void setHttpReferrerRegex(String httpReferrerRegex) {
        this.httpReferrerRegex = httpReferrerRegex;
        if (httpReferrerRegex != null) {
            this.httpReferrerRegexPattern = Pattern.compile(httpReferrerRegex);
        }
    }

    public String getHttpReferrerRegex() {
        return this.httpReferrerRegex;
    }

    @NotInToString
    public Pattern getHttpReferrerRegexPattern() {
        return this.httpReferrerRegexPattern;
    }

    public void setSearchEngineId(int searchEngineId) {
        this.searchEngineId = searchEngineId;
    }

    public int getSearchEngineId() {
        return this.searchEngineId;
    }

    public void setSearchEngineName(String searchEngineName) {
        this.searchEngineName = searchEngineName;
    }

    public String getSearchEngineName() {
        return this.searchEngineName;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return this.vendorId;
    }
}
