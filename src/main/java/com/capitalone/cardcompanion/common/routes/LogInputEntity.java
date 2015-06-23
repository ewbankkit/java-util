//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the input entity should be logged.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInputEntity {}
