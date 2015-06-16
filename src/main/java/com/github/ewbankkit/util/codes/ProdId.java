    /**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.codes;

import com.github.ewbankkit.util.beans.BaseData;

import static com.github.ewbankkit.util.beans.BaseData.arrayIsEmpty;

// EDB Product ID constants for marketing products.
public final class ProdId {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:32 ProdId.java NSI";

    public final static long FREE_BIZ_PROFILE = 7100637L;
    public final static long LSV = 7100555L;
    public final static long DIFM_PPC = 7100219L;
    public final static long DIY_PPC = 7100964L;
    public final static long SEO_FOR_ECOMMERCE = 7100867L;
    public final static long SUPERSTATS = 2000206L;
    public final static long WEBSTATS = 7100919L;
    public final static long PCT = 7100835L;
    public final static long LEADS_BY_WEB = 7101001L;
    public final static long SMARTCALLS = 7101024L;

    // These Product ID's are made up (i.e. not an EDB product ID).
    public final static long DNI = 7777777L;
    public final static long SEO_BASIC = 7777778L;

    public final static long[] PPC_PROD_IDS = {DIFM_PPC, DIY_PPC};
    public final static long[] ADAGENT_PROD_IDS = {DIFM_PPC, SEO_FOR_ECOMMERCE, PCT, LEADS_BY_WEB, SMARTCALLS};

    private ProdId() {}

    /**
     * Return whether or not the specified product ID is for a DNI product.
     */
    public static boolean isDniProduct(long prodId) {
        return isProduct(prodId, DNI);
    }

    /**
     * Return whether or not the specified product ID is for a DNI product.
     */
    public static boolean isDniProduct(Long prodId) {
        return isProduct(prodId, DNI);
    }

    /**
     * Return whether or not the specified product ID is for an "AMP" (non-PPC) product.
     */
    public static boolean isAmpProduct(long prodId) {
        return isLsvProduct(prodId) || isFreeBizProfileProduct(prodId) || isS4EProduct(prodId) || isProduct(prodId, SEO_BASIC);
    }

    /**
     * Return whether or not the specified product ID is for an "AMP" (non-PPC) product.
     */
    public static boolean isAmpProduct(Long prodId) {
        return isLsvProduct(prodId) || isFreeBizProfileProduct(prodId) || isS4EProduct(prodId) || isProduct(prodId, SEO_BASIC);
    }

    /**
     * Return whether or not the product can be accessed from the AdAgent UI.
     */
    public static boolean isAdagentProduct(Long prodId){
        return isProduct(prodId, ADAGENT_PROD_IDS);
    }

    /**
     * Return whether or not the specified product ID is for a PPC product (DIFM or DIY).
     */
    public static boolean isPpcProduct(long prodId) {
        return isProduct(prodId, PPC_PROD_IDS);
    }

    /**
     * Return whether or not the specified product ID is for a PPC product (DIFM or DIY).
     */
    public static boolean isPpcProduct(Long prodId) {
        return isProduct(prodId, PPC_PROD_IDS);
    }

    /**
     * Return whether or not the specified product ID is for a DIFM PPC product.
     */
    public static boolean isDifmPpcProduct(long prodId) {
        return isProduct(prodId, DIFM_PPC);
    }

    /**
     * Return whether or not the specified product ID is for a DIFM PPC product.
     */
    public static boolean isDifmPpcProduct(Long prodId) {
        return isProduct(prodId, DIFM_PPC);
    }

    /**
     * Return whether or not the specified product ID is for a DIY PPC product.
     */
    public static boolean isDiyPpcProduct(long prodId) {
        return isProduct(prodId, DIY_PPC);
    }

    /**
     * Return whether or not the specified product ID is for a DIY PPC product.
     */
    public static boolean isDiyPpcProduct(Long prodId) {
        return isProduct(prodId, DIY_PPC);
    }

    /**
     * Return whether or not the specified product ID is for a Leads By Web product.
     */
    public static boolean isLeadsByWebProduct(long prodId) {
        return isProduct(prodId, LEADS_BY_WEB);
    }

    /**
     * Return whether or not the specified product ID is for a Leads By Web product.
     */
    public static boolean isLeadsByWebProduct(Long prodId) {
        return isProduct(prodId, LEADS_BY_WEB);
    }

    /**
     * Return whether or not the specified product ID is for an SEO for e-commerce product.
     */
    public static boolean isS4EProduct(long prodId) {
        return isProduct(prodId, SEO_FOR_ECOMMERCE);
    }

    /**
     * Return whether or not the specified product ID is for an SEO for e-commerce product.
     */
    public static boolean isS4EProduct(Long prodId) {
        return isProduct(prodId, SEO_FOR_ECOMMERCE);
    }

    /**
     * Return whether or not the specified product ID is for a SmartCalls product.
     */
    public static boolean isSmartCallsProduct(long prodId) {
        return isProduct(prodId, SMARTCALLS);
    }

    /**
     * Return whether or not the specified product ID is for a SmartCalls product.
     */
    public static boolean isSmartCallsProduct(Long prodId) {
        return isProduct(prodId, SMARTCALLS);
    }

    /**
     * Return whether or not the specified product ID is for an nsWebStats product.
     */
    public static boolean isWebstatsProduct(long prodId) {
        return isProduct(prodId, WEBSTATS);
    }

    /**
     * Return whether or not the specified product ID is for an nsWebStats product.
     */
    public static boolean isWebstatsProduct(Long prodId) {
        return isProduct(prodId, WEBSTATS);
    }

    /**
     * Return whether or not the specified product ID is for a PCT product.
     */
    public static boolean isPCTProduct(long prodId) {
        return isProduct(prodId, PCT);
    }

    /**
     * Return whether or not the specified product ID is for a PCT product.
     */
    public static boolean isPCTProduct(Long prodId) {
        return isProduct(prodId, PCT);
    }

    /**
     * Return whether or not the specified product ID is for an LSV product.
     */
    public static boolean isLsvProduct(long prodId) {
        return isProduct(prodId, LSV);
    }

    /**
     * Return whether or not the specified product ID is for an LSV product.
     */
    public static boolean isLsvProduct(Long prodId) {
        return isProduct(prodId, LSV);
    }

    /**
     * Return whether or not the specified product ID is for a Free Business Profile product.
     */
    public static boolean isFreeBizProfileProduct(Long prodId) {
        return isProduct(prodId, FREE_BIZ_PROFILE);
    }

    /**
     * Return whether or not the specified product ID is for a Free Business Profile product.
     */
    public static boolean isFreeBizProfileProduct(long prodId) {
        return isProduct(prodId, FREE_BIZ_PROFILE);
    }

    private static boolean isProduct(long prodId, long productProdId) {
        return prodId == productProdId;
    }

    private static boolean isProduct(Long prodId, long productProdId) {
        if (prodId == null) {
            return false;
        }
        return isProduct(prodId.longValue(), productProdId);
    }

    private static boolean isProduct(long prodId, long[] productProdIds) {
        if (BaseData.arrayIsEmpty(productProdIds)) {
            return false;
        }
        for (int i = 0; i < productProdIds.length; i++) {
            if (prodId == productProdIds[i]) {
                return true;
            }
        }
        return false;
    }

    private static boolean isProduct(Long prodId, long[] productProdIds) {
        if (prodId == null) {
            return false;
        }
        return isProduct(prodId.longValue(), productProdIds);
    }
}
