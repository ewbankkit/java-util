//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.capitalone.cardcompanion.common.base.LocalHost;
import com.capitalone.cardcompanion.common.jmx.MBeanRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import javax.annotation.concurrent.ThreadSafe;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.File;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents the application's Tomcat domain.
 */
@ThreadSafe
public final class TomcatDomain {
    private final Optional<String> localBaseUrl;
    private final Optional<String> name;

    private TomcatDomain() {
        String name = null;

        // Deduce the Tomcat domain name.
        String catalinaBase = System.getenv("CATALINA_BASE");
        if (catalinaBase != null) {
            // It's the last part of the path.
            Path path = new File(catalinaBase).toPath();
            try {
                name = Iterators.getLast(path.iterator()).toString();
            }
            catch (NoSuchElementException ignore) {}
        }

        this.name = Optional.fromNullable(name);

        String localBaseUrl = null;

        // Get the domain's local URL.
        if (MBeanRegistry.isJmxEnabled()) {
            MBeanServer mBeanServer = MBeanRegistry.getInstance().getMBeanServer();
            try {
                Set<ObjectName> objectNames = mBeanServer.queryNames(
                    new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"))
                );
                ObjectName objectName = Iterables.getFirst(objectNames, null);
                if (objectName != null) {
                    String scheme = mBeanServer.getAttribute(objectName, "scheme").toString();
                    String port = objectName.getKeyProperty("port");
                    localBaseUrl = scheme + "://" + LocalHost.ADDRESS + ':' + port;
                }
            }
            catch (JMException ignore) {}
        }

        this.localBaseUrl = Optional.fromNullable(localBaseUrl);
    }

    /**
     * Returns the single instance.
     */
    public static TomcatDomain getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the local base URL.
     */
    public Optional<String> getLocalBaseUrl() {
        return localBaseUrl;
    }

    /**
     * Returns the application name.
     */
    public Optional<String> getName() {
        return name;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final TomcatDomain INSTANCE = new TomcatDomain();
    }
}
