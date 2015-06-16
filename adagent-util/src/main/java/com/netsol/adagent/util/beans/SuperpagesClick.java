package com.netsol.adagent.util.beans;

import java.sql.Connection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Superpages click debitable item.
 * 
 * @author Adam S. Vernon
 */
public class SuperpagesClick extends PPCAdClick {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:08 SuperpagesClick.java NSI";
	
    /**
     * Constructor.
     */
	public SuperpagesClick(String logComponent) {
        super(logComponent);
        return;
    }

    /**
     * Constructor. 
     */
    public SuperpagesClick(Log logger) {
        super(logger);
        return;
    }
    
    /**
     * Constructor. 
     */
    public SuperpagesClick(BaseLoggable baseLoggable) {
        super(baseLoggable);
        return;
    }
	
	@Override
	public boolean validate(Connection conn) throws BudgetManagerException {
		if (this.getNsCampaignId() <= 0L) {
			logInfo(logTag, "debitClick: Debit click was invoked with a zero-value entity ID... exiting...");
			return false;
		}
		return true;
	}
	
	@Override
	public void calculateCosts(Connection pdbConn, Product product) throws BudgetManagerException {

		double cpc = budgetManagerHelper.getSuperpagesCostForClick(pdbConn, this);
		logInfo(logTag, "debitClick: CPC: "+cpc);
		this.setBaseCost(cpc);
		
		// Get the PpcProductDetail.
		String prodInstId = product.getProdInstId();
		PpcProductDetail ppcProductDetail = null;
		try {
			ppcProductDetail = new PpcProductDetailHelper(this).getPpcProductDetail(getLogTag(prodInstId), pdbConn, prodInstId);
		}
		catch(Exception e) {
			this.logError(logTag, e);
			throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Error initializing ppcProductDetail", e.getMessage(), e);
		}
		
		// Calculate the markup.
		double markup = 0D;
		if (ppcProductDetail.isDebitCpcMarkup()) {
			markup = cpc * ppcProductDetail.getCpcMarkup();			
		}
		logInfo(logTag, "debitClick -> Markup: " + markup);
		this.setMarkup(markup);
	}
	
	@Override
	public void debit(Connection conn) throws BudgetManagerException {
	    if (!budgetManagerHelper.debitClickFromAdGroup(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from campaign: no rows updated!"); }
	    if (!budgetManagerHelper.debitClickFromCampaign(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from campaign: no rows updated!"); }
		if (!budgetManagerHelper.debitClickFromProduct(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from product: no rows updated!"); }
	}
	
	@Override
	public void insertBudgetAdjustment(Connection connection) {
		try {
			String prodInstId = this.getProdInstId();
			ProductSummaryData productSum = new ProductSummaryData(this);
			productSum.init(connection, prodInstId);
			
			CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(connection, prodInstId, this.getNsCampaignId());
			Campaign campaign = campaignList.getCampaigns().get(0);
			
			BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
			BudgetAdjustment budgetAdjustment = factory.getDebitSuperpagesClickBudgetAdjustment(prodInstId, this.getDate(), 
					this.getSystem(), this.getHitId(), this.getDate(), this.getBaseCost(), this.getMarkup(), this.getNsCampaignId(), 
					productSum.getMonthlyBudgetRemaining(),	productSum.getDailyBudgetRemaining(), campaign.getDailyBudget(), getVendorId());
			budgetAdjustment.insert(connection);
		} catch (Exception e) {
			// Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
			logError(logTag, e);
		}
	}
}
