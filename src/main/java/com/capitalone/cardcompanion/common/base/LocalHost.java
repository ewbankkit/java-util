//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Represents localhost.
 */
public final class LocalHost {
    private static final LocalHost INSTANCE = new LocalHost();

    public static final String ADDRESS = INSTANCE.address;
    public static final String NAME = INSTANCE.name;

    private final String address;
    private final String name;

    /**
     * Constructor.
     */
    private LocalHost() {
        String address = "127.0.0.1";
        String name = "localhost";
        try {
            name = InetAddress.getLocalHost().getHostName();

            InetAddress[] addresses = InetAddress.getAllByName(name);
            for (InetAddress a : addresses) {
                if (a instanceof Inet4Address) {
                    address = a.getHostAddress();
                }
            }
        }
        catch (UnknownHostException ignored) {}

        this.address = address;
        this.name = name;
    }
}
