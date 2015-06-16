/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import com.netsol.adagent.util.beans.BaseData;

/**
 * Local search phrase. 
 */
/* package-private */ class LocalSearchPhrase extends SearchEnginePhrase {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:20 LocalSearchPhrase.java NSI";
    
    private String location;
    
    /**
     * Constructor.
     */
    public LocalSearchPhrase() {
        return;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalSearchPhrase)) {
            return false;
        }
        LocalSearchPhrase rhs = (LocalSearchPhrase)o;
        return SearchEnginePhrase.searchPhrasesEqual(this,rhs) && SearchEnginePhrase.urlsEqual(this, rhs) && LocalSearchPhrase.locationsEqual(this, rhs);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        if (this.location != null) {
            result = 31 * result + this.location.toLowerCase().hashCode();
        }
        
        return result;
    }
    
    /**
     * Are the locations equal? 
     */
    protected static boolean locationsEqual(LocalSearchPhrase lsp1, LocalSearchPhrase lsp2) {
        return BaseData.stringsEqualIgnoreCase(lsp1.location, lsp2.location);
    }
}
