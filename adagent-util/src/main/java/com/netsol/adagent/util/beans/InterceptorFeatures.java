/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents Interceptor features.
 */
public class InterceptorFeatures extends BaseDataWithUpdateTracking {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:54 InterceptorFeatures.java NSI";

    @ColumnName("allow_empty_referrer")
    private boolean allowEmptyReferrer;
    @ColumnName("collect_email_params")
    private boolean collectEmailParams;
    @ColumnName("collect_form_params")
    private boolean collectFormParams;
    @ColumnName("collect_shopping_cart_params")
    private boolean collectShoppingCartParams;
    @ColumnName("perform_replacements")
    private boolean performReplacements;
    private String prodInstId;
    @ColumnName("propagate_ad_params")
    private boolean propagateAdParams;
    @ColumnName("rewrite_referer")
    private boolean rewriteReferrer;
    @ColumnName("track_email")
    private boolean trackEmail;
    @ColumnName("track_form")
    private boolean trackForm;
    @ColumnName("track_high_value_page")
    private boolean trackHighValuePage;
    @ColumnName("track_shopping_cart")
    private boolean trackShoppingCart;

    public void setAllowEmptyReferrer(boolean allowEmptyReferrer) {
        setTrackedField("allowEmptyReferrer", allowEmptyReferrer);
    }

    public boolean isAllowEmptyReferrer() {
        return allowEmptyReferrer;
    }

    public void setCollectEmailParams(boolean collectEmailParams) {
        setTrackedField("collectEmailParams", collectEmailParams);
    }

    public boolean isCollectEmailParams() {
        return collectEmailParams;
    }

    public void setCollectFormParams(boolean collectFormParams) {
        setTrackedField("collectFormParams", collectFormParams);
    }

    public boolean isCollectFormParams() {
        return collectFormParams;
    }

    public void setCollectShoppingCartParams(boolean collectShoppingCartParams) {
        setTrackedField("collectShoppingCartParams", collectShoppingCartParams);
    }

    public boolean isCollectShoppingCartParams() {
        return collectShoppingCartParams;
    }

    public void setPerformReplacements(boolean performReplacements) {
        setTrackedField("performReplacements", performReplacements);
    }

    public boolean isPerformReplacements() {
        return performReplacements;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setPropagateAdParams(boolean propagateAdParams) {
        setTrackedField("propagateAdParams", propagateAdParams);
    }

    public boolean isPropagateAdParams() {
        return propagateAdParams;
    }

    public void setRewriteReferrer(boolean rewriteReferrer) {
        setTrackedField("rewriteReferrer", rewriteReferrer);
    }

    public boolean isRewriteReferrer() {
        return rewriteReferrer;
    }

    public void setTrackEmail(boolean trackEmail) {
        setTrackedField("trackEmail", trackEmail);
    }

    public boolean isTrackEmail() {
        return trackEmail;
    }

    public void setTrackForm(boolean trackForm) {
        setTrackedField("trackForm", trackForm);
    }

    public boolean isTrackForm() {
        return trackForm;
    }

    public void setTrackHighValuePage(boolean trackHighValuePage) {
        setTrackedField("trackHighValuePage", trackHighValuePage);
    }

    public boolean isTrackHighValuePage() {
        return trackHighValuePage;
    }

    public void setTrackShoppingCart(boolean trackShoppingCart) {
        setTrackedField("trackShoppingCart", trackShoppingCart);
    }

    public boolean isTrackShoppingCart() {
        return trackShoppingCart;
    }
}
