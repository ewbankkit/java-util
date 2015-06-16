/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.restclient;

/**
 * Message converter.
 */
public interface MessageConverter {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:27 MessageConverter.java NSI";

    public abstract <T> T fromString(String string, Class<T> classOfT) throws Exception;
    public abstract String getMediaType();
    public abstract String toString(Object object) throws Exception;
}
