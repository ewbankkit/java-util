/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

/* package-private */ abstract class AbstractParamManipulator {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:31 AbstractParamManipulator.java NSI";

    protected static final Strategy DEFAULT_STRATEGY = new Strategy() {
        public QueryString newQueryString(String s) {
            return QueryString.fromString(s);
        }

        public String toString(QueryString queryString) {
            return queryString.toString();
        }
    };

    protected static final Strategy UTF8_URL_ENCODED_STRATEGY = new UrlEncodedStrategy("UTF-8") {};

    protected Strategy getUrlEncodedStrategy(String characterEncoding) {
        return "UTF-8".equalsIgnoreCase(characterEncoding) ?
                UTF8_URL_ENCODED_STRATEGY : new UrlEncodedStrategy(characterEncoding);
    }

    protected static interface Strategy {
        public abstract QueryString newQueryString(String s);
        public abstract String toString(QueryString queryString);
    }

    private static class UrlEncodedStrategy implements Strategy {
        private final String characterEncoding;

        /**
         * Constructor.
         */
        public UrlEncodedStrategy(String characterEncoding) {
            this.characterEncoding = characterEncoding;
        }

        public QueryString newQueryString(String s) {
            return QueryString.fromUrlEncodedString(s, characterEncoding);
        }

        public String toString(QueryString queryString) {
            return queryString.toUrlEncodedString(characterEncoding);
        }
    }
}
