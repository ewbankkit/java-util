//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

/**
 * Represents a REST client PUT request.
 */
public final class PutRequest<T, U> extends Request<T, U> {
    /**
     * Constructor.
     */
    public PutRequest() {
        super(PUT);
    }
}
