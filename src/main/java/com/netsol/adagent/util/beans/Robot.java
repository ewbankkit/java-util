/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents a web robot.
 */
public class Robot extends BaseData implements Serializable {

    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:06 Robot.java NSI";

    // Auto-generated. Regenerate if there are any relevant changes.
    private static final long serialVersionUID = 79393965851851600L;

    private String httpUserAgent;
    private String robotName;

    /**
     * Constructor.
     */
    public Robot() {
        super();

        return;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    public String getHttpUserAgent() {
        return this.httpUserAgent;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public String getRobotName() {
        return this.robotName;
    }
}
