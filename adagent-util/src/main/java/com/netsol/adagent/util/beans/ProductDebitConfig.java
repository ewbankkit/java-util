/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.codes.LeadType;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;

/**
 * Debit configuration for a product.
 *
 * @author Adam S. Vernon
 */
public class ProductDebitConfig extends BaseBudgetManagerData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:04 ProductDebitConfig.java NSI";

    private static final Log log = LogFactory.getLog(ProductDebitConfig.class);

    /** Do we debit this product for CPC markup? */
    private Boolean debitCpcMarkup = Boolean.FALSE;
    /** Do we debit this product for phone leads? */
    private Boolean debitPhoneLeadCost = Boolean.FALSE;
    /** Do we debit this product for email leads? */
    private Boolean debitEmailLeadCost = Boolean.FALSE;
    /** Do we debit this product for form leads? */
    private Boolean debitFormSubmitLeadCost = Boolean.FALSE;
    /** Do we debit this product for page leads? */
    private Boolean debitPageLoadLeadCost = Boolean.FALSE;
    /** Do we debit this product for cart leads? */
    private Boolean debitShoppingCartLeadCost = Boolean.FALSE;
    /** Do we debit this product for phone leads? */
    private Boolean debitUnansweredPhoneLeadCost = Boolean.FALSE;

    /**
     * Constructor. Initializes from the database.
     *
     * @param pdbConn
     * @param prodInstId
     */
    public ProductDebitConfig(Connection pdbConn, String prodInstId) throws Exception {
        super(log);
        init(pdbConn, prodInstId);
    }

    /**
     * @return the debitCpcMarkup
     */
    public Boolean getDebitCpcMarkup() {
        return debitCpcMarkup;
    }

    /**
     * @return the debitPhoneLeadCost
     */
    public Boolean getDebitPhoneLeadCost() {
        return debitPhoneLeadCost;
    }

    /**
     * @return the debitEmailLeadCost
     */
    public Boolean getDebitEmailLeadCost() {
        return debitEmailLeadCost;
    }

    /**
     * @return the debitFormSubmitLeadCost
     */
    public Boolean getDebitFormSubmitLeadCost() {
        return debitFormSubmitLeadCost;
    }

    /**
     * @return the debitPageLoadLeadCost
     */
    public Boolean getDebitPageLoadLeadCost() {
        return debitPageLoadLeadCost;
    }

    /**
     * @return the debitShoppingCartLeadCost
     */
    public Boolean getDebitShoppingCartLeadCost() {
        return debitShoppingCartLeadCost;
    }

    public Boolean getDebitUnansweredPhoneLeadCost() {
        return debitUnansweredPhoneLeadCost;
    }

    /**
     * Determine if a particular lead type cost is debited.
     *
     * @param leadTypeId
     * @return
     */
    public boolean isLeadTypeDebited(int leadTypeId) {
        boolean isDebited = false;
        switch (leadTypeId) {
        case LeadType.PHONE_LEAD: isDebited = debitPhoneLeadCost; break;
        case LeadType.EMAIL_LEAD: isDebited = debitEmailLeadCost; break;
        case LeadType.FORM_LEAD: isDebited = debitFormSubmitLeadCost; break;
        case LeadType.HIGH_VALUE_PAGE_LEAD: isDebited = debitPageLoadLeadCost; break;
        case LeadType.SHOPPING_CART_LEAD: isDebited = debitShoppingCartLeadCost; break;
        case LeadType.UNANSWERED_PHONE_LEAD: isDebited = debitUnansweredPhoneLeadCost; break;
        default: log.warn("Unknown lead type: " + Integer.toString(leadTypeId)); break;
        }
        return isDebited;
    }

    /**
     * Initialize the debit configuration from the database.
     *
     * @param pdbConn
     * @throws SQLException
     */
    private void init(Connection pdbConn, String prodInstId) throws SQLException {
        PpcProductDetail ppcProductDetail = new PpcProductDetailHelper(log).getPpcProductDetail(getLogTag(prodInstId), pdbConn, prodInstId);
        if (ppcProductDetail != null) {
            debitCpcMarkup = ppcProductDetail.isDebitCpcMarkup();
            debitEmailLeadCost = ppcProductDetail.isDebitEmailLeadCost();
            debitFormSubmitLeadCost = ppcProductDetail.isDebitFormLeadCost();
            debitPageLoadLeadCost = ppcProductDetail.isDebitHighValuePageLeadCost();
            debitPhoneLeadCost = ppcProductDetail.isDebitPhoneLeadCost();
            debitShoppingCartLeadCost = ppcProductDetail.isDebitShoppingCartLeadCost();
            debitUnansweredPhoneLeadCost = ppcProductDetail.isDebitUnansweredPhoneLeadCost();
            setProdInstId(ppcProductDetail.getProdInstId());
        }
    }

    //
    // Unit test
    //

    /**
     * Simple unit test.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        Connection pdbConn = null;
        try {
            // Get dev pdb2 connection.
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");
            System.out.println(new ProductDebitConfig(pdbConn, "WN-DIY-TEST4"));
        }
        catch(Throwable e) {
            e.printStackTrace(System.out);
        }
        finally {
            BaseHelper.close(pdbConn);
        }
    }
}
