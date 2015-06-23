//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

import org.junit.Test

import static org.junit.Assert.assertEquals

final class AppVersionUnitTest {
    @Test
    void testEquals1() {
        AppVersion appVersion = AppVersion.fromVersionString '0.0'
        assertEquals AppVersion.UNKNOWN, appVersion
    }

    @Test
    void testEquals2() {
        AppVersion appVersion = AppVersion.fromVersionString '2.5'
        assertEquals 2, appVersion.majorVersionNumber
        assertEquals 5, appVersion.minorVersionNumber
        assertEquals 0, appVersion.hotfixVersionNumber
    }

    @Test
    void testEquals3() {
        AppVersion appVersion = AppVersion.fromVersionString '3.6.18'
        assertEquals 3, appVersion.majorVersionNumber
        assertEquals 6, appVersion.minorVersionNumber
        assertEquals 18, appVersion.hotfixVersionNumber
    }

    @Test(expected = IllegalArgumentException.class)
    void testEquals4() {
        AppVersion appVersion = AppVersion.fromVersionString '3.6.'
        assertEquals 3, appVersion.majorVersionNumber
        assertEquals 6, appVersion.minorVersionNumber
    }

    @Test(expected = IllegalArgumentException.class)
    void testEquals5() {
        AppVersion appVersion = AppVersion.fromVersionString 'xyz.2'
        assertEquals 'xyz', appVersion.majorVersionNumber
        assertEquals 2, appVersion.minorVersionNumber
    }

    @Test
    void testEquals6() {
        AppVersion appVersion = AppVersion.fromVersionString '1.0.0-default1-qa'
        assertEquals 1, appVersion.majorVersionNumber
        assertEquals 0, appVersion.minorVersionNumber
        assertEquals 0, appVersion.hotfixVersionNumber
    }

    @Test(expected = IllegalArgumentException.class)
    void testEquals7() {
        AppVersion appVersion = AppVersion.fromVersionString '3.6.x'
        assertEquals 3, appVersion.majorVersionNumber
        assertEquals 6, appVersion.minorVersionNumber
    }
}
