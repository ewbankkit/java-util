/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents an interceptor replacement.
 */
public class InterceptorReplacement extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:55 InterceptorReplacement.java NSI";

    // Auto-generated. Regenerate if there are any relevant changes.
    private static final long serialVersionUID = -6090581809645467747L;

    private long limitId;
    private String limitType;
    private String originalRegex;
    private String originalText;
    private String prodInstId;
    private String replacementText;
    private String replacementType;
    private String vanityRegex;
    private Long vendorEntityId;
    private Integer vendorId;

    public void setLimitId(long limitId) {
        this.limitId = limitId;
    }

    public long getLimitId() {
        return limitId;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setOriginalRegex(String originalRegex) {
        this.originalRegex = originalRegex;
    }

    public String getOriginalRegex() {
        return originalRegex;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setReplacementText(String replacementText) {
        this.replacementText = replacementText;
    }

    public String getReplacementText() {
        return replacementText;
    }

    public void setReplacementType(String replacementType) {
        this.replacementType = replacementType;
    }

    public String getReplacementType() {
        return replacementType;
    }

    public void setVanityRegex(String vanityRegex) {
        this.vanityRegex = vanityRegex;
    }

    public String getVanityRegex() {
        return vanityRegex;
    }

    public void setVendorEntityId(Long vendorEntityId) {
        this.vendorEntityId = vendorEntityId;
    }

    public Long getVendorEntityId() {
        return vendorEntityId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getVendorId() {
        return vendorId;
    }
}
