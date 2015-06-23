//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.base;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ObjectUtils;
import rx.exceptions.OnErrorThrowable.OnNextValue;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;

/**
 * Static utility methods pertaining to Throwable instances.
 */
public final class Throwables {
    private Throwables() {}

    /**
     * Returns the cause of the specified exception if present, else the exception.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static Throwable getCauseOrSelf(Throwable e) {
        Preconditions.checkNotNull(e);

        return ObjectUtils.defaultIfNull(e.getCause(), e);
    }

    /**
     * Returns the "useful" cause of the specified exception.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static Throwable getUseful(Throwable e) {
        Preconditions.checkNotNull(e);

        if ((e instanceof ExecutionException) || ((e instanceof RuntimeException) && !(e instanceof WebApplicationException))) {
            Throwable cause = e.getCause();
            if (!(cause instanceof OnNextValue)) {
                e = ObjectUtils.defaultIfNull(cause, e);
            }
        }
        if (e instanceof ProcessingException) {
            e = getCauseOrSelf(e);
        }

        return e;
    }
}
