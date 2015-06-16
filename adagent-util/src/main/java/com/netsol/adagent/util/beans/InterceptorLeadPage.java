/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an Interceptor lead page.
 */
public class InterceptorLeadPage extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:54 InterceptorLeadPage.java NSI";

    private String description;
    private boolean formPage;
    private boolean highValuePage;
    private String path;
    private String prodInstId;
    private boolean shoppingCartPage;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setFormPage(boolean formPage) {
        this.formPage = formPage;
    }

    public boolean isFormPage() {
        return formPage;
    }

    public void setHighValuePage(boolean highValuePage) {
        this.highValuePage = highValuePage;
    }

    public boolean isHighValuePage() {
        return highValuePage;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setShoppingCartPage(boolean shoppingCartPage) {
        this.shoppingCartPage = shoppingCartPage;
    }

    public boolean isShoppingCartPage() {
        return shoppingCartPage;
    }
}
