//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import javax.ws.rs.Priorities;

/**
 * Jersey filter priorities.
 */
public final class FilterPriorities {
    private FilterPriorities() {}

    // Pre-matching filters run first.
    public static final int EXECUTION_CONTEXT             = Integer.MIN_VALUE; // Must be first.
    public static final int APPLICATION_LOCALE             = EXECUTION_CONTEXT + 50;
    public static final int SERVER_LOGGING                = EXECUTION_CONTEXT + 100;
    // Post-matching filter runs next.
    public static final int SERVER_HEALTH_CHECK           = 1;
    public static final int CNS_AUTHENTICATION_TOKEN      = Priorities.AUTHORIZATION;
    public static final int PASSBOOK_AVAILABILITY_CHECK   = SERVER_HEALTH_CHECK + 100;
    public static final int PASSBOOK_AUTHORIZATION_TOKEN  = Priorities.AUTHORIZATION;
    public static final int PASSBOOK_PASS_TYPE_IDENTIFIER = PASSBOOK_AUTHORIZATION_TOKEN + 100;
    public static final int USER_TOKEN                    = Priorities.AUTHORIZATION;
    public static final int NEW_SESSION                   = USER_TOKEN + 100;

    public static final int SWAGGER = 42; // TODO
}
