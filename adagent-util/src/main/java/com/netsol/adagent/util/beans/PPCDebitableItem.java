package com.netsol.adagent.util.beans;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.log.BaseLoggable;

public abstract class PPCDebitableItem extends DebitableItem {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:03 PPCDebitableItem.java NSI";

    private Long hitId;
    private long nsAdId;
    private long nsAdGroupId;
    private long nsKeywordId;

    /**
     * Constructor.
     */
    protected PPCDebitableItem(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    protected PPCDebitableItem(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    protected PPCDebitableItem(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public Long getHitId() {
        return hitId;
    }

    public void setHitId(Long hitId) {
        this.hitId = hitId;
    }

    public long getNsAdGroupId() {
        return nsAdGroupId;
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }

    public long getNsKeywordId() {
        return nsKeywordId;
    }

    public void setNsKeywordId(long nsKeywordId) {
        this.nsKeywordId = nsKeywordId;
    }

    public long getNsAdId() {
        return nsAdId;
    }

    public void setNsAdId(long nsAdId) {
        this.nsAdId = nsAdId;
    }
}
