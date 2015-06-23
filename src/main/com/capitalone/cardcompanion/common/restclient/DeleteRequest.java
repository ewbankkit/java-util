//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

/**
 * Represents a REST client DELETE request.
 */
public class DeleteRequest<U> extends Request<Void, U> {
    /**
     * Constructor.
     */
    public DeleteRequest() {
        super(DELETE);
    }
}
