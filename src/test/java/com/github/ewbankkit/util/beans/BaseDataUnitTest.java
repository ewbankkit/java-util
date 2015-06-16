/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.github.ewbankkit.util.F1;
import org.junit.Test;

import com.github.ewbankkit.util.Predicate;

public class BaseDataUnitTest {
    @Test
    public void toIterableTest1() {
        assertFalse(BaseData.toIterable((int[])null).iterator().hasNext());
    }

    @Test
    public void toIterableTest2() {
        assertFalse(BaseData.toIterable((long[])null).iterator().hasNext());
    }

    @Test
    public void toIterableTest3() {
        assertFalse(BaseData.toIterable((String[])null).iterator().hasNext());
    }

    @Test
    public void toIterableTest4() {
        Iterator<Integer> iterator = BaseData.toIterable(new int[0]).iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest5() {
        Iterator<Long> iterator = BaseData.toIterable(new long[0]).iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest6() {
        Iterator<Object> iterator = BaseData.toIterable(new Object[0]).iterator();
        assertFalse(iterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void toIterableTest7() {
        Iterator<Integer> iterator = BaseData.toIterable(new int[0]).iterator();
        iterator.next();
    }

    @Test(expected=NoSuchElementException.class)
    public void toIterableTest8() {
        Iterator<Long> iterator = BaseData.toIterable(new long[0]).iterator();
        iterator.next();
    }

    @Test(expected=NoSuchElementException.class)
    public void toIterableTest9() {
        Iterator<Object> iterator = BaseData.toIterable(new Object[0]).iterator();
        iterator.next();
    }

    @Test
    public void toIterableTest10() {
        Iterator<Integer> iterator = BaseData.toIterable(new int[] {1, 2}).iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest11() {
        Iterator<Long> iterator = BaseData.toIterable(new long[] {Long.MIN_VALUE, Long.MAX_VALUE}).iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Long.valueOf(Long.MIN_VALUE), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Long.valueOf(Long.MAX_VALUE), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest12() {
        Date rightNow = new Date();
        Iterator<Object> iterator = BaseData.toIterable(new Object[] {"Hello", rightNow}).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("Hello", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(rightNow, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest13() {
        assertFalse(BaseData.toIterable((CharSequence)null).iterator().hasNext());
    }

    @Test
    public void toIterableTest14() {
        Iterator<Character> iterator = BaseData.toIterable("No").iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Character.valueOf('N'), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Character.valueOf('o'), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toIterableTest15() {
        assertFalse(BaseData.toIterable((Enumeration<String>)null).iterator().hasNext());
    }

    @Test
    public void toIterableTest16() {
        Vector<String> vector = new Vector<String>();
        vector.add("Hello");
        vector.add("Goodbye");
        Iterator<String> iterator = BaseData.toIterable(vector.elements()).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("Hello", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Goodbye", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void mapIsEmptyTest1() {
        assertTrue(BaseData.mapIsEmpty(null));
    }

    @Test
    public void mapIsEmptyTest2() {
        assertTrue(BaseData.mapIsEmpty(Collections.emptyMap()));
    }

    @Test
    public void mapIsEmptyTest3() {
        assertFalse(BaseData.mapIsEmpty(Collections.singletonMap(null, null)));
    }

    @Test
    public void mapIsNotEmptyTest1() {
        assertFalse(BaseData.mapIsNotEmpty(null));
    }

    @Test
    public void mapIsNotEmptyTest2() {
        assertFalse(BaseData.mapIsNotEmpty(Collections.emptyMap()));
    }

    @Test
    public void mapIsNotEmptyTest3() {
        assertTrue(BaseData.mapIsNotEmpty(Collections.singletonMap(null, null)));
    }

    @Test
    public void stringContainsTest1() {
        assertFalse(BaseData.stringContains(null, 'Y'));
    }

    @Test
    public void stringContainsTest2() {
        assertFalse(BaseData.stringContains("", 'Y'));
    }

    @Test
    public void stringContainsTest3() {
        assertTrue(BaseData.stringContains("Oh Yes", 'Y'));
    }

    @Test(expected=IllegalArgumentException.class)
    public void greaterThanTest1() {
        BaseData.greaterThan(null, "Hello");
    }

    @Test(expected=IllegalArgumentException.class)
    public void greaterThanTest2() {
        BaseData.greaterThan("Hello", null);
    }

    @Test
    public void greaterThanTest3() {
        assertTrue(BaseData.greaterThan("ABC", "AAA"));
    }

    @Test
    public void greaterThanOrEqualsTest1() {
        assertTrue(BaseData.greaterThanOrEquals("ABC", "AAA"));
    }

    @Test
    public void greaterThanOrEqualsTest2() {
        assertTrue(BaseData.greaterThanOrEquals("ABC", "ABC"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void lessThanTest1() {
        BaseData.lessThan(null, "Hello");
    }

    @Test(expected=IllegalArgumentException.class)
    public void lessThanTest2() {
        BaseData.lessThan("Hello", null);
    }

    @Test
    public void lessThanTest3() {
        assertFalse(BaseData.lessThan("ABC", "AAA"));
    }

    @Test
    public void lessThanOrEqualsTest1() {
        assertFalse(BaseData.lessThanOrEquals("ABC", "AAA"));
    }

    @Test
    public void lessThanOrEqualsTest2() {
        assertTrue(BaseData.lessThanOrEquals("ABC", "ABC"));
    }

    @Test
    public void maxTest1() {
        Integer i1 = Integer.valueOf(99);
        Integer i2 = Integer.valueOf(999);
        assertEquals(i2, BaseData.max(i1, i2));
    }

    @Test
    public void maxTest2() {
        Integer i1 = null;
        Integer i2 = Integer.valueOf(999);
        assertNull(BaseData.max(i1, i2));
    }

    @Test
    public void minTest1() {
        Integer i1 = Integer.valueOf(99);
        Integer i2 = null;
        assertNull(BaseData.min(i1, i2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void listFromStringTest1() {
        assertNull(BaseData.listFromString(null, new F1<String, Object>() {
            @Override
            public Object apply(String a) throws Exception {
                return null;
            }}));
    }

    @Test
    public void listFromStringTest2() {
        assertNull(BaseData.listFromString("null", new F1<String, Object>() {
            @Override
            public Object apply(String a) throws Exception {
                return null;
            }}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void listFromStringTest3() {
        assertNull(BaseData.listFromString("", new F1<String, Object>() {
            @Override
            public Object apply(String a) throws Exception {
                return null;
            }}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void listFromStringTest4() {
        assertNull(BaseData.listFromString("[XX", new F1<String, Object>() {
            @Override
            public Object apply(String a) throws Exception {
                return null;
            }}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void listFromStringTest5() {
        assertNull(BaseData.listFromString("XX]", new F1<String, Object>() {
            @Override
            public Object apply(String a) throws Exception {
                return null;
            }}));
    }

    @Test
    public void listFromStringTest6() {
        assertTrue(BaseData.listFromString("[]", new F1<String, String>() {
            @Override
            public String apply(String a) throws Exception {
                return null;
            }}).isEmpty());
    }

    @Test
    public void arrayFromStringTest1() {
        assertNull(BaseData.arrayFromString("null"));
    }

    @Test
    public void arrayFromStringTest2() {
        String s = Arrays.toString(new String[] {"AAA"});
        String[] array = BaseData.arrayFromString(s);
        assertEquals(1, array.length);
        assertEquals("AAA", array[0]);
    }

    @Test
    public void arrayFromStringTest3() {
        String s = Arrays.toString(new String[] {"AAA", "BBBB", "CCCCC"});
        String[] array = BaseData.arrayFromString(s);
        assertEquals(3, array.length);
        assertEquals("AAA", array[0]);
        assertEquals("BBBB", array[1]);
        assertEquals("CCCCC", array[2]);
    }

    @Test
    public void arrayFromStringTest4() {
        String s = Arrays.toString(new String[] {"A,AA", "BB  B B", "C,C,,CC  C"});
        String[] array = BaseData.arrayFromString(s);
        assertEquals(3, array.length);
        assertEquals("A,AA", array[0]);
        assertEquals("BB  B B", array[1]);
        assertEquals("C,C,,CC  C", array[2]);
    }

    @Test
    public void arrayFromStringTest6() {
        String s = Arrays.toString(new String[] {"AAA,", "BBBB  ", "CCCC,C"});
        String[] array = BaseData.arrayFromString(s);
        assertEquals(3, array.length);
        assertEquals("AAA,", array[0]);
        assertEquals("BBBB  ", array[1]);
        assertEquals("CCCC,C", array[2]);
    }

    @Test
    public void toListOfPairsTest1() {
        assertNull(BaseData.toListOfPairs((Object[][])null));
    }

    @Test
    public void toListOfPairsTest2() {
        assertTrue(BaseData.toListOfPairs(new String[][] {}).isEmpty());
    }

    @Test
    public void toListOfPairsTest3() {
        List<Pair<String, String>> list = BaseData.toListOfPairs(new String[][] {{"AB", "XY"}, {"C", "Z"}});
        assertEquals(2, list.size());
        assertEquals("AB", list.get(0).getFirst());
        assertEquals("Z", list.get(1).getSecond());
    }

    @Test
    public void toListOfPairsTest4() {
        assertNull(BaseData.toListOfPairs((Map<Object, Object>)null));
    }

    @Test
    public void toListOfPairsTest5() {
        assertTrue(BaseData.toListOfPairs(Collections.emptyMap()).isEmpty());
    }

    @Test
    public void toListOfPairsTest6() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("AB", Integer.valueOf(1));
        map.put("C", Integer.valueOf(-1));
        List<Pair<String, Integer>> list = BaseData.toListOfPairs(map);
        assertEquals(2, list.size());
        assertEquals("AB", list.get(0).getFirst());
        assertEquals(Integer.valueOf(-1), list.get(1).getSecond());
    }

    @Test
    public void coalesceTest1() {
        assertNull(BaseData.coalesce(null, null));
    }

    @Test
    public void coalesceTest2() {
        assertNotNull(BaseData.coalesce(null, ""));
    }

    @Test
    public void coalesceTest3() {
        assertEquals("BOOP", BaseData.coalesce(null, null, "BOOP"));
    }

    @Test
    public void allTest1() {
        assertFalse(BaseData.all(Arrays.asList("", null), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return BaseData.stringIsNotEmpty(s);
            }}));
    }

    @Test
    public void allTest2() {
        assertFalse(BaseData.all(Arrays.asList("", null, "HELLO"), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return BaseData.stringIsNotEmpty(s);
            }}));
    }

    @Test
    public void allTest3() {
        assertTrue(BaseData.all(Arrays.asList("GOODBYE", "HELLO"), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return BaseData.stringIsNotEmpty(s);
            }}));
    }

    @Test
    public void anyTest1() {
        assertFalse(BaseData.any(Arrays.asList("", null), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return BaseData.stringIsNotEmpty(s);
            }}));
    }

    @Test
    public void anyTest2() {
        assertTrue(BaseData.any(Arrays.asList("", null, "HELLO"), new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return BaseData.stringIsNotEmpty(s);
            }}));
    }

    @Test
    public void firstPositiveTest1() {
        assertEquals(2, BaseData.firstPositive(0, -2, 2, 3));
    }

    @Test
    public void firstPositiveTest2() {
        assertEquals(0, BaseData.firstPositive(-2, -1));
    }

    @Test
    public void firstPositiveTest3() {
        assertEquals(2L, BaseData.firstPositive(0L, -2L, 2L, 3L));
    }

    @Test
    public void firstPositiveTest4() {
        assertEquals(0L, BaseData.firstPositive(-2L, -1L));
    }

    @Test
    public void firstNonBlankTest1() {
        assertNull(BaseData.firstNonBlank(" ", null, "", "           "));
    }

    @Test
    public void firstNonBlankTest2() {
        assertEquals("HELLO", BaseData.firstNonBlank(" ", null, "HELLO", "           "));
    }

    @Test
    public void chunkTest1() {
    	assertNull(BaseData.chunk(null, 2));
    }

    @Test
    public void chunkTest2() {
    	assertEquals(1, BaseData.chunk(Arrays.asList("A"), 2).size());
    	assertEquals(1, BaseData.chunk(Arrays.asList("A"), 2).get(0).size());
    }

    @Test
    public void chunkTest3() {
    	assertEquals(1, BaseData.chunk(Arrays.asList("A", "B"), 2).size());
    	assertEquals(2, BaseData.chunk(Arrays.asList("A", "B"), 2).get(0).size());
    	assertEquals("A", BaseData.chunk(Arrays.asList("A", "B"), 2).get(0).get(0));
    }

    @Test
    public void chunkTest4() {
    	assertEquals(2, BaseData.chunk(Arrays.asList("A", "B", "C"), 2).size());
    	assertEquals(2, BaseData.chunk(Arrays.asList("A", "B", "C"), 2).get(0).size());
    	assertEquals("B", BaseData.chunk(Arrays.asList("A", "B", "C"), 2).get(0).get(1));
    	assertEquals(1, BaseData.chunk(Arrays.asList("A", "B", "C"), 2).get(1).size());
    }
}
