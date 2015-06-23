//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.concurrent.BasicThreadFactory.Builder;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pools.
 */
@ThreadSafe
public final class ThreadPools {
    /**
     * Constructor.
     */
    private ThreadPools() {}

    /**
     * Returns a new thread pool executor.
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(
        final String                  key,
        final int                     corePoolSize,
        final int                     maxPoolSize,
        final long                    keepAliveTime,
        final TimeUnit                timeUnit,
        final BlockingQueue<Runnable> workQueue
    ) {
        Preconditions.checkNotNull(key);
        Preconditions.checkArgument(corePoolSize >= 0);
        Preconditions.checkArgument(maxPoolSize > 0);
        Preconditions.checkArgument(keepAliveTime >= 0);
        Preconditions.checkNotNull(timeUnit);
        Preconditions.checkNotNull(workQueue);

        return new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            timeUnit,
            workQueue,
            new Builder().
                daemon(true).
                namingPattern(key + "-%d").
                build()
        ) {
            /**
             * Method invoked upon completion of execution of the given Runnable.
             * This method is invoked by the thread that executed the task.
             * If non-null, the Throwable is the uncaught RuntimeException or Error that caused execution to terminate abruptly.
             */
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                ExecutionContext.removeContextFromCurrentThread();
                ApplicationLocale.removeApplicationLocaleFromCurrentThread();
                super.afterExecute(r, t);
            }

            /**
             * Method invoked prior to executing the given Runnable in the given thread.
             * This method is invoked by thread t that will execute task r, and may be used to re-initialize ThreadLocals, or to perform logging.
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                SharedRunnable sharedRunnable = (SharedRunnable)r;
                ExecutionContext.setContextOnCurrentThread(sharedRunnable.context);
                ApplicationLocale.setApplicationLocaleOnCurrentThread(sharedRunnable.applicationLocale);

                super.beforeExecute(t, r);
            }

            /**
             * Executes the given task sometime in the future. The task may execute in a new thread or in an existing pooled thread. If the task
             * cannot be submitted for execution, either because this executor has been shutdown or because its capacity has been reached, the
             * task is handled by the current RejectedExecutionHandler.
             */
            @Override
            public void execute(Runnable command) {
                super.execute(new SharedRunnable(command));
            }
        };
    }

    /**
     * Runnable that propagates execution context and application local.
     */
    private static final class SharedRunnable implements Runnable {
        public final ExecutionContext context = ExecutionContext.getContextForCurrentThread();

        public  final ApplicationLocale applicationLocale = ApplicationLocale.getApplicationLocaleForCurrentThread();

        private final Runnable runnable;

        /**
         * Constructor.
         */
        public SharedRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }
}
