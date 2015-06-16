/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

public final class VendorId {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:36 VendorId.java NSI";

    public static final int GOOGLE = 1;
    public static final int YAHOO = 2;
    public static final int MICROSOFT = 3;
    public static final int THINKLOCAL = 4;
    public static final int LOCALEZE = 5;
    public static final int CALLSOURCE = 6;
    public static final int TELMETRICS = 7;
    public static final int GENERIC_CLICK_SOURCE = 8;
    public static final int SUPERPAGES = 9;

    private VendorId() {}

    public static int[] getActivePPCVendorIds() {
    	return new int[] { GOOGLE, MICROSOFT, SUPERPAGES };
    }
}
