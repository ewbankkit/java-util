//
// Kit's Java Utils.
//

package com.github.ewbankkit.rx.observers;

import com.github.ewbankkit.jaxrs.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.container.AsyncResponse;

/**
 * AsyncResponse observer that returns HTTP 204 No Content.
 */
public final class NoContentAsyncResponseObserver extends AbstractAsyncResponseObserver<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoContentAsyncResponseObserver.class);

    /**
     * Constructor.
     */
    public NoContentAsyncResponseObserver(AsyncResponse asyncResponse, @Nullable String notFoundMessage) {
        super(asyncResponse, notFoundMessage);
    }

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    @Override
    public void onCompleted() {
        LOGGER.trace("Observable finished");

        resume(item().isPresent() ? Responses.noContent() : notFound());
    }
}
