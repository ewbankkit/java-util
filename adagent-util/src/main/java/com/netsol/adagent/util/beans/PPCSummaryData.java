package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.log.BaseLoggable;

public abstract class PPCSummaryData extends BaseBudgetManagerData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:04 PPCSummaryData.java NSI";
	
	protected Date updateDate;
	
    /**
     * Constructor.
     */
	protected PPCSummaryData(String logComponent) {
        super(logComponent);
        
        return;
    }

    /**
     * Constructor. 
     */
    protected PPCSummaryData(Log logger) {
        super(logger);
        
        return;
    }
    
    /**
     * Constructor. 
     */
    protected PPCSummaryData(BaseLoggable baseLoggable) {
        super(baseLoggable);
        
        return;
    }
	
	public abstract void init(Connection conn, String prodInstId) throws BudgetManagerException;

	public abstract void persist() throws BudgetManagerException;

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Calendar updateDate) {
		this.updateDate = new Date(updateDate.getTimeInMillis());
	}
	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
