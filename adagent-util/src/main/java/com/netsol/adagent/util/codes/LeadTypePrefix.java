/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

public enum LeadTypePrefix {
    PHONE_LEAD(LeadType.PHONE_LEAD, "phone_lead_"),
    FORM_LEAD(LeadType.FORM_LEAD, "form_submit_lead_"),
    EMAIL_LEAD(LeadType.EMAIL_LEAD, "email_lead_"),
    HIGH_VALUE_PAGE_LEAD(LeadType.HIGH_VALUE_PAGE_LEAD, "page_load_lead_"),
    SHOPPING_CART_LEAD(LeadType.SHOPPING_CART_LEAD, "shop_cart_lead_"),
    UNANSWERED_PHONE_LEAD(LeadType.UNANSWERED_PHONE_LEAD, "unanswered_phone_lead_");

    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:28 LeadTypePrefix.java NSI";

    private int id;
    private String prefix;

    private LeadTypePrefix(int id, String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public int getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }
}
