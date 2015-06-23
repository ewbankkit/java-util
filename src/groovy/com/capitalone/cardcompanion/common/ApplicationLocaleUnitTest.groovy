/*
 * //
 * //  ApplicationLocaleUnitTest.groovy
 * //  Created by Ali, Sarmad on 4/24/15 1:28 PM
 * //  Copyright (c) 2015 Capital One. All rights reserved.
 * //  Modified on 4/24/15 1:28 PM
 *
 */

package com.capitalone.cardcompanion.common

import org.junit.Test

import static org.junit.Assert.assertArrayEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue


/**
 * Created by hfg971 on 4/24/15.
 */
final class ApplicationLocaleUnitTest {

    @Test
    void testInitialize1()
    {
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale()

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

    }

    @Test
    void testInitialize2()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA1()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.UK
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA2()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.UK
        acceptableLanguages.add Locale.ITALY
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA3()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.UK
        acceptableLanguages.add Locale.ITALY
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking.dev")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA4()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add new Locale("en","IN")
        acceptableLanguages.add Locale.ITALY
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA_FR1()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.FRANCE
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread
        assertEquals applicationLocale.acceptableLanguages, ApplicationLocale.getApplicationLocaleForCurrentThread().acceptableLanguages
        assertEquals applicationLocale.acceptableLanguages.size() , ApplicationLocale.getApplicationLocaleForCurrentThread().acceptableLanguages.size()
        assertEquals Locale.CANADA_FRENCH, ApplicationLocale.getApplicationLocaleForCurrentThread().getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA_FR2()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA_FRENCH, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestCA_FR3()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking.dev")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA_FRENCH, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestUS1()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"com.capitalone.cardcompanion")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.US, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestUS2()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"com.capitalone.cardcompanion")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.US, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequestUS3()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.FRENCH
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"com.capitalone.cardcompanion")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.US, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequest1()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CHINESE
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,null)

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CHINESE, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequest2()
    {

        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(null,null)

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.getDefault(), ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testInitializeLocaleForCurrentRequest3()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,null)

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread

        assertEquals Locale.CANADA_FRENCH, ApplicationLocale.applicationLocaleForCurrentThread.getLocaleForCurrentRequest();

    }

    @Test
    void testIsCanadainApp()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread
        assertTrue(ApplicationLocale.applicationLocaleForCurrentThread.isCanadianApplication())

    }

    @Test
    void testIsCanadainApp2()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking.dev")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread
        assertTrue(ApplicationLocale.applicationLocaleForCurrentThread.isCanadianApplication())

    }

    @Test
    void testIsUSApp()
    {
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"com.capitalone.cardcompanion")

        assertEquals applicationLocale, ApplicationLocale.applicationLocaleForCurrentThread
        assertTrue(ApplicationLocale.applicationLocaleForCurrentThread.isUSApplication())

    }

}
