/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

import static com.netsol.adagent.util.beans.BaseData.stringIsNotBlank;
import static com.netsol.adagent.util.beans.BaseData.stringsCompare;
import static com.netsol.adagent.util.beans.BaseData.stringsEqual;
import static com.netsol.adagent.util.beans.BaseData.stringsEqualIgnoreCase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.netsol.adagent.util.Comparators;
import com.netsol.adagent.util.F1;
import com.netsol.adagent.util.Factory;

/**
 * Represents a URL query string.
 */
public class QueryString {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:35 QueryString.java NSI";

    private static final NameMatcher<String> CASE_INSENSITIVE_NAME_MATCHER = new NameMatcher<String>() {
        public boolean matches(String string, String parameterName) {
            return stringsEqualIgnoreCase(string, parameterName);
        }
    };
    private static final Stringizer DEFAULT_STRINGIZER = new Stringizer() {
        public String toString(CharSequence charSequence) {
            return (charSequence == null) ? null : charSequence.toString();
        }
    };
    private static final NameMatcher<Pattern> REGEX_NAME_MATCHER = new NameMatcher<Pattern>() {
        public boolean matches(Pattern pattern, String parameterName) {
            return pattern.matcher(parameterName).matches();
        }
    };
    private static final ListHandler SORTED_LIST_HANDLER = new ListHandler() {
        public void add(List<String> list, String value) {
            int index = Collections.binarySearch(list, value, Comparators.CASE_SENSITIVE_STRING_COMPARATOR);
            if (index < 0) {
                list.add(-index - 1, value);
            }
            else {
                list.add(index, value);
            }
        }

        public List<String> newInstance() {
            return new LinkedList<String>();
        }
    };
    private static final ListHandler UNSORTED_LIST_HANDLER = new ListHandler() {
        public void add(List<String> list, String value) {
            list.add(value);
        }

        public List<String> newInstance() {
            return new ArrayList<String>();
        }
    };

    // Use a TreeMap to guarantee that the keys are in ascending order.
    private final Map<String, List<String>> canonicalParameterMap = new TreeMap<String, List<String>>(Comparators.CASE_SENSITIVE_STRING_COMPARATOR);
    private final Map<String, List<String>> orderPreservingParameterMap = new LinkedHashMap<String, List<String>>();

    /**
     * Parse an HTTP query string.
     * The query string is assumed NOT to be URL encoded.
     */
    public static QueryString fromString(String s) {
        return fromString(s, true);
    }

    /**
     * Parse an HTTP query string.
     * The query string is assumed NOT to be URL encoded.
     */
    public static QueryString fromString(String s, boolean semicolonAsSeparator) {
        return fromString(s, semicolonAsSeparator, DEFAULT_STRINGIZER);
    }

    /**
     * Parse an HTTP query string.
     * The query string is assumed to be URL encoded.
     */
    public static QueryString fromUrlEncodedString(String s, String characterEncoding) {
        return fromUrlEncodedString(s, characterEncoding, true);
    }

    /**
     * Parse an HTTP query string.
     * The query string is assumed to be URL encoded.
     */
    public static QueryString fromUrlEncodedString(String s, String characterEncoding, boolean semicolonAsSeparator) {
        return fromString(s, semicolonAsSeparator, new UrlDecodingStringizer(characterEncoding));
    }

    /**
     * Add a parameter to this query string.
     */
    public QueryString add(CharSequence parameterName, CharSequence parameterValue) {
        add(parameterName, parameterValue, DEFAULT_STRINGIZER, false);

        return this;
    }

    /**
     * Add parameters to this query string.
     */
    public QueryString addMulti(CharSequence parameterName, Iterable<? extends CharSequence> parameterValues) {
        for (CharSequence parameterValue : parameterValues) {
            add(parameterName, parameterValue);
        }

        return this;
    }

    /**
     * Returns true iff this query string contains the specified query string.
     */
    public boolean contains(QueryString queryString) {
        return contains(queryString, Comparators.CASE_SENSITIVE_STRING_COMPARATOR);
    }

