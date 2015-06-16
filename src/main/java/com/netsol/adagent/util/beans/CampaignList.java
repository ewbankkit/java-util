/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class CampaignList extends BaseBudgetManagerData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:52 CampaignList.java NSI";

    private final List<Campaign> campaigns = new ArrayList<Campaign>();

    /**
     * Constructor.
     */
    public CampaignList(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public CampaignList(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public boolean isEmpty() {
        return campaigns.isEmpty();
    }

    public void persist(Connection conn, String updatedBySystem) throws BudgetManagerException {
        if (isEmpty()) {
            return;
        }
        
        PreparedStatement pstmt = null;

        try {
            for (Campaign campaign : campaigns) {
            	campaign.persist(conn, updatedBySystem);
            }
        } 
        catch (BudgetManagerException e) {
            logError(logTag, e);
            throw e;
        }
        finally {
            BaseHelper.close(pstmt);
        }
    }
}
