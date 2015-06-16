/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a product listing creation event.
 */
public class ProductListingCreationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:16 ProductListingCreationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProductListingCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setDescription(String description) {
        productLifeCycleEventData.setGenericString1(description);
    }

    public String getDescription() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setNotes(String notes) {
        productLifeCycleEventData.setGenericString2(notes);
    }

    public String getNotes() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setProductListingId(long productListingId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(productListingId));
    }

    public void setProductListingId(Long productListingId) {
        productLifeCycleEventData.setGenericNumber1(productListingId);
    }

    public Long getProductListingId() {
        return productLifeCycleEventData.getGenericNumber1();
    }

    public void setTitle(String title) {
        productLifeCycleEventData.setGenericString3(title);
    }

    public String getTitle() {
        return productLifeCycleEventData.getGenericString3();
    }
}
