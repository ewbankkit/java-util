/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents an Interceptor mapping.
 */
public class InterceptorMapping extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:54 InterceptorMapping.java NSI";

    // Auto-generated. Regenerate if there are any relevant changes.
    private static final long serialVersionUID = 8165829926398515677L;

    private String alias;
    private String prodInstId;
    private String realHost;
    private int realPort;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setRealHost(String realHost) {
        this.realHost = realHost;
    }

    public String getRealHost() {
        return realHost;
    }

    public void setRealPort(int realPort) {
        this.realPort = realPort;
    }

    public int getRealPort() {
        return realPort;
    }
}
