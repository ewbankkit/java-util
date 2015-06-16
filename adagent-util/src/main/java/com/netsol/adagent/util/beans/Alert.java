/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

public class Alert extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:49 Alert.java NSI";

    // Priority.
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MED = 2;
    public static final int PRIORITY_LOW = 3;
    public static final int PRIORITY_NONE = 4;

    // Alert type.
    public static final String BUDGET_ALERT = "budget";
    public static final String GENERIC_ALERT = "generic";
    public static final String MESSAGE_ALERT = "message";
    public static final String PERFORMANCE_ALERT = "performance";
    public static final String LANDING_PAGE_ALERT = "landing_page";

    // Status.
    public static final String STATUS_DEFERRED = "deferred";
    public static final String STATUS_DELETED = "deleted";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_UNREAD = "unread";

    // Entity type.
    public static final String ENTITY_AD = "ad";
    public static final String ENTITY_AD_GROUP = "ad group";
    public static final String ENTITY_CAMPAIGN = "campaign";
    public static final String ENTITY_KEYWORD = "keyword";
    public static final String ENTITY_PRODUCT = "product";

    private String details;
    private long entityId = -1L;
    private String entityType = ENTITY_PRODUCT;
    private String origin;
    private int priority = -1;
    private String prodInstId;
    private String status = STATUS_UNREAD;
    private String type = GENERIC_ALERT;

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static enum AlertType {
        MESSAGE(Alert.MESSAGE_ALERT),
        BUDGET(Alert.BUDGET_ALERT),
        PERFORMANCE(Alert.PERFORMANCE_ALERT),
        GENERIC(Alert.GENERIC_ALERT),
        LANDING_PAGE(Alert.LANDING_PAGE_ALERT);

        public final String value;

        AlertType(String value) {
            this.value = value;
        }
    }
}
