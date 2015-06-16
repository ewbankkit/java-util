package com.github.ewbankkit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * String utilities.
 *
 * @author ngrobisa
 *
 */
public final class StringUtil
{

    private static Log log = LogFactory.getLog(StringUtil.class);


    /**
     * Private constructor. Prevent this class to be instantiated.
     */
    private StringUtil()
    {
        // this class is not supposed to be instantiated
    }


    /**
     * Converts a string in camel case into an upper case string separating words with the '_'
     * character.
     *
     * @param camelCaseString The string we want to convert to upper case.
     * @return The converted string.
     */
    public static String convertCamelCaseToUpperCase(final String camelCaseString)
    {
        final String result;

        if ( camelCaseString != null && camelCaseString.length() > 0 ) {
            final char[] sourceChars = camelCaseString.toCharArray();
            final char[] resultChars = new char[2 * camelCaseString.length() - 1];
            boolean currCharIsUpperCase, prevCharIsUpperCase = false;
            int j = 0;

            for ( int i = 0, n = camelCaseString.length(); i < n; i++ ) {
                char c = sourceChars[i];

                if ( Character.isLetter(c) ) {
                    currCharIsUpperCase = Character.isUpperCase(c);

                    if ( i > 0 && !prevCharIsUpperCase && currCharIsUpperCase ) {
                        resultChars[j++] = '_';
                    }

                    prevCharIsUpperCase = currCharIsUpperCase;
                    resultChars[j++] = currCharIsUpperCase ? c : Character.toUpperCase(c);
                }
                else {
                    resultChars[j++] = c;
                }
            }

            result = String.copyValueOf(resultChars, 0, j);
        }
        else {
            result = camelCaseString;
        }

        return result;
    }


    /**
     * Converts a string with words separated by underscores into a "camel case" string.
     *
     * @param camelCaseString The string we want to convert to camel case.
     * @return The converted string.
     */
    public static String convertUnderscoreToCamelCase(final String stringWithUnderscores)
    {
        final String result;

        if ( stringWithUnderscores != null && stringWithUnderscores.length() > 0 ) {
            final char[] sourceChars = stringWithUnderscores.toCharArray();
            final char[] resultChars = new char[stringWithUnderscores.length()];
            boolean nextCharMustBeLower = true;
            int j = 0;

            for ( int i = 0, n = stringWithUnderscores.length(); i < n; i++ ) {
                char c = sourceChars[i];

                if ( Character.isLetter(c) ) {
                    resultChars[j++] = nextCharMustBeLower ? Character.toLowerCase(c) : Character.toUpperCase(c);
                    nextCharMustBeLower = true;
                }
                else if ( c == '_' ) {
                    nextCharMustBeLower = false;
                }
                else {
                    resultChars[j++] = c;
                }
            }

            result = String.copyValueOf(resultChars, 0, j);
        }
        else {
            result = stringWithUnderscores;
        }

        return result;
    }


    /**
     * Parse the numeric portion from a product Instance ID
     *
     * @param prodInst
     * @return numeric value. If there is no number found, then 0
     */
    public static long parseIntFromProdInstId(String prodInst)
    {
        long num = 0;
        log.info("Parsing number from string: " + prodInst);
        Matcher m = Pattern.compile("\\d+").matcher(prodInst);
        if ( m.find() ) {
            try {
                num = Long.parseLong(m.group());

            }
            catch (NumberFormatException e) {
                log.info("Unable to Parse number from: " + prodInst);
            }
        }
        log.info("Numeric Value: " + num);
        return num;

    }

}
