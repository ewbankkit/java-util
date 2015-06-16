/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.log;

import org.apache.log4j.NDC;

/**
 * Nested diagnostic context.
 * Used with %x pattern for log4j PatternLayout.
 */
public final class NestedDiagnosticContext {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:08 NestedDiagnosticContext.java NSI";

    /**
     * Constructor.
     */
    private NestedDiagnosticContext() {}

    /**
     * Return the value that was pushed last. If no context is available, then the empty string "" is returned.
     * The context is NOT removed.
     */
    public static String peek() {
        return NDC.peek();
    }

    /**
     * Return the value that was pushed last. If no context is available, then the empty string "" is returned.
     * The context is removed.
     */
    public static String pop() {
        return NDC.pop();
    }

    /**
     * Push new diagnostic context information for the current thread.
     */
    public static void push(String message) {
        NDC.push(message);
    }
}
