/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

public final class ProductLifeCycleEventType {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:16 ProductLifeCycleEventType.java NSI";

    // DB column maximum length is 50.
    //                                                            |--------------------------------------------------|
    public static final String AD_ACCOUNT_CREATION =            "Ad Account Creation";
    public static final String AD_CAMPAIGN_CREATION =           "Ad Campaign Creation";
    public static final String AD_CAMPAIGN_STATUS_CHANGE =      "Ad Campaign Status Change";
    public static final String AD_GROUP_SYNC =                  "Ad Group Sync";
    public static final String BUDGET_ADJUSTMENT =              "Budget Adjustment";
    public static final String BUDGET_RENEWAL =                 "Budget Renewal";
    public static final String CALL_TRACKING_ACCOUNT_CREATION = "Call Tracking Account Creation";
    public static final String CALL_TRACKING_ACCOUNT_DELETION = "Call Tracking Account Deletion";
    public static final String CAMPAIGN_SYNC =                  "Campaign Sync";
    public static final String PRODUCT_LISTING_CREATION =       "Product Listing Creation";
    public static final String PRODUCT_SYNC =                   "Product Sync";
    public static final String PROVISIONING_ACTIVATION =        "Provisioning Activation";
    public static final String PROVISIONING_ADDON =             "Provisioning Add On";
    public static final String PROVISIONING_CREATION =          "Provisioning Creation";
    public static final String PROVISIONING_DEACTIVATION =      "Provisioning Deactivation";
    public static final String PROVISIONING_DELETION =          "Provisioning Deletion";
    public static final String PROVISIONING_MODIFICATION =      "Provisioning Modification";
    public static final String PROVISIONING_REACTIVATION =      "Provisioning Reactivation";
    public static final String PROVISIONING_RENEWAL =           "Provisioning Renewal";
    public static final String PROVISIONING_UPGRADE =           "Provisioning Upgrade";
    public static final String TARGET_CREATION =                "Target Creation";
    public static final String TARGET_MODIFICATION =            "Target Modification";

    private ProductLifeCycleEventType() {}
}
