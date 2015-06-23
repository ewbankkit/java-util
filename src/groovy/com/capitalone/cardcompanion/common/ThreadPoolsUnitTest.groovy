/*
 * //
 * //  ThreadPoolsUnitTest.groovy
 * //  Created by Ali, Sarmad on 4/29/15 10:13 AM
 * //  Copyright (c) 2015 Capital One. All rights reserved.
 * //  Modified on 4/29/15 10:13 AM
 *
 */

package com.capitalone.cardcompanion.common

import org.junit.Before
import org.junit.Test

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertTrue

final class ThreadPoolsUnitTest {

    public static final String APPLICATION_ID = "ca.capitalone.enterprisemobilebanking"
    private ThreadPoolExecutor threadPoolExecutor

    @Before
    public void before() {
        initApplicationLocale()
        threadPoolExecutor = buildThreadExecutor()
    }

    @Test
    public void testSingleThreadShouldContainApplicationLocale() {

        boolean applicationIdValidInThread = false;

        Runnable runnableThatChecksApplicationLocale = new Runnable() {
            @Override
            void run() {
                if (APPLICATION_ID.equals(ApplicationLocale.applicationLocaleForCurrentThread.applicationId)) {
                    applicationIdValidInThread = true;
                }
            }
        }

        threadPoolExecutor.execute(runnableThatChecksApplicationLocale)

        waitForThreadsToComplete(1);

        assertTrue("Second thread should have proper application id", applicationIdValidInThread);

    }

    @Test
    public void testMultipleThreadShouldContainApplicationLocale() {

        int totalThreadCount = 10;

        List<Boolean> threadSuccesses = Collections.synchronizedList(new ArrayList<Boolean>());

        for (int threadOn = 0; threadOn < totalThreadCount; threadOn++) {

            Runnable runnableThatChecksApplicationLocale = new Runnable() {
                @Override
                void run() {
                    if (applicationIDMatchesExpected()) {
                        threadSuccesses.add(true);
                    } else {
                        threadSuccesses.add(false);
                    }
                }

                private boolean applicationIDMatchesExpected() {
                    APPLICATION_ID.equals(ApplicationLocale.applicationLocaleForCurrentThread.applicationId)
                }
            }

            threadPoolExecutor.execute(runnableThatChecksApplicationLocale)
        }

        waitForThreadsToComplete(totalThreadCount);

        for (int i = 0; i < totalThreadCount; i++) {
            assertTrue("There should have same app_id for ApplicationLocale static", threadSuccesses.get(i));
        }

    }

    private void waitForThreadsToComplete(int numberOfThreads) {
        while (threadPoolExecutor.getCompletedTaskCount() != numberOfThreads) {
            sleep(10)
        }
    }

    private void initApplicationLocale() {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale.initializeApplicationLocale(acceptableLanguages, APPLICATION_ID)
    }

    private ThreadPoolExecutor buildThreadExecutor() {
        final Queue<Runnable> workQueue = new LinkedBlockingQueue<>(500);

        int coreSize = 0
        long keepAliveSeconds = 60L

        ThreadPoolExecutor threadPoolExecutor = ThreadPools.newThreadPoolExecutor(
                "orchestrator",
                coreSize,
                Integer.MAX_VALUE,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                workQueue
        );
        threadPoolExecutor
    }


}
