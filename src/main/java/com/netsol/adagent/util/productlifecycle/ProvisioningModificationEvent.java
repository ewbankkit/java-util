/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a provisioning product modify event.
 */
public class ProvisioningModificationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:18 ProvisioningModificationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningModificationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNewAccountRep(String newAccountRep) {
        productLifeCycleEventData.setGenericString6(newAccountRep);
    }

    public String getNewAccountRep() {
        return productLifeCycleEventData.getGenericString6();
    }

    public void setOldAccountRep(String oldAccountRep) {
        productLifeCycleEventData.setGenericString5(oldAccountRep);
    }

    public String getOldAccountRep() {
        return productLifeCycleEventData.getGenericString5();
    }

    public void setNewProductId(long newProductId) {
        productLifeCycleEventData.setGenericNumber2(Long.valueOf(newProductId));
    }

    public void setNewProductId(Long newProductId) {
        productLifeCycleEventData.setGenericNumber2(newProductId);
    }

    public Long getNewProductId() {
        return productLifeCycleEventData.getGenericNumber2();
    }

    public void setOldProductId(long oldProductId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(oldProductId));
    }

    public void setOldProductId(Long oldProductId) {
        productLifeCycleEventData.setGenericNumber1(oldProductId);
    }

    public Long getOldProductId() {
        return productLifeCycleEventData.getGenericNumber1();
    }

    public void setNewSalesRep(String newSalesRep) {
        productLifeCycleEventData.setGenericString8(newSalesRep);
    }

    public String getNewSalesRep() {
        return productLifeCycleEventData.getGenericString8();
    }

    public void setOldSalesRep(String oldSalesRep) {
        productLifeCycleEventData.setGenericString7(oldSalesRep);
    }

    public String getOldSalesRep() {
        return productLifeCycleEventData.getGenericString7();
    }

    public void setNewSpecialist(String newSpecialist) {
        productLifeCycleEventData.setGenericString2(newSpecialist);
    }

    public String getNewSpecialist() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setOldSpecialist(String oldSpecialist) {
        productLifeCycleEventData.setGenericString1(oldSpecialist);
    }

    public String getOldSpecialist() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setNewTrackingType(String newTrackingType) {
        productLifeCycleEventData.setGenericString10(newTrackingType);
    }

    public String getNewTrackingType() {
        return productLifeCycleEventData.getGenericString10();
    }

    public void setOldTrackingType(String oldTrackingType) {
        productLifeCycleEventData.setGenericString9(oldTrackingType);
    }

    public String getOldTrackingType() {
        return productLifeCycleEventData.getGenericString9();
    }

    public void setNewUrl(String newUrl) {
        productLifeCycleEventData.setGenericString4(newUrl);
    }

    public String getNewUrl() {
        return productLifeCycleEventData.getGenericString4();
    }

    public void setOldUrl(String oldUrl) {
        productLifeCycleEventData.setGenericString3(oldUrl);
    }

    public String getOldUrl() {
        return productLifeCycleEventData.getGenericString3();
    }
}
