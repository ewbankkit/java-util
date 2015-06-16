/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class LocalHost {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:07 LocalHost.java NSI";

    private static final LocalHost INSTANCE = new LocalHost();
    public static final String NAME = LocalHost.INSTANCE.name;

    private String name;

    /**
     * Constructor.
     */
    private LocalHost() {
        try {
            this.name = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            this.name = "localhost";
        }

        return;
    }

    // Test harness.
    public static void main(String[] args) {
        try {
            System.out.println(LocalHost.NAME);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        return;
    }
}
