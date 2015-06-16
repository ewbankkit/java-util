/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.netsol.adagent.util.UserAccountUtil;
import com.github.ewbankkit.util.beans.Pair;

public class UserAccountUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:07 UserAccountUtilUnitTest.java NSI";

    @Test
    public void getUserAccountNameAndDomainNameTest1() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName(null);
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest2() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("");
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest3() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("kewbank");
        assertEquals("kewbank", pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest4() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("corpit\\kewbank");
        assertEquals("kewbank", pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest5() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("kewbank@corpit");
        assertEquals("kewbank", pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest6() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("corpit\\");
        assertNull(pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest7() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("corpit\\ ");
        assertNull(pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest8() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("kewbank@");
        assertEquals("kewbank", pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest9() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("kewbank@ ");
        assertEquals("kewbank", pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest10() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("\\kewbank");
        assertEquals("kewbank", pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest11() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName(" \\kewbank");
        assertEquals("kewbank", pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest12() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("@corpit");
        assertNull(pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest13() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName(" @corpit");
        assertNull(pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest14() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("corpit\\kewbank\\boop");
        assertEquals("kewbank", pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }

    @Test
    public void getUserAccountNameAndDomainNameTest15() {
        Pair<String, String> pair = UserAccountUtil.getUserAccountNameAndDomainName("kewbank@corpit@boop");
        assertEquals("kewbank", pair.getFirst());
        assertEquals("corpit", pair.getSecond());
    }
}
