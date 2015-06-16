/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static com.github.ewbankkit.util.beans.BaseData.stringsCompare;
import static com.github.ewbankkit.util.beans.BaseData.stringsCompareIgnoreCase;

import java.util.Comparator;

public class Comparators {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:39 Comparators.java NSI";

    /**
     * Case-insensitive string comparison function that handles nulls.
     * A null string is less than any non-null string.
     */
    public static final Comparator<String> CASE_INSENSITIVE_STRING_COMPARATOR = new Comparator<String>() {
        /**
         * Compare the two arguments for order.
         */
        public int compare(String o1, String o2) {
            return stringsCompareIgnoreCase(o1, o2);
        }};
    /**
     * Case-sensitive string comparison function that handles nulls.
     * A null string is less than any non-null string.
     */
    public static final Comparator<String> CASE_SENSITIVE_STRING_COMPARATOR = new Comparator<String>() {
        /**
         * Compare the two arguments for order.
         */
        public int compare(String o1, String o2) {
            return stringsCompare(o1, o2);
        }};

    /**
     * Constructor.
     */
    private Comparators() {}

    /**
     * Case-sensitive comparison function that handles nulls.
     * A null object is greater than any non-null object.
     */
    public static <T extends Comparable<T>> Comparator<T> newNullLargestComparator() {
        return new NullComparator<T>(1) {};
    }

    /**
     * Case-sensitive comparison function that handles nulls.
     * A null object is less than any non-null object.
     */
    public static <T extends Comparable<T>> Comparator<T> newNullSmallestComparator() {
        return new NullComparator<T>(-1) {};
    }

    /**
     * Case-sensitive comparison function that does not allow nulls.
     */
    public static <T extends Comparable<T>> Comparator<T> newSimpleComparator() {
        return new Comparator<T>() {
            public int compare(T t1, T t2) {
                if ((t1 == null) || (t2 == null)) {
                    throw new IllegalArgumentException();
                }
                return t1.compareTo(t2);
            }};
    }

    /**
     * Case-sensitive comparison function that handles nulls.
     */
    private static abstract class NullComparator<T extends Comparable<T>> implements Comparator<T> {
        private final int nullComparison;

        protected NullComparator(int nullComparison) {
            this.nullComparison = nullComparison;
        }

        public int compare(T o1, T o2) {
            if (o1 == null) {
                return (o2 == null) ? 0 : nullComparison;
            }
            if (o2 == null) {
                // From the test above we know s1 != null.
                return -nullComparison;
            }
            return o1.compareTo(o2);
        }
    }
}
