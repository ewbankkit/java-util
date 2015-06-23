//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

/**
 * Represents a REST client POST request.
 */
public final class PostRequest<T, U> extends Request<T, U> {
    /**
     * Constructor.
     */
    public PostRequest() {
        super(POST);
    }
}
