//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.rx.observers;

import com.google.common.base.Functions;

import javax.ws.rs.container.AsyncResponse;

/**
 * Simple single-item AsyncResponse observer.
 */
public final class SimpleAsyncResponseObserver<T> extends ConvertingAsyncResponseObserver<T, T> {
    /**
     * Constructor.
     */
    public SimpleAsyncResponseObserver(AsyncResponse asyncResponse) {
        super(asyncResponse, Functions.<T>identity());
    }
}
