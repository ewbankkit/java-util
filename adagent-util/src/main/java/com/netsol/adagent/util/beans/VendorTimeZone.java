/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;;

public class VendorTimeZone extends BaseData {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:12 VendorTimeZone.java NSI";

    private String javaName;
    private String name;
    private int vendorId;
    private long vendorTimeZoneId;

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorTimeZoneId(long vendorTimeZoneId) {
        this.vendorTimeZoneId = vendorTimeZoneId;
    }

    public long getVendorTimeZoneId() {
        return vendorTimeZoneId;
    }
}
