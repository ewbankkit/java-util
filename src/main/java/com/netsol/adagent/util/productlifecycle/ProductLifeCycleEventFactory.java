/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

public final class ProductLifeCycleEventFactory {
    public static AdAccountCreationEvent createAdAccountCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.AD_ACCOUNT_CREATION);
        return createAdAccountCreationEvent(productLifeCycleEventData);
    }

    public static AdCampaignCreationEvent createAdCampaignCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.AD_CAMPAIGN_CREATION);
        return createAdCampaignCreationEvent(productLifeCycleEventData);
    }

    public static AdCampaignStatusChangeEvent createAdCampaignStatusChangeEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.AD_CAMPAIGN_STATUS_CHANGE);
        return createAdCampaignStatusChangeEvent(productLifeCycleEventData);
    }

    public static AdGroupSyncEvent createAdGroupSyncEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.AD_GROUP_SYNC);
        return createAdGroupSyncEvent(productLifeCycleEventData);
    }

    public static BudgetAdjustmentEvent createBudgetAdjustmentEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.BUDGET_ADJUSTMENT);
        return createBudgetAdjustmentEvent(productLifeCycleEventData);
    }

    public static BudgetRenewalEvent createBudgetRenewalEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.BUDGET_RENEWAL);
        return createBudgetRenewalEvent(productLifeCycleEventData);
    }

    public static CallTrackingAccountCreationEvent createCallTrackingAccountCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.CALL_TRACKING_ACCOUNT_CREATION);
        return createCallTrackingAccountCreationEvent(productLifeCycleEventData);
    }

    public static CallTrackingAccountDeletionEvent createCallTrackingAccountDeletionEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.CALL_TRACKING_ACCOUNT_DELETION);
        return createCallTrackingAccountDeletionEvent(productLifeCycleEventData);
    }

    public static CampaignSyncEvent createCampaignSyncEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.CAMPAIGN_SYNC);
        return createCampaignSyncEvent(productLifeCycleEventData);
    }

    public static ProductListingCreationEvent createProductListingCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PRODUCT_LISTING_CREATION);
        return createProductListingCreationEvent(productLifeCycleEventData);
    }

    public static ProductSyncEvent createProductSyncEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PRODUCT_SYNC);
        return createProductSyncEvent(productLifeCycleEventData);
    }

    public static ProvisioningActivationEvent createProvisioningActivationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_ACTIVATION);
        return createProvisioningActivationEvent(productLifeCycleEventData);
    }

    public static ProvisioningAddOnEvent createProvisioningAddOnEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_ADDON);
        return createProvisioningAddOnEvent(productLifeCycleEventData);
    }

    public static ProvisioningCreationEvent createProvisioningCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_CREATION);
        return createProvisioningCreationEvent(productLifeCycleEventData);
    }

    public static ProvisioningDeactivationEvent createProvisioningDeactivationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_DEACTIVATION);
        return createProvisioningDeactivationEvent(productLifeCycleEventData);
    }

    public static ProvisioningDeletionEvent createProvisioningDeletionEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_DELETION);
        return createProvisioningDeletionEvent(productLifeCycleEventData);
    }

    public static ProvisioningModificationEvent createProvisioningModificationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_MODIFICATION);
        return createProvisioningModificationEvent(productLifeCycleEventData);
    }

    public static ProvisioningReactivationEvent createProvisioningReactivationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_REACTIVATION);
        return createProvisioningReactivationEvent(productLifeCycleEventData);
    }

    public static ProvisioningRenewalEvent createProvisioningRenewalEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_RENEWAL);
        return createProvisioningRenewalEvent(productLifeCycleEventData);
    }

    public static ProvisioningUpgradeEvent createProvisioningUpgradeEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.PROVISIONING_UPGRADE);
        return createProvisioningUpgradeEvent(productLifeCycleEventData);
    }

    public static TargetCreationEvent createTargetCreationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.TARGET_CREATION);
        return createTargetCreationEvent(productLifeCycleEventData);
    }

    public static TargetModificationEvent createTargetModificationEvent() {
        ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
        productLifeCycleEventData.setEventType(ProductLifeCycleEventType.TARGET_MODIFICATION);
        return createTargetModificationEvent(productLifeCycleEventData);
    }

    /* package-private */ static AdAccountCreationEvent createAdAccountCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new AdAccountCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static AdCampaignCreationEvent createAdCampaignCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new AdCampaignCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static AdCampaignStatusChangeEvent createAdCampaignStatusChangeEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new AdCampaignStatusChangeEvent(productLifeCycleEventData);
    }

    /* package-private */ static AdGroupSyncEvent createAdGroupSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new AdGroupSyncEvent(productLifeCycleEventData);
    }

    /* package-private */ static BudgetAdjustmentEvent createBudgetAdjustmentEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new BudgetAdjustmentEvent(productLifeCycleEventData);
    }

    /* package-private */ static BudgetRenewalEvent createBudgetRenewalEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new BudgetRenewalEvent(productLifeCycleEventData);
    }

    /* package-private */ static CallTrackingAccountCreationEvent createCallTrackingAccountCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new CallTrackingAccountCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static CallTrackingAccountDeletionEvent createCallTrackingAccountDeletionEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new CallTrackingAccountDeletionEvent(productLifeCycleEventData);
    }

    /* package-private */ static CampaignSyncEvent createCampaignSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new CampaignSyncEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProductListingCreationEvent createProductListingCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProductListingCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProductSyncEvent createProductSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProductSyncEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningActivationEvent createProvisioningActivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningActivationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningAddOnEvent createProvisioningAddOnEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningAddOnEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningCreationEvent createProvisioningCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningDeactivationEvent createProvisioningDeactivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningDeactivationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningDeletionEvent createProvisioningDeletionEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningDeletionEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningModificationEvent createProvisioningModificationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningModificationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningReactivationEvent createProvisioningReactivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningReactivationEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningRenewalEvent createProvisioningRenewalEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningRenewalEvent(productLifeCycleEventData);
    }

    /* package-private */ static ProvisioningUpgradeEvent createProvisioningUpgradeEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new ProvisioningUpgradeEvent(productLifeCycleEventData);
    }

    /* package-private */ static TargetCreationEvent createTargetCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new TargetCreationEvent(productLifeCycleEventData);
    }

    /* package-private */ static TargetModificationEvent createTargetModificationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            return null;
        }
        return new TargetModificationEvent(productLifeCycleEventData);
    }

    private ProductLifeCycleEventFactory() {}
}
