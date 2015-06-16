/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

// Google campaign statuses.
public final class GoogleCampaignStatus extends GoogleEntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:26 GoogleCampaignStatus.java NSI";

    public final static String ACTIVE = "Active";
    public final static String ENDED = "Ended";
    public final static String PENDING = "Pending";
    public final static String SUSPENDED = "Suspended";

    private GoogleCampaignStatus() {
        super();

        return;
    }
}
