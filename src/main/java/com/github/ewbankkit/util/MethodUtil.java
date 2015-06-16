/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

public final class MethodUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:12 MethodUtil.java NSI";

    /**
     * Return whether or not a sub class has overridden a base class method.
     */
    public static <T> boolean hasOverridenMethod(Class<T> baseClass, Class<? extends T> subClass, String name, Class<?>... parameterTypes) {
        try {
            while ((subClass != null) && !subClass.equals(baseClass)) {
                try {
                    subClass.getDeclaredMethod(name, parameterTypes);
                    return true;
                }
                catch (NoSuchMethodException ex) {}
                subClass = subClass.getSuperclass().asSubclass(baseClass);
            }
        }
        catch (Exception ex) {}

        return false;
    }

    private MethodUtil() {}
}
