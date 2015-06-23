package com.capitalone.cardcompanion.common

/**
 * Created by jep372 on 4/27/15.
 */
import org.junit.Test

import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

final class ResourceBundleLocaleTest {
    @Test
    void testGetLocaleMFAText1(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("","")
        assertNull actualText

    }
    @Test
    void testGetLocaleMFAText2(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("","Sample Text")
        assertNull actualText

    }
    @Test
    void testGetLocaleMFAText3(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText(null,null)
        assertNull actualText

    }
    @Test
    void testGetLocaleMFAText4(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText(null,"")
        assertNull actualText

    }
    @Test
    void testGetLocaleMFAText5(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("15",null)
        def expectedText = "What is your father's middle name?"
        assertEquals actualText, expectedText

    }
    @Test
    void testGetLocaleMFAText6(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("15","What is your father's middle name?").toString();
        def expectedText = "What is your father's middle name?"
        assertEquals actualText, expectedText

    }
    @Test
    void testGetLocaleMFAText7(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("20",null)
        def expectedText = "Quel est le deuxième prénom de votre grand-père (le père de votre père)?"
        assertEquals actualText, expectedText

    }
    @Test
    void testGetLocaleMFAText8(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("20","What is your grandfather's middle name (your father's father)?")
        def expectedText = "Quel est le deuxième prénom de votre grand-père (le père de votre père)?"
        assertTrue actualText.equals(expectedText)

    }
    @Test
    void testGetLocaleMFAText9(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("170",null)
        assertNull actualText

    }
    @Test
    void testGetLocaleMFAText10(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.CANADA_FRENCH
        acceptableLanguages.add Locale.FRENCH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("170","Sample Text")
        def expectedText = "Sample Text"
        assertEquals actualText, expectedText

    }
    @Test
    void testGetLocaleMFAText11(){
        ArrayList<Locale> acceptableLanguages = new ArrayList<Locale>()
        acceptableLanguages.add Locale.ENGLISH
        ApplicationLocale applicationLocale = ApplicationLocale.initializeApplicationLocale(acceptableLanguages,"ca.capitalone.enterprisemobilebanking")
        def actualText = ResourceBundleLocale.getLocaleMFAText("170","Sample Text")
        def expectedText = "Sample Text"
        assertEquals actualText, expectedText

    }
}
