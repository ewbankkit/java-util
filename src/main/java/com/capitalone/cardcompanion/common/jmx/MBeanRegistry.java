//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.jmx;

import com.capitalone.cardcompanion.common.Config;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static utility methods pertaining to JMX.
 */
public final class MBeanRegistry {
    public static final String DOMAIN = "com.github.ewbankkit";

    private final MBeanServer             mBeanServer      = mBeanServer();
    private final Map<ObjectName, Object> registeredMBeans = new LinkedHashMap<>();

    private MBeanRegistry() {}

    /**
     * Returns the single server instance.
     */
    public static MBeanRegistry getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the object name for the specified name.
     */
    public static ObjectName getObjectName(String name) throws JMException {
        Preconditions.checkNotNull(name);

        return ObjectName.getInstance(DOMAIN, "name", name);
    }

    /**
     * Returns whether or not JMX is enabled.
     */
    public static boolean isJmxEnabled() {
        return Config.getInstance().getBoolean("jmxEnabled", false);
    }

    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /**
     * Registers an MBean
     */
    public void register(String name, Object mBean) throws JMException {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(mBean);

        ObjectName objectName = getObjectName(name);
        mBeanServer.registerMBean(mBean, objectName);
        registeredMBeans.put(objectName, mBean);
    }

    /**
     * Unregisters all registered MBeans.
     */
    public void unregister() {
        for (ObjectName name : registeredMBeans.keySet()) {
            try {
                mBeanServer.unregisterMBean(name);
            }
            catch (JMException ignored) {}
        }
        registeredMBeans.clear();
    }

    /**
     * Returns the preferred MBean server.
     */
    private static MBeanServer mBeanServer() {
        MBeanServer mBeanServer = Iterables.getFirst(MBeanServerFactory.findMBeanServer(null), null);
        if (mBeanServer == null) {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        return mBeanServer;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final MBeanRegistry INSTANCE = new MBeanRegistry();
    }
}
