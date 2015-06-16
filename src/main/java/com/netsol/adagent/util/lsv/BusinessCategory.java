/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BusinessCategory implements Serializable {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:09 BusinessCategory.java NSI";

    private long categoryId;
    private List<BusinessCategory> childCategories;
    private String name;
    private Long parentCategoryId;
    private long vendorId;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public List<BusinessCategory> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<BusinessCategory> childCategories) {
        this.childCategories = childCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }
}
