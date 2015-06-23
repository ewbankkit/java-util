//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Jackson object mapper for unwrapped objects.
 */
@ThreadSafe
public final class UnwrappedObjectMapper extends AbstractObjectMapper {
    /*
     * Constructor.
     */
    private UnwrappedObjectMapper() {
        super(false);
    }

    /**
     * Returns the single instance.
     */
    public static ObjectMapper getInstance() {
        return LazyHolder.INSTANCE.getObjectMapper();
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final UnwrappedObjectMapper INSTANCE = new UnwrappedObjectMapper();
    }
}
