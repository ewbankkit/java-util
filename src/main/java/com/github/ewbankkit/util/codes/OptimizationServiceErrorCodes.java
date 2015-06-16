/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

// Optimization service error codes.
// Values are returned via IntHubException.
public final class OptimizationServiceErrorCodes {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:31 OptimizationServiceErrorCodes.java NSI";

    public static final int VALIDATION_ERROR = 9000;
    public static final int DB_ERROR = 9001;
    public static final int NOT_ENOUGH_DATA_ERROR = 9002;
    public static final int QUALITY_SCORE_OPT_ERROR = 9003;
    public static final int BUDGET_MANAGER_ERROR = 9004;

    private OptimizationServiceErrorCodes() {
        return;
    }
}
