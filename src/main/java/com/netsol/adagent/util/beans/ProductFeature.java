/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Collection;

/**
 * Represents a product feature.
 */
public class ProductFeature extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:05 ProductFeature.java NSI";

    private int featureId;
    private String prodInstId;
    private String status;

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Return whether or not the specified feature exists.
     */
    public static boolean featureExists(Collection<ProductFeature> productFeatures, int feature) {
        if (collectionIsEmpty(productFeatures)) {
            return false;
        }
        for (ProductFeature productFeature : productFeatures) {
            if (productFeature.getFeatureId() == feature) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether or not the specified feature has the required status.
     */
    public static boolean featureHasStatus(Collection<ProductFeature> productFeatures, int feature, String status) {
        if (collectionIsEmpty(productFeatures)) {
            return false;
        }
        for (ProductFeature productFeature : productFeatures) {
            if (productFeature.getFeatureId() == feature) {
                return stringsEqual(productFeature.getStatus(), status);
            }
        }
        return false;
    }
}
