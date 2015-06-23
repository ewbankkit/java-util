//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.rx.observables

import com.google.common.base.Optional
import org.junit.Test
import rx.observables.BlockingObservable

import static com.capitalone.cardcompanion.common.rx.observables.Observables.required
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertNull

final class ObservablesUnitTest {
    @Test
    void testRequired1() {
        BlockingObservable<String> bo = required(rx.Observable.just(Optional.of("OK"))).
            toBlocking()
        assertEquals "OK", bo.first()
    }

    @Test
    void testRequired2() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.of("OK")))
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertEquals "OK", result
    }

    @Test
    void testRequired3() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.of("OK"))).
            first()
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertEquals "OK", result
    }

    @Test(expected = NoSuchElementException.class)
    void testRequired4() {
        BlockingObservable<String> bo = required(rx.Observable.just(Optional.<String>absent())).
            toBlocking()
        assertNotEquals "OK", bo.first()
    }

    @Test
    void testRequired5() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.<String>absent()))
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertNull result
    }

    @Test
    void testRequired6() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.<String>absent())).
            first()
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertEquals "ERROR", result
    }

    @Test
    void testRequired7() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.<String>absent())).
            firstOrDefault("DEFAULT")
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertEquals "DEFAULT", result
    }

    @Test
    void testRequired8() {
        rx.Observable<String> o = required(rx.Observable.just(Optional.<String>absent())).
            firstOrDefault("DEFAULT").
            filter({
                it != "DEFAULT"
            })
        String result = null
        o.subscribe(
            {
                // OnNext
                result = it
            },
            {
                // OnError
                result = "ERROR"
            }
        )
        assertNull result
    }
}