    /**
     * Returns true iff this query string contains the specified query string.
     */
    public boolean contains(QueryString queryString, Comparator<String> valueComparator) {
        if (queryString == null) {
            return false;
        }

        Iterator<Map.Entry<String, List<String>>> containerEntries = canonicalParameterMap.entrySet().iterator();
        Iterator<Map.Entry<String, List<String>>> containedEntries = queryString.canonicalParameterMap.entrySet().iterator();
        nextContainedEntry:
        while (containedEntries.hasNext()) {
            Map.Entry<String, List<String>> containedEntry = containedEntries.next();
            while (containerEntries.hasNext()) {
                Map.Entry<String, List<String>> containerEntry = containerEntries.next();
                int comparison = stringsCompare(containedEntry.getKey(), containerEntry.getKey());
                if (comparison == 0) {
                    // Parameter names match.
                    // Check parameter values.
                    Iterator<String> containedValues = containedEntry.getValue().iterator();
                    Iterator<String> containerValues = containerEntry.getValue().iterator();
                    nextContainedValue:
                    while (containedValues.hasNext()) {
                        String containedValue = containedValues.next();
                        while (containerValues.hasNext()) {
                            String containerValue = containerValues.next();
                            comparison = valueComparator.compare(containedValue, containerValue);
                            if (comparison == 0) {
                                continue nextContainedValue;
                            }
                            else if (comparison < 0) {
                                return false;
                            }
                        }
                        return false;
                    }
                    continue nextContainedEntry;
                }
                else if (comparison < 0) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Return the first parameter value for the specified parameter name.
     */
    public String getFirst(String parameterName) {
        List<String> parameterValues = orderPreservingParameterMap.get(parameterName);
        if (parameterValues == null) {
            return null;
        }
        return parameterValues.get(0);
    }

    /**
     * Return the last parameter value for the specified parameter name.
     */
    public String getLast(String parameterName) {
        List<String> parameterValues = orderPreservingParameterMap.get(parameterName);
        if (parameterValues == null) {
            return null;
        }
        return parameterValues.get(parameterValues.size() - 1);
    }

    /**
     * Return the parameter names.
     */
    public Collection<String> getParameterNames() {
        return new ArrayList<String>(orderPreservingParameterMap.keySet());
    }

    /**
     * Merge a query string into this query string.
     * Duplicate parameter values are ignored.
     */
    public QueryString merge(QueryString queryString) {
        if (queryString != null) {
            for (Map.Entry<String, List<String>> mapEntry : queryString.orderPreservingParameterMap.entrySet()) {
                String k = mapEntry.getKey();
                for (String v : mapEntry.getValue()) {
                    add(k, v, DEFAULT_STRINGIZER, true);
                }
            }
        }

        return this;
    }

    /**
     * Remove the specified parameter.
     */
    public QueryString remove(String parameterName) {
        canonicalParameterMap.remove(parameterName);
        orderPreservingParameterMap.remove(parameterName);

        return this;
    }

    /**
     * Remove the specified parameters.
     */
    public QueryString remove(Iterable<String> parameterNames) {
        if (parameterNames != null) {
            for (String parameterName : parameterNames) {
                remove(parameterName);
            }
        }

        return this;
    }

    /**
     * Remove the specified parameter, ignoring case.
     */
    public QueryString removeIgnoreCase(String parameterName) {
        return remove(parameterName, CASE_INSENSITIVE_NAME_MATCHER);
    }

    /**
     * Remove the specified parameters, ignoring case.
     */
    public QueryString removeIgnoreCase(Iterable<String> parameterNames) {
        return remove(parameterNames, CASE_INSENSITIVE_NAME_MATCHER);
    }

    /**
     * Remove the specified parameter.
     */
    public QueryString removeRegex(Pattern pattern) {
        return remove(pattern, REGEX_NAME_MATCHER);
    }

    /**
     * Remove the specified parameters.
     */
    public QueryString removeRegex(Iterable<Pattern> patterns) {
        return remove(patterns, REGEX_NAME_MATCHER);
    }

    /**
     * Transform the parameter names.
     */
    public QueryString transform(F1<String, String> f1) {
        QueryString queryString = new QueryString();
        for (Map.Entry<String, List<String>> mapEntry : orderPreservingParameterMap.entrySet()) {
            String k = null;
            try {
                k = f1.apply(mapEntry.getKey());
            }
            catch (Exception ex) {
                k = mapEntry.getKey();
            }
            for (String v : mapEntry.getValue()) {
                queryString.add(k, v, DEFAULT_STRINGIZER, true);
            }
        }
        return queryString;
    }

    /**
     * Return a deep copy of this object.
     */
    @Override
    public Object clone() {
        QueryString queryString = new QueryString();
        copy(queryString.orderPreservingParameterMap, orderPreservingParameterMap, UNSORTED_LIST_HANDLER);
        copy(queryString.canonicalParameterMap, canonicalParameterMap, SORTED_LIST_HANDLER);

        return queryString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryString)) {
            return false;
        }
        QueryString rhs = (QueryString)o;
        // Both the parameter map and the lists of parameter values within the canonical map are guaranteed to be in ascending order.
        // Two query strings are equal if the string representations of their canonical parameter maps are equal.
        return stringsEqual(toString(canonicalParameterMap), toString(rhs.canonicalParameterMap));
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (Map.Entry<String, List<String>> parameter : canonicalParameterMap.entrySet()) {
            result = 31 * result + parameter.hashCode();
        }
        return result;
    }

    /**
     * Return a string representation of this object.
     * The string is NOT URL encoded.
     */
    @Override
    public String toString() {
        return toString(orderPreservingParameterMap);
    }

    /**
     * Return a string representation of this object.
     * The string is URL encoded.
     */
    public String toUrlEncodedString(String characterEncoding) {
        return toString(orderPreservingParameterMap, new UrlEncodingStringizer(characterEncoding));
    }

    /**
     * Add a parameter to the query string.
     */
    private void add(CharSequence parameterName, CharSequence parameterValue, Stringizer toInternal, boolean ignoreDuplicateValues) {
        String k = toInternal.toString(parameterName);
        String v = toInternal.toString(parameterValue);
        // There is no concept of a null parameter name.
        if (k == null) {
            return;
        }
        if (ignoreDuplicateValues) {
            List<String> values = canonicalParameterMap.get(k);
            if ((values != null) && Collections.binarySearch(values, v, Comparators.CASE_SENSITIVE_STRING_COMPARATOR) >= 0) {
                return;
            }
        }
        add(orderPreservingParameterMap, k, v, UNSORTED_LIST_HANDLER);
        add(canonicalParameterMap, k, v, SORTED_LIST_HANDLER);
    }

    /**
     * Add the specified key and value to the parameter map.
     */
    private static List<String> add(Map<String, List<String>> parameterMap, String k, String v, ListHandler listHandler) {
        List<String> parameterValues = parameterMap.get(k);
        if (parameterValues == null) {
            parameterValues = listHandler.newInstance();
            parameterMap.put(k, parameterValues);
        }
        listHandler.add(parameterValues, v);

        return parameterValues;
    }

    /**
     * Copy all keys and values form one parameter map to another.
     */
    private static void copy(Map<String, List<String>> destination, Map<String, List<String>> source, ListHandler listHandler) {
        for (Map.Entry<String, List<String>> entry : source.entrySet()) {
            for (String v : entry.getValue()) {
                add(destination, entry.getKey(), v, listHandler);
            }
        }
    }

    /**
     * Parse an HTTP query string.
     */
    private static QueryString fromString(String s, boolean semicolonAsSeparator, Stringizer toInternal) {
        QueryString queryString = new QueryString();
        if (stringIsNotBlank(s)) {
            // Distinguish between empty values and null values.
            CharSequence parameterName = null;
            CharSequence parameterValue = null;
            StringBuilder sbParameterName = new StringBuilder();
            StringBuilder sbParameterValue = new StringBuilder();
            StringBuilder sb = sbParameterName;

            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (ch == '=') {
                    sb = sbParameterValue;
                    sb.setLength(0);
                    parameterName = sbParameterName;
                    parameterValue = sbParameterValue;
                }
                else if ((ch == '&') || (semicolonAsSeparator && (ch == ';'))) {
                    queryString.add(parameterName, parameterValue, toInternal, false);
                    sbParameterName.setLength(0);
                    sbParameterValue.setLength(0);
                    sb = sbParameterName;
                    parameterName = null;
                    parameterValue = null;
                }
                else {
                    if ((parameterName == null) && (sb == sbParameterName)) {
                        parameterName = sbParameterName;
                    }
                    sb.append(ch);
                }
            }
            queryString.add(parameterName, parameterValue, toInternal, false);
        }

        return queryString;
    }

