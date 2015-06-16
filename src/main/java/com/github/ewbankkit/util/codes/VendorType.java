/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.codes;

public final class VendorType {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:38 VendorType.java NSI";

    public static final int AD            = 1 << 0;
    public static final int LOCAL_SEARCH  = 1 << 1;
    public static final int CALL_TRACKING = 1 << 2;

    private VendorType() {
        return;
    }
}
