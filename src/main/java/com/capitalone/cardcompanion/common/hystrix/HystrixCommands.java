//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.hystrix;

import com.google.common.base.Preconditions;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Callable;

/**
 * Hystrix commands.
 */
@ThreadSafe
public final class HystrixCommands {
    /**
     * Constructor.
     */
    private HystrixCommands() {}

    /**
     * Returns a new Hystrix command that wraps the specified Callable.
     */
    public static <T> HystrixCommand<T> fromCallable(
        final Callable<T> callable,
        final String      commandKeyName,
        final String      commandGroupKeyName
    ) {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(commandKeyName);
        Preconditions.checkNotNull(commandGroupKeyName);

        return new HystrixCommand<T>(
            Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKeyName)).
                andCommandKey(HystrixCommandKey.Factory.asKey(commandKeyName))
        ) {
            @Override
            protected T run() throws Exception {
                return callable.call();
            }
        };
    }
}
