/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

// Microsoft campaign statuses.
public final class MicrosoftCampaignStatus extends MicrosoftEntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:29 MicrosoftCampaignStatus.java NSI";
    
    public final static String BUDGET_AND_MANUAL_PAUSED = "BudgetAndManualPaused";
    public final static String BUDGET_PAUSED = "BudgetPaused";
    
    private MicrosoftCampaignStatus() {
        super();
        
        return;
    }
}
