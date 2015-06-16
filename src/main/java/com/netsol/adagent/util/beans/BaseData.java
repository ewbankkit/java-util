/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.netsol.adagent.util.Comparators;
import com.netsol.adagent.util.F1;
import com.netsol.adagent.util.Factories;
import com.netsol.adagent.util.Factory;
import com.netsol.adagent.util.Predicate;

public abstract class BaseData {
    /**
     * Return a string representation of this object.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Return true if all elements of the iterable match the predicate.
     */
    public static <T> boolean all(Iterable<T> ts, Predicate<T> predicate) {
        for (T t : ts) {
            if (!predicate.apply(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if any elements of the iterable match the predicate.
     */
    public static <T> boolean any(Iterable<T> ts, Predicate<T> predicate) {
        for (T t : ts) {
            if (predicate.apply(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the specified array contain the specified value?
     */
    public static boolean arrayContains(int[] array, int value) {
        if (arrayIsNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Is the specified array empty?
     */
    public static boolean arrayIsEmpty(byte[] a) {
        return (a == null) || (a.length == 0);
    }

    /**
     * Is the specified array empty?
     */
    public static boolean arrayIsEmpty(int[] a) {
        return (a == null) || (a.length == 0);
    }

    /**
     * Is the specified array empty?
     */
    public static boolean arrayIsEmpty(long[] a) {
        return (a == null) || (a.length == 0);
    }

    /**
     * Is the specified array empty?
     */
    public static boolean arrayIsEmpty(Object[] a) {
        return (a == null) || (a.length == 0);
    }

    /**
     * Is the specified array not empty?
     */
    public static boolean arrayIsNotEmpty(byte[] a) {
        return !arrayIsEmpty(a);
    }

    /**
     * Is the specified array not empty?
     */
    public static boolean arrayIsNotEmpty(int[] a) {
        return !arrayIsEmpty(a);
    }

    /**
     * Is the specified array not empty?
     */
    public static boolean arrayIsNotEmpty(long[] a) {
        return !arrayIsEmpty(a);
    }

    /**
     * Is the specified array not empty?
     */
    public static boolean arrayIsNotEmpty(Object[] a) {
        return !arrayIsEmpty(a);
    }

    /**
     * Parse the output of Arrays.toString.
     */
    public static String[] arrayFromString(String s) {
        List<String> list = listFromString(s, new F1<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s;
            }});
        return (list == null) ? null : list.toArray(new String[0]);
    }

    /**
     * Chunk up a list.
     */
    public static <T> List<List<T>> chunk(Iterable<T> ts, int chunkSize) {
        if (ts == null) {
            return null;
        }

        List<List<T>> chunks = new ArrayList<List<T>>();
        List<T> chunk = null;
        int chunkLength = 0;
        for (T t : ts) {
        	if (chunkLength == chunkSize) {
        		chunk = null;
        	}
        	if (chunk == null) {
        		chunk = new ArrayList<T>();
        		chunkLength = 0;
        		chunks.add(chunk);
        	}
        	chunk.add(t);
        	chunkLength++;
        }
        return chunks;
    }

    /**
     * Returns the first non-null value in the list, or null if there are no non-null values.
     */
    public static <T> T coalesce(T... objects) {
        Option<T> tMaybe = find(toIterable(objects), new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return t != null;
            }});
        return tMaybe.isNone() ? null : tMaybe.getValue();
    }

    /**
     * Does the specified collection contain the specified int?
     */
    public static boolean collectionContains(Collection<?> c, int i) {
        return collectionContains(c, Integer.valueOf(i));
    }

    /**
     * Does the specified collection contain the specified long?
     */
    public static boolean collectionContains(Collection<?> c, long l) {
        return collectionContains(c, Long.valueOf(l));
    }

    /**
     * Does the specified collection contain the specified object?
     */
    public static boolean collectionContains(Collection<?> c, Object o) {
        return collectionIsEmpty(c) ? false : c.contains(o);
    }

    /**
     * Is the specified collection empty?
     */
    public static boolean collectionIsEmpty(Collection<?> c) {
        return (c == null) || c.isEmpty();
    }

    /**
     * Is the specified collection not empty?
     */
    public static boolean collectionIsNotEmpty(Collection<?> c) {
        return !collectionIsEmpty(c);
    }

    /**
     * Return an empty iterable object.
     */
    public static <T> Iterable<T> emptyIterable() {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    public boolean hasNext() {
                        return false;
                    }

                    public T next() {
                        throw new NoSuchElementException();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }};
            }};
    }

    /**
     * Test the existence in the sequence of an element that satisfies the predicate p.
     */
    public static <T> boolean exists(Iterable<T> ts, Predicate<? super T> p) {
        if (ts != null) {
            for (T t : ts) {
                if (p.apply(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filter one list to another.
     */
    public static <T> List<T> filter(Iterable<T> ts, Predicate<? super T> p) {
        Factory<List<T>> listFactory = Factories.newArrayListFactory();
        return filter(ts, p, listFactory);
    }

    /**
     * Filter one list to another.
     */
    public static <T> List<T> filter(Iterable<T> ts, Predicate<? super T> p, Factory<List<T>> listFactory) {
        if (ts == null) {
            return null;
        }

        List<T> list = listFactory.newInstance();
        for (T t : ts) {
            if (p.apply(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Search for an element that matches the conditions defined by the specified predicate and return the first occurrence.
     */
    public static <T> Option<T> find(Iterable<T> ts, Predicate<T> predicate) {
        for (T t : ts) {
            if (predicate.apply(t)) {
                return Option.some(t);
            }
        }
        return Option.none();
    }

    /**
     * Return the first element of a collection or NULL if the collection is empty,
     */
    public static <T> T firstElement(Collection<T> c) {
        return collectionIsEmpty(c) ? null : Collections.enumeration(c).nextElement();
    }

    /**
     * Return the first non-blank value in the list, or null if there are none.
     */
    public static <T extends CharSequence> T firstNonBlank(T... ts) {
        Option<T> tMaybe = find(toIterable(ts), new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return stringIsNotBlank(t);
            }});
        return tMaybe.isNone() ? null : tMaybe.getValue();
    }

    /**
     * Return the first positive value in the list, or 0 if there are none.
     */
    public static int firstPositive(Integer... is) {
        Option<Integer> iMaybe = find(toIterable(is), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer i) {
                return (i != null) && (i.intValue() > 0);
            }});
        return iMaybe.isNone() ? 0 : iMaybe.getValue().intValue();
    }

    /**
     * Return the first positive value in the list, or 0 if there are none.
     */
    public static long firstPositive(Long... ls) {
        Option<Long> lMaybe = find(toIterable(ls), new Predicate<Long>() {
            @Override
            public boolean apply(Long l) {
                return (l != null) && (l.longValue() > 0L);
            }});
        return lMaybe.isNone() ? 0L : lMaybe.getValue().longValue();
    }

    /**
     * Perform the specified action on each element.
     */
    public static <T> void forEach(Enumeration<T> enumeration, F1<? super T, Void> f1) throws Exception {
        forEach(toIterable(enumeration), f1);
    }

    /**
     * Perform the specified action on each element.
     */
    public static <T> void forEach(Iterable<T> ts, F1<? super T, Void> f1) throws Exception {
        if (ts != null) {
            for (T t : ts) {
                f1.apply(t);
            }
        }
    }

    /**
     * Perform the specified action on each element.
     */
    public static <T> void forEach(T[] array, F1<? super T, Void> f1) throws Exception {
        forEach(toIterable(array), f1);
    }

    public static <T extends Enum<T>> T getEnumValue(Class<T> classOfT, String s) {
        try {
            return Enum.valueOf(classOfT, s.toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static <T extends Comparable<T>> boolean greaterThan(T t1, T t2) {
        Comparator<T> comparator = Comparators.newSimpleComparator();
        return greaterThan(t1, t2, comparator);
    }

    public static <T> boolean greaterThan(T t1, T t2, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException();
        }
        return comparator.compare(t1, t2) > 0;
    }

    public static <T extends Comparable<T>> boolean greaterThanOrEquals(T t1, T t2) {
        return objectsEqual(t1, t2) || greaterThan(t1, t2);
    }

    public static <T> boolean greaterThanOrEquals(T t1, T t2, Comparator<T> comparator) {
        return objectsEqual(t1, t2) || greaterThan(t1, t2, comparator);
    }

    public static <T extends Comparable<T>> boolean lessThan(T t1, T t2) {
        Comparator<T> comparator = Comparators.newSimpleComparator();
        return lessThan(t1, t2, comparator);
    }

    public static <T> boolean lessThan(T t1, T t2, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException();
        }
        return comparator.compare(t1, t2) < 0;
    }

    public static <T extends Comparable<T>> boolean lessThanOrEquals(T t1, T t2) {
        return objectsEqual(t1, t2) || lessThan(t1, t2);
    }

    public static <T> boolean lessThanOrEquals(T t1, T t2, Comparator<T> comparator) {
        return objectsEqual(t1, t2) || lessThan(t1, t2, comparator);
    }

    /**
     * Parse the output of Arrays.toString.
     */
    public static <T> List<T> listFromString(String s, F1<String, T> f1) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        if (s.equals("null")) {
            return null;
        }
        int length = s.length();
        if ((length <= 1) || (s.charAt(0) != '[') || (s.charAt(length - 1) != ']')) {
            throw new IllegalArgumentException();
        }

        List<T> list = new ArrayList<T>();
        StringBuilder sb = new StringBuilder();
        char previousChar = 0;
        // Skip leading '[' and trailing ']';
        for (int i = 1; i < length - 1; i++) {
            char c = s.charAt(i);
            switch (c) {
            case ',':
                if (previousChar == ',') {
                    sb.append(previousChar);
                }
                break;

            case ' ':
                if (previousChar == ',') {
                    try {
                        list.add(f1.apply(sb.toString()));
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    sb.setLength(0);
                    previousChar = 0;
                }
                else {
                    sb.append(c);
                }
                break;

            default:
                if (previousChar == ',') {
                    sb.append(previousChar);
                }
                sb.append(c);
                break;
            }
            previousChar = c;
        }
        if (previousChar == ',') {
            sb.append(previousChar);
        }
        if (sb.length() != 0) {
            try {
                list.add(f1.apply(sb.toString()));
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return list;
    }

    /**
     * Is the specified map empty?
     */
    public static boolean mapIsEmpty(Map<?, ?> m) {
        return (m == null) || m.isEmpty();
    }

    /**
     * Is the specified map not empty?
     */
    public static boolean mapIsNotEmpty(Map<?, ?> m) {
        return !mapIsEmpty(m);
    }

    /**
     * Return the larger of two comparable values.
     */
    public static <T extends Comparable<T>> T max(T t1, T t2) {
        if ((t1 == null) || (t2 == null)) {
            return null;
        }
        return greaterThanOrEquals(t1, t2) ? t1 : t2;
    }

    /**
     * Return the smaller of two comparable values.
     */
    public static <T extends Comparable<T>> T min(T t1, T t2) {
        if ((t1 == null) || (t2 == null)) {
            return null;
        }
        return lessThanOrEquals(t1, t2) ? t1 : t2;
    }

    /**
     * Are the objects equal?
     * Two null objects are considered equal.
     */
    public static boolean objectsEqual(Object o1, Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }

    /**
     * Partition a list.
     */
    public static <T> Pair<List<T>, List<T>> partition(Iterable<T> ts, Predicate<? super T> p) {
        Factory<List<T>> listFactory = Factories.newArrayListFactory();
        return partition(ts, p, listFactory);
    }

    /**
     * Partition a list.
     */
    public static <T> Pair<List<T>, List<T>> partition(Iterable<T> ts, Predicate<? super T> p, Factory<List<T>> listFactory) {
        if (ts == null) {
            return null;
        }

        List<T> listIn = listFactory.newInstance();
        List<T> listOut = listFactory.newInstance();
        for (T t : ts) {
            if (p.apply(t)) {
                listIn.add(t);
            }
            else {
                listOut.add(t);
            }
        }
        return Pair.from(listIn, listOut);
    }

    /**
     * Does the string contain the specified character?
     */
    public static boolean stringContains(CharSequence s, char ch) {
        int length;
        if ((s == null) || (length = s.length()) == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is the specified string blank?
     */
    public static boolean stringIsBlank(CharSequence s) {
        int length;
        if ((s == null) || (length = s.length()) == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is the specified string empty?
     */
    public static boolean stringIsEmpty(CharSequence s) {
        return (s == null) || (s.length() == 0);
    }

    /**
     * Is the specified string not blank?
     */
    public static boolean stringIsNotBlank(CharSequence s) {
        return !stringIsBlank(s);
    }

    /**
     * Is the specified string not empty?
     */
    public static boolean stringIsNotEmpty(CharSequence s) {
        return !stringIsEmpty(s);
    }

    /**
     * Compare two strings lexicographically.
     * A null string is less than any non-null string.
     */
    public static int stringsCompare(String s1, String s2) {
        if (s1 == null) {
            return (s2 == null) ? 0 : -1;
        }
        if (s2 == null) {
            // From the test above we know s1 != null.
            return 1;
        }
        return s1.compareTo(s2);
    }

    /**
     * Compare two strings lexicographically (ignoring case).
     * A null string is less than any non-null string.
     */
    public static int stringsCompareIgnoreCase(String s1, String s2) {
        if (s1 == null) {
            return (s2 == null) ? 0 : -1;
        }
        if (s2 == null) {
            // From the test above we know s1 != null.
            return 1;
        }
        return s1.compareToIgnoreCase(s2);
    }

    /**
     * Are the strings equal?
     * Two null strings are considered equal.
     */
    public static boolean stringsEqual(String s1, String s2) {
        return objectsEqual(s1, s2);
    }

    /**
     * Are the strings equal (ignoring case)?
     * Two null strings are considered equal.
     */
    public static boolean stringsEqualIgnoreCase(String s1, String s2) {
        return (s1 == null) ? (s2 == null) : s1.equalsIgnoreCase(s2);
    }

    public static BigDecimal toBigDecimal(double d, int scale) {
        return new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal toBigDecimal(Number number, int scale) {
        if (number == null) {
            return null;
        }
        return toBigDecimal(number.doubleValue(), scale);
    }

    public static Double toDouble(Number number) {
        if (number == null) {
            return null;
        }
        return Double.valueOf(number.doubleValue());
    }

    /**
     * Return a hex string for the specified byte array.
     */
    public static String toHexString(byte[] array) {
        if (array == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            String hexString = Integer.toHexString((int)b & 0xFF);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static Integer toInteger(long l) {
        return (l > Integer.MAX_VALUE) ? null : Integer.valueOf((int)l);
    }

    public static Integer toInteger(Number number) {
        return (number == null) ? null : toInteger(number.longValue());
    }

    public static Long toLong(Number number) {
        return (number == null) ? null : Long.valueOf(number.longValue());
    }

    /**
     * Return an iterable over the specified character sequence.
     */
    public static Iterable<Character> toIterable(final CharSequence charSequence) {
        if (stringIsEmpty(charSequence)) {
            return emptyIterable();
        }
        return new Iterable<Character>() {
            public Iterator<Character> iterator() {
                return new Iterator<Character>() {
                    private int index;
                    private final int length = charSequence.length();

                    public boolean hasNext() {
                        return (index < length);
                    }

                    public Character next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Character.valueOf(charSequence.charAt(index++));
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }};
            }};
    }

    /**
     * Return an iterable over the specified enumeration.
     */
    public static <T> Iterable<T> toIterable(final Enumeration<T> enumeration) {
        if (enumeration == null) {
            return emptyIterable();
        }
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    public boolean hasNext() {
                        return enumeration.hasMoreElements();
                    }

                    public T next() {
                        return enumeration.nextElement();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }};
            }};
    }

    /**
     * Return an iterable over the specified array.
     */
    public static Iterable<Integer> toIterable(int[] array) {
        if (arrayIsEmpty(array)) {
            return emptyIterable();
        }
        Integer[] wrapperArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
          wrapperArray[i] = Integer.valueOf(array[i]);
        }
        return toIterable(wrapperArray);
    }

    /**
     * Return an iterable over the specified array.
     */
    public static Iterable<Long> toIterable(long[] array) {
        if (arrayIsEmpty(array)) {
            return emptyIterable();
        }
        Long[] wrapperArray = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
          wrapperArray[i] = Long.valueOf(array[i]);
        }
        return toIterable(wrapperArray);
    }

    /**
     * Return an iterable over the specified array.
     */
    public static <T> Iterable<T> toIterable(final T[] array) {
        if (arrayIsEmpty(array)) {
            return emptyIterable();
        }
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int index;

                    public boolean hasNext() {
                        return (index < array.length);
                    }

                    public T next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return array[index++];
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }};
            }};
    }

    /**
     * Return a list of pairs from an array of arrays.
     */
    public static <T> List<Pair<T, T>> toListOfPairs(T[][] arrays) {
        if (arrays == null) {
            return null;
        }

        List<Pair<T, T>> listOfPairs = new ArrayList<Pair<T, T>>(arrays.length);
        for (T[] array : arrays) {
            if (arrayIsEmpty(array)) {
                continue;
            }
            listOfPairs.add(Pair.from(array[0], array[1]));
        }
        return listOfPairs;
    }

    /**
     * Return a list of pairs from a map.
     */
    public static <K, V> List<Pair<K, V>> toListOfPairs(Map<K, V> map) {
        if (map == null) {
            return null;
        }

        List<Pair<K, V>> listOfPairs = new ArrayList<Pair<K, V>>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            listOfPairs.add(Pair.from(entry.getKey(), entry.getValue()));
        }
        return listOfPairs;
    }

    /**
     * Return a string representation of the specified object.
     */
    public static String toString(Object object) {
        return (object == null) ? null : new ReflectiveRepresentation(object).toString();
    }

    /**
     * Transform one list to another.
     */
    public static <A, B> List<B> transform(Iterable<A> as, F1<? super A, ? extends B> f1) throws Exception {
        Factory<List<B>> listFactory = Factories.newArrayListFactory();
        return transform(as, f1, listFactory);
    }

    /**
     * Transform one list to another.
     */
    public static <A, B> List<B> transform(Iterable<A> as, F1<? super A, ? extends B> f1, Factory<List<B>> listFactory) throws Exception {
        if (as == null) {
            return null;
        }

        List<B> list = listFactory.newInstance();
        for (A a : as) {
            list.add(f1.apply(a));
        }
        return list;
    }

    public static boolean unbox(Boolean b) {
        return (b == null) ? false : b.booleanValue();
    }

    public static int unbox(Integer i) {
        return (i == null) ? 0 : i.intValue();
    }

    public static long unbox(Long l) {
        return (l == null) ? 0L : l.longValue();
    }

    public static double unbox(Double d) {
        return (d == null) ? 0D : d.doubleValue();
    }

    public static float unbox(Float f) {
        return (f == null) ? 0F : f.floatValue();
    }
}
