/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a product listing.
 */
public class ProductListing extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:05 ProductListing.java NSI";

    private String crmId;
    private String description;
    private long fulfillmentId;
    private String notes;
    private String prodInstId;
    private long productListingId;
    private String title;
    private String updatedBySystem;
    private String updatedByUser;

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setFulfillmentId(long fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public long getFulfillmentId() {
        return fulfillmentId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setProductListingId(long productListingId) {
        this.productListingId = productListingId;
    }

    public long getProductListingId() {
        return productListingId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }
}
