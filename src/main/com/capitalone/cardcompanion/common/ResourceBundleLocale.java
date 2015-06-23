package com.capitalone.cardcompanion.common;

import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by jep372 on 4/24/15.
 * Used to display specific Locale texts
 */
public class ResourceBundleLocale {
    final static String MFA_QUESTION_PREFIX = "MFA_QUESTION_";

    /**
     *
     * @param id : Id of the MFA question as coming from the database
     * @param defaultText : The actual MFA question as coming from the database in English
     * @return : The translated MFA question in English or French depending on the Current Application Locale
     */
    public static String getLocaleMFAText(String id, String defaultText) {
        if((id == null) || (id.isEmpty())){
            return null;
        }

        String value;
        try {
            ResourceBundle labels = ResourceBundle.getBundle("common-mfa", ApplicationLocale.getApplicationLocaleForCurrentThread().getLocaleForCurrentRequest());
            value = labels.getString(MFA_QUESTION_PREFIX + id);
            value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
            if (value.isEmpty()) {
                return defaultText;
            }
        }catch(MissingResourceException e){
            return defaultText;
        }
        catch(NullPointerException e){
            return defaultText;
        }
        catch(UnsupportedEncodingException e)
        {
            return defaultText;
        }
        return value;
    }
}
