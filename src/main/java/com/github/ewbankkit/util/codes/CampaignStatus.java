/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

public final class CampaignStatus extends EntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:24 CampaignStatus.java NSI";

    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String ENDED = "ENDED";
    public static final String MANUAL_PAUSE = "MANUAL_PAUSE";
    public static final String PENDING = "PENDING";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String SYSTEM_PAUSE = "SYSTEM_PAUSE";


    public static boolean isPausedStatus(String status){
        return
            MANUAL_PAUSE.equals(status) ||
            SYSTEM_PAUSE.equals(status);
    }

    private CampaignStatus() {
        return;
    }
}
