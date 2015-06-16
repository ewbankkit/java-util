package com.netsol.adagent.util.beans;

import java.sql.Connection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Debitable item for generic click sources.
 * 
 * @author Adam S. Vernon
 */
public class GenericClick extends PPCAdClick {
    /**
     * Constructor.
     */
    public GenericClick(String logComponent) {
        super(logComponent);
        
        return;
    }

    /**
     * Constructor. 
     */
    public GenericClick(Log logger) {
        super(logger);
        
        return;
    }
    
    /**
     * Constructor. 
     */
    public GenericClick(BaseLoggable baseLoggable) {
        super(baseLoggable);
        
        return;
    }
    
	/**
	 * Override implementation in PPCAdClick to only check for nsCampaignId and nsAdGroupId.
	 */
	@Override
	public boolean hasAllIds() {
		if (this.getNsCampaignId() > 0L && this.getNsAdGroupId() > 0L) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Override implementation in PPCAdClick to calculate generic click costs differently.
	 */
	@Override
	public void calculateCosts(Connection conn, Product product) throws BudgetManagerException {
		// For now, all generic clicks have $0 cost. In the future, this could change.
		// Design could be to put the channel-specific generic_click_cost factor in budget_factors 
		// or put a generic_click_cost column in tier_config to have tiered pricing.
		this.setBaseCost(0);
		this.setMarkup(0);
	}

	/**
	 * Override implementation in PPCAdClick to use the generic click budget adjustment type.
	 */
	@Override
	public void insertBudgetAdjustment(Connection connection) {
		try {
			String prodInstId = this.getProdInstId();
			ProductSummaryData productSum = new ProductSummaryData(this);
			productSum.init(connection, prodInstId);
			
			CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(connection, prodInstId, this.getNsCampaignId());
			Campaign campaign = campaignList.getCampaigns().get(0);
			
			BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
			BudgetAdjustment budgetAdjustment = factory.getDebitGenericClickBudgetAdjustment(prodInstId, this.getDate(), 
					this.getSystem(), this.getHitId(), this.getDate(), this.getBaseCost(), this.getMarkup(), this.getNsCampaignId(), 
					this.getNsAdGroupId(), productSum.getMonthlyBudgetRemaining(), productSum.getDailyBudgetRemaining(), 
					campaign.getDailyBudget(), getVendorId());
			budgetAdjustment.insert(connection);
		} catch (Exception e) {
			// Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
			logError(logTag, e);
		}
	}	
	
	/**
	 * Override implementation in PPCAdClick.
	 */
	@Override
	public void debit(Connection conn) throws BudgetManagerException {
		if (!budgetManagerHelper.debitClickFromAdGroup(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting generic click from ad group: no rows updated!"); }
		if (!budgetManagerHelper.debitClickFromCampaign(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting generic click from campaign: no rows updated!"); }
		if (!budgetManagerHelper.debitClickFromProduct(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting generic click from product: no rows updated!"); }
	}
}
