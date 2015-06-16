package com.github.ewbankkit.util;

import junit.framework.TestCase;


/**
 * Unit test for StringUtil.
 *
 * @author ngrobisa
 *
 */
public class StringUtilUnitTest extends TestCase
{

    public void testConvertCamelCaseToUpperCase()
    {
        String source;

        source = null;
        assertNull("Expected null", StringUtil.convertCamelCaseToUpperCase(source));

        source = "";
        assertEquals("Incorrect conversion.", "", StringUtil.convertCamelCaseToUpperCase(source));

        source = "a";
        assertEquals("Incorrect conversion.", "A", StringUtil.convertCamelCaseToUpperCase(source));

        source = "ab";
        assertEquals("Incorrect conversion.", "AB", StringUtil.convertCamelCaseToUpperCase(source));

        source = "someSimpleCamelCaseText";
        assertEquals("Incorrect conversion.", "SOME_SIMPLE_CAMEL_CASE_TEXT",
                StringUtil.convertCamelCaseToUpperCase(source));

        source = "aBcDeFg";
        assertEquals("Incorrect conversion.", "A_BC_DE_FG", StringUtil.convertCamelCaseToUpperCase(source));

        source = "ABcDEfg";
        assertEquals("Incorrect conversion.", "ABC_DEFG", StringUtil.convertCamelCaseToUpperCase(source));
    }


    public void testConvertUnderscoreToCamelCase()
    {
        String source;

        source = null;
        assertNull("Expected null", StringUtil.convertUnderscoreToCamelCase(source));

        source = "";
        assertEquals("Incorrect conversion.", "", StringUtil.convertUnderscoreToCamelCase(source));

        source = "A";
        assertEquals("Incorrect conversion.", "a", StringUtil.convertUnderscoreToCamelCase(source));

        source = "AB";
        assertEquals("Incorrect conversion.", "ab", StringUtil.convertUnderscoreToCamelCase(source));

        source = "SOME_SIMPLE_CAMEL_CASE_TEXT";
        assertEquals("Incorrect conversion.", "someSimpleCamelCaseText",
                StringUtil.convertUnderscoreToCamelCase(source));

        source = "A_BC_DE_FG";
        assertEquals("Incorrect conversion.", "aBcDeFg", StringUtil.convertUnderscoreToCamelCase(source));

        source = "ABC_DEFG";
        assertEquals("Incorrect conversion.", "abcDefg", StringUtil.convertUnderscoreToCamelCase(source));
    }

}
