//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents the application's descriptor.
 */
@ThreadSafe
public final class Descriptor {
    private String name;
    private String version;

    /*
     * Constructor.
     */
    private Descriptor() {}

    /**
     * Returns the single instance.
     */
    public static Descriptor getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the application name.
     */
    public String getName() {
        Preconditions.checkState(name != null);

        return name;
    }

    /**
     * Sets the application name.
     */
    public void setName(String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkState(this.name == null);

        this.name = name;
    }

    /**
     * Returns the application version.
     */
    public String getVersion() {
        Preconditions.checkState(version != null);

        return version;
    }

    /**
     * Set the application version.
     */
    public void setVersion(String version) {
        Preconditions.checkNotNull(version);
        Preconditions.checkState(this.version == null);

        this.version = version;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public final static Descriptor INSTANCE = new Descriptor();
    }
}
