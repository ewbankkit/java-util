//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

/**
 * Represents a REST client GET request.
 */
public final class GetRequest<U> extends Request<Void, U> {
    /**
     * Constructor.
     */
    public GetRequest() {
        super(GET);
    }
}
