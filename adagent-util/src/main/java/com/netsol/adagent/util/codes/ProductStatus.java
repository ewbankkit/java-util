/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

public final class ProductStatus extends StatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:32 ProductStatus.java NSI";

    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String IN_MIGRATION = "IN_MIGRATION";
    public static final String INACTIVE = "INACTIVE";
    public static final String PENDING_DELETE = "PENDING_DELETE";

    private ProductStatus() {
        return;
    }
}
