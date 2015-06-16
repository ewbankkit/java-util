/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.lang.reflect.AccessibleObject;

public final class AccessibleObjectUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:47 AccessibleObjectUtil.java NSI";
    
    public static <A extends AccessibleObject> void withToggledAccessibility(A accessibleObject, A1<A> a1) throws Exception {
        withToggledAccessibility(accessibleObject, a1.toF1());
    }
    
    public static <A extends AccessibleObject, B> B withToggledAccessibility(A accessibleObject, F1<A, B> f1) throws Exception {
        boolean toggleAccessiblity = !accessibleObject.isAccessible();
        if (toggleAccessiblity) {
            accessibleObject.setAccessible(true);
        }
        try {
            return f1.apply(accessibleObject);
        }
        finally {
            if (toggleAccessiblity) {
                accessibleObject.setAccessible(false);
            }
        }
    }

    private AccessibleObjectUtil() {}
}
