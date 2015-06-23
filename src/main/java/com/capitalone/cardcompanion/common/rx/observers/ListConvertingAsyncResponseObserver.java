//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.rx.observers;

import com.capitalone.cardcompanion.common.jaxrs.Responses;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.AsyncResponse;

/**
 * AsyncResponse observer that converts to a list of items.
 */
public final class ListConvertingAsyncResponseObserver<T, U> extends AbstractAsyncResponseObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListConvertingAsyncResponseObserver.class);

    private final Function<T, U> converter;

    /**
     * Constructor.
     */
    public ListConvertingAsyncResponseObserver(AsyncResponse asyncResponse, Function<T, U> converter) {
        super(asyncResponse);
        this.converter = converter;
    }

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    @Override
    public void onCompleted() {
        LOGGER.trace("Observable finished");

        Iterable<T> data = data();
        if (Iterables.isEmpty(data)) {
            resume(notFound());
        }
        else {
            ImmutableList.Builder<U> builder = ImmutableList.builder();
            for (T t : data) {
                U u = converter.apply(t);
                assert u != null;
                builder.add(u);
            }
            resume(Responses.ok(builder.build()));
        }
    }
}