    /**
     * Remove all parameters which match the specified value.
     */
    private <T> QueryString remove(T t, NameMatcher<T> nameMatcher) {
        return remove(Collections.singleton(t), nameMatcher);
    }

    /**
     * Remove all parameters which match the specified values.
     */
    private <T> QueryString remove(Iterable<T> ts, NameMatcher<T> nameMatcher) {
        if (ts != null) {
            Collection<String> parameterNamesToRemove = new HashSet<String>();
            for (Map.Entry <String, List<String>> entry : canonicalParameterMap.entrySet()) {
                String parameterName = entry.getKey();
                for (T t : ts) {
                    if (nameMatcher.matches(t, parameterName)) {
                        parameterNamesToRemove.add(parameterName);
                    }
                }
            }
            remove(parameterNamesToRemove);
        }

        return this;
    }

    /**
     * Return a string representation of the specified parameter map.
     * The string is NOT URL encoded.
     */
    private static String toString(Map<String, List<String>> parameterMap) {
        return toString(parameterMap, DEFAULT_STRINGIZER);
    }

    /**
     * Return a string representation of the specified parameter map.
     */
    private static String toString(Map<String, List<String>> parameterMap, Stringizer toExternal) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> parameter : parameterMap.entrySet()) {
            String parameterName = parameter.getKey();
            List<String> parameterValues = parameter.getValue();
            if (parameterValues != null) {
                for (String parameterValue : parameterValues) {
                    if (parameterName != null) {
                        sb.append(toExternal.toString(parameterName));
                    }
                    if (parameterValue != null) {
                        sb.append('=').append(toExternal.toString(parameterValue));
                    }
                    sb.append('&');
                }
                int length = sb.length();
                if ((length > 0) && (sb.charAt(length - 1) == '&')) {
                    sb.setLength(length - 1);
                }
            }
            else {
                // The map contains no empty parameters.
                sb.append(toExternal.toString(parameterName));
            }
            sb.append('&');
        }
        int length = sb.length();
        if ((length > 0) && (sb.charAt(length - 1) == '&')) {
            sb.setLength(length - 1);
        }

        return sb.toString();
    }

    private static interface NameMatcher<T> {
        public abstract boolean matches(T t, String parameterName);
    }

    private static interface ListHandler extends Factory<List<String>> {
        public abstract void add(List<String> list, String value);
    }

    private static interface Stringizer {
        public abstract String toString(CharSequence charSequence);
    }

    private static abstract class BaseCodingStringizer implements Stringizer {
        protected final String characterEncoding;

        /**
         * Constructor.
         */
        protected BaseCodingStringizer(String characterEncoding) {
            this.characterEncoding = characterEncoding;
        }
    }

    private static class UrlDecodingStringizer extends BaseCodingStringizer {
        /**
         * Constructor.
         */
        public UrlDecodingStringizer(String characterEncoding) {
            super(characterEncoding);
        }

        public String toString(CharSequence charSequence) {
            if (charSequence != null) {
                try {
                    return URLDecoder.decode(charSequence.toString(), characterEncoding);
                }
                catch (UnsupportedEncodingException ex) {}
            }

            return null;
        }
    }

    private static class UrlEncodingStringizer extends BaseCodingStringizer {
        /**
         * Constructor.
         */
        public UrlEncodingStringizer(String characterEncoding) {
            super(characterEncoding);
        }

        public String toString(CharSequence charSequence) {
            if (charSequence != null) {
                try {
                    return URLEncoder.encode(charSequence.toString(), characterEncoding);
                }
                catch (UnsupportedEncodingException ex) {}
            }

            return null;
        }
    }
}
