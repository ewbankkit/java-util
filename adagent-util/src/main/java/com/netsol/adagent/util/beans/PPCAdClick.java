package com.netsol.adagent.util.beans;

import java.sql.Connection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class PPCAdClick extends PPCDebitableItem {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:03 PPCAdClick.java NSI";
	
	private double baseCost;
	private double markup;
	private int vendorId;
	
    /**
     * Constructor.
     */
	public PPCAdClick(String logComponent) {
        super(logComponent);
        
        return;
    }

    /**
     * Constructor. 
     */
    public PPCAdClick(Log logger) {
        super(logger);

        return;
    }
    
    /**
     * Constructor. 
     */
    public PPCAdClick(BaseLoggable baseLoggable) {
        super(baseLoggable);
        
        return;
    }
	
	public double getMarkup() {
		return markup;
	}

	public void setMarkup(double markup) {
		this.markup = markup;
	}

	public void setBaseCost(double baseCost) {
		this.baseCost = baseCost;
	}

	public double getBaseCost() {
		return baseCost;
	}
	
	public int getVendorId() { return vendorId; }
	public void setVendorId(int vendorId) { this.vendorId = vendorId; } 
	
	public boolean hasAllIds() {
		if (this.getNsCampaignId() > 0L && this.getNsAdGroupId() > 0L && this.getNsKeywordId() > 0L && this.getNsAdId() > 0L) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean validate(Connection conn) throws BudgetManagerException {
		// first, check the IDs
		if (!this.hasAllIds()) {
			logInfo(logTag, "debitClick -> Debit click was invoked with a zero-value entity ID... exiting...");
			return false;
		}
		return true;
	}
	
	@Override
	public void calculateCosts(Connection pdbConn, Product product) throws BudgetManagerException {
		// get the keyword's most recent avg cpc, or the bid if no recent values
		double cpc = budgetManagerHelper.getCostForClick(pdbConn, this);
		if (cpc <= 0.0) {
			cpc = budgetManagerHelper.getGlobalAverageCPC(pdbConn, product.getChannelId());
		}
		logInfo(logTag, "debitClick -> CPC: "+cpc);
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
		if (!budgetManagerHelper.debitClickFromAdGroup(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from ad group: no rows updated!"); }
		if (!budgetManagerHelper.debitClickFromKeyword(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from keyword: no rows updated!"); }
		if (!budgetManagerHelper.debitClickFromAd(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from ad: no rows updated!"); }
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
			BudgetAdjustment budgetAdjustment = factory.getDebitClickBudgetAdjustment(prodInstId, this.getDate(), 
					this.getSystem(), this.getHitId(), this.getDate(), this.getBaseCost(), this.getMarkup(), this.getNsCampaignId(), 
					this.getNsAdGroupId(), this.getNsAdId(), this.getNsKeywordId(), productSum.getMonthlyBudgetRemaining(), 
					productSum.getDailyBudgetRemaining(), campaign.getDailyBudget(), getVendorId());
			budgetAdjustment.insert(connection);
		} catch (Exception e) {
			// Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
			logError(logTag, e);
		}
	}
	
	@Override
	public double getFullCost() {
		return baseCost+markup;
	}
}
