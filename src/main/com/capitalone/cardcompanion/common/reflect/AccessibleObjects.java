//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.reflect;

import com.google.common.base.Function;

import java.lang.reflect.AccessibleObject;

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
