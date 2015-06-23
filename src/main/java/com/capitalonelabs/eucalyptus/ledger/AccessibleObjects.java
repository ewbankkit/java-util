//
// Kit's Java Utils.
//

package com.capitalonelabs.eucalyptus.ledger;

import java.lang.reflect.AccessibleObject;
import java.util.function.Function;

/**
 * Static utility methods pertaining to AccessibleObject instances.
 */
public final class AccessibleObjects {
    public static <A extends AccessibleObject, B> B withAccessibility(A accessibleObject, Function<A, B> f) throws SecurityException {
        boolean toggleAccessibility = !accessibleObject.isAccessible();
        if (toggleAccessibility) {
            accessibleObject.setAccessible(true);
        }
        try {
            return f.apply(accessibleObject);
        }
        finally {
            if (toggleAccessibility) {
                accessibleObject.setAccessible(false);
            }
        }
    }

    private AccessibleObjects() {}
}
