//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

/**
 * Indicates an exception during server initialization.
 */
final class InitializationException extends Exception {
    /**
     * Constructor.
     */
    InitializationException(String message) {
        super(message)
    }

    /**
     * Constructor.
     */
    InitializationException(Throwable cause) {
        super(cause)
    }

    /**
     * Constructor.
     */
    InitializationException(String message, Throwable cause) {
        super(message, cause)
    }
}
