/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.github.ewbankkit.util.beans.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:45 BaseHelperUnitTest.java NSI";

    private static DbHelper dbHelper;

    @BeforeClass
    public static void setup() throws SQLException {
        dbHelper = new DbHelper();
    }

    @AfterClass
    public static void teardown() {
        dbHelper.close();
    }

    @Test
    public void md5Test1() throws SQLException {
        // SELECT CAST(MD5('boop') AS CHAR);
        assertEquals("65eab40bf1bcd5c82c6d9e02abea5ed3", BaseHelper.getMD5Checksum("boop"));
    }

    @Test
    public void md5Test2() throws SQLException {
        assertEquals(null, BaseHelper.getMD5Checksum(null));
    }

    @Test
    public void md5Test3() throws SQLException {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", BaseHelper.getMD5Checksum(""));
    }

    @Test
    public void md5Test4() throws SQLException {
        assertEquals("655197e1142f25d09d1676839d0d6594", BaseHelper.getMD5Checksum("TMas2slLZuZ9irVXN16gGtZYuZPYD3pU"));
    }

    @Test
    public void md5Test5() throws SQLException {
        assertEquals("3ecdc2cf11448a52a897c620ceff845a", BaseHelper.getMD5Checksum("cWSrtIqXnNe9BOCPHiZezOaf7GStFFtdrca4pcUyQkk54yLod8TmWoG8bYir9ZS9"));
    }

    @Test
    public void sha1Test1() throws SQLException {
        // SELECT CAST(SHA1('boop') AS CHAR);
        assertEquals("ae8d904cebfd629cdb1cc773a5bce8aca1dc1eee", BaseHelper.getSHA1Checksum("boop"));
    }

    @Test
    public void sha1Test2() throws SQLException {
        assertEquals(null, BaseHelper.getSHA1Checksum(null));
    }

    @Test
    public void sha1Test3() throws SQLException {
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", BaseHelper.getSHA1Checksum(""));
    }

    @Test
    public void sha1Test4() throws SQLException {
        assertEquals("cdd26907d5bfe22b61166da6045338c1c0888c4b", BaseHelper.getSHA1Checksum("TMas2slLZuZ9irVXN16gGtZYuZPYD3pU"));
    }

    @Test
    public void sha1Test5() throws SQLException {
        assertEquals("326ec8f649b91a2db6775f4ee4e022672fb6da53", BaseHelper.getSHA1Checksum("cWSrtIqXnNe9BOCPHiZezOaf7GStFFtdrca4pcUyQkk54yLod8TmWoG8bYir9ZS9"));
    }

    @Test
    public void selectBigDecimalTest1() throws SQLException {
        assertEquals(BaseHelper.toBigDecimal(Integer.valueOf(1), 2), dbHelper.getBigDecimal(BigDecimal.ONE));
    }

    @Test
    public void selectBigDecimalTest2() throws SQLException {
        assertEquals(BaseHelper.toBigDecimal(Integer.valueOf(0), 2), dbHelper.getBigDecimal(BigDecimal.ZERO));
    }

    @Test
    public void selectBigDecimalTest3() throws SQLException {
        assertEquals(null, dbHelper.getBigDecimal(null));
    }

    @Test
    public void selectBigDecimalTest4() throws SQLException {
        assertEquals(Double.valueOf(1.23D), BaseHelper.toDouble(dbHelper.getBigDecimal(BaseHelper.toBigDecimal(1.234D, 2))));
    }

    @Test
    public void selectBooleanTest1() throws SQLException {
        assertTrue(dbHelper.getBoolean(true));
    }

    @Test
    public void selectBooleanTest2() throws SQLException {
        assertFalse(dbHelper.getBoolean(false));
    }

    @Test
    public void selectBooleanTest3() throws SQLException {
        assertFalse(dbHelper.getBoolean(null));
    }

    @Test
    public void selectBooleanTest4() throws SQLException {
        assertTrue(dbHelper.getBoolean(null, true));
    }

    @Test
    public void selectBooleanTest5() throws SQLException {
        assertFalse(dbHelper.getBoolean(Boolean.FALSE, true));
    }

    @Test
    public void selectBBooleanTest1() throws SQLException {
        assertEquals(Boolean.TRUE, dbHelper.getBBoolean(Boolean.TRUE));
    }

    @Test
    public void selectBBooleanTest2() throws SQLException {
        assertEquals(Boolean.FALSE, dbHelper.getBBoolean(Boolean.FALSE));
    }

    @Test(expected=IllegalArgumentException.class)
    public void selectCharacterTest1() throws SQLException {
        assertEquals(Character.valueOf('a'), dbHelper.getCharacter(Character.valueOf('a')));
    }

    @Test
    public void selectDateTest1() throws SQLException {
        java.util.Date date = new java.util.Date();
        assertEquals(date, dbHelper.getDate(date));
    }

    @Test
    public void selectDateTest2() throws SQLException {
        assertEquals(null, dbHelper.getDate(null));
    }

    @Test
    public void selectDateTest3() throws SQLException {
        java.util.Date date = new java.util.Date();
        assertEquals(date, dbHelper.getDate2(date));
    }

    @Test
    public void selectDateTest4() throws SQLException {
        java.util.Date date = new java.util.Date();
        assertEquals(date, dbHelper.getDate3(date));
    }

    @Test
    public void selectDoubleTest1() throws SQLException {
        assertEquals(1D, dbHelper.getDouble(1D), 0D);
    }

    @Test
    public void selectDoubleTest2() throws SQLException {
        assertEquals(0D, dbHelper.getDouble(0D), 0D);
    }

    @Test
    public void selectDoubleTest3() throws SQLException {
        assertEquals(0D, dbHelper.getDouble(null), 0D);
    }

    @Test
    public void selectDoubleTest4() throws SQLException {
        assertEquals(0D, dbHelper.getDouble(null, 1D), 1D);
    }

    @Test
    public void selectDoubleTest5() throws SQLException {
        assertEquals(0D, dbHelper.getDouble(Double.valueOf(2D), 1D), 2D);
    }

    @Test
    public void selectDoubleTest6() throws SQLException {
        assertEquals(Double.MAX_VALUE, dbHelper.getDouble(Double.MAX_VALUE), 0D);
    }

    @Test
    public void selectDoubleTest7() throws SQLException {
        assertEquals(Double.MIN_VALUE, dbHelper.getDouble(Double.MIN_VALUE), 0D);
    }

    @Test
    public void selectDDoubleTest1() throws SQLException {
        assertEquals(Double.valueOf(1D), dbHelper.getDDouble(Double.valueOf(1D)));
    }

    @Test
    public void selectDDoubleTest2() throws SQLException {
        assertEquals(Double.valueOf(0D), dbHelper.getDDouble(Double.valueOf(0D)));
    }

    @Test
    public void selectDDoubleTest3() throws SQLException {
        assertEquals(null, dbHelper.getDDouble(null));
    }

    @Test
    public void selectDDoubleTest4() throws SQLException {
        assertEquals(Double.valueOf(Double.MAX_VALUE), dbHelper.getDDouble(Double.valueOf(Double.MAX_VALUE)));
    }

    @Test
    public void selectDDoubleTest5() throws SQLException {
        assertEquals(Double.valueOf(Double.MIN_VALUE), dbHelper.getDDouble(Double.valueOf(Double.MIN_VALUE)));
    }

    @Test
    public void selectFloatTest1() throws SQLException {
        assertEquals(1F, dbHelper.getFloat(1F), 0F);
    }

    @Test
    public void selectFloatTest2() throws SQLException {
        assertEquals(0F, dbHelper.getFloat(0F), 0F);
    }

    @Test
    public void selectFloatTest3() throws SQLException {
        assertEquals(0F, dbHelper.getFloat(null), 0F);
    }

    @Test
    public void selectFloatTest4() throws SQLException {
        assertEquals(0F, dbHelper.getFloat(null, 1F), 1F);
    }

    @Test
    public void selectFloatTest5() throws SQLException {
        assertEquals(0F, dbHelper.getFloat(Float.valueOf(2F), 1F), 2F);
    }

    @Test
    public void selectFloatTest6() throws SQLException {
        assertEquals(Float.MAX_VALUE, dbHelper.getFloat(Float.MAX_VALUE), 0F);
    }

    @Test
    public void selectFloatTest7() throws SQLException {
        assertEquals(Float.MIN_VALUE, dbHelper.getFloat(Float.MIN_VALUE), 0F);
    }

    @Test
    public void selectFFloatTest1() throws SQLException {
        assertEquals(Float.valueOf(1F), dbHelper.getFFloat(Float.valueOf(1F)));
    }

    @Test
    public void selectFFloatTest2() throws SQLException {
        assertEquals(Float.valueOf(0F), dbHelper.getFFloat(Float.valueOf(0F)));
    }

    @Test
    public void selectFFloatTest3() throws SQLException {
        assertEquals(null, dbHelper.getFFloat(null));
    }

    @Test
    public void selectFFloatTest4() throws SQLException {
        assertEquals(Float.valueOf(Float.MAX_VALUE), dbHelper.getFFloat(Float.valueOf(Float.MAX_VALUE)));
    }

    @Test
    public void selectFFloatTest5() throws SQLException {
        assertEquals(Float.valueOf(Float.MIN_VALUE), dbHelper.getFFloat(Float.valueOf(Float.MIN_VALUE)));
    }

    @Test
    public void selectIntTest1() throws SQLException {
        assertEquals(1, dbHelper.getInt(1));
    }

    @Test
    public void selectIntTest2() throws SQLException {
        assertEquals(0, dbHelper.getInt(0));
    }

    @Test
    public void selectIntTest3() throws SQLException {
        assertEquals(0, dbHelper.getInt(null));
    }

    @Test
    public void selectIntTest4() throws SQLException {
        assertEquals(1, dbHelper.getInt(null, 1));
    }

    @Test
    public void selectIntTest5() throws SQLException {
        assertEquals(2, dbHelper.getInt(Integer.valueOf(2), 1));
    }

    @Test
    public void selectIntTest6() throws SQLException {
        assertEquals(Integer.MAX_VALUE, dbHelper.getInt(Integer.MAX_VALUE));
    }

    @Test
    public void selectIntTest7() throws SQLException {
        assertEquals(Integer.MIN_VALUE, dbHelper.getInt(Integer.MIN_VALUE));
    }

    @Test
    public void selectIntTest8() throws SQLException {
        assertEquals(0, dbHelper.getNoInt());
    }

    @Test
    public void selectIntTest9() throws SQLException {
        assertEquals(1, dbHelper.getNoInt(1));
    }

    @Test
    public void selectIntTest10() throws SQLException {
        assertEquals(Integer.MAX_VALUE, dbHelper.getNoInt(Integer.MAX_VALUE));
    }

    @Test
    public void selectIntTest11() throws SQLException {
        assertEquals(Integer.MIN_VALUE, dbHelper.getNoInt(Integer.MIN_VALUE));
    }

    @Test
    public void selectIntTest12() throws SQLException {
        assertEquals(0, dbHelper.getInt2(0));
    }

    @Test
    public void selectIntTest13() throws SQLException {
        assertEquals(Integer.MIN_VALUE, dbHelper.getInt2(Integer.MIN_VALUE));
    }

    @Test(expected = SQLException.class)
    public void selectIntTest14() throws SQLException {
        assertEquals(0, dbHelper.getInt3(0));
    }

    @Test
    public void selectIntegerTest1() throws SQLException {
        assertEquals(Integer.valueOf(1), dbHelper.getInteger(Integer.valueOf(1)));
    }

    @Test
    public void selectIntegerTest2() throws SQLException {
        assertEquals(Integer.valueOf(0), dbHelper.getInteger(Integer.valueOf(0)));
    }

    @Test
    public void selectIntegerTest3() throws SQLException {
        assertEquals(null, dbHelper.getInteger(null));
    }

    @Test
    public void selectIntegerTest4() throws SQLException {
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), dbHelper.getInteger(Integer.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void selectIntegerTest5() throws SQLException {
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), dbHelper.getInteger(Integer.valueOf(Integer.MIN_VALUE)));
    }

    @Test
    public void selectIntegerTest6() throws SQLException {
        assertEquals(null, dbHelper.getNoInteger());
    }

    @Test
    public void selectLongTest1() throws SQLException {
        assertEquals(1L, dbHelper.getLong(1L));
    }

    @Test
    public void selectLongTest2() throws SQLException {
        assertEquals(0L, dbHelper.getLong(0L));
    }

    @Test
    public void selectLongTest3() throws SQLException {
        assertEquals(0L, dbHelper.getLong(null));
    }

    @Test
    public void selectLongTest4() throws SQLException {
        assertEquals(1L, dbHelper.getLong(null, 1L));
    }

    @Test
    public void selectLongTest5() throws SQLException {
        assertEquals(2L, dbHelper.getLong(Long.valueOf(2L), 1L));
    }

    @Test
    public void selectLongTest6() throws SQLException {
        assertEquals(Long.MAX_VALUE, dbHelper.getLong(Long.MAX_VALUE));
    }

    @Test
    public void selectLongTest7() throws SQLException {
        assertEquals(Long.MIN_VALUE, dbHelper.getLong(Long.MIN_VALUE));
    }

    @Test
    public void selectLLongTest1() throws SQLException {
        assertEquals(Long.valueOf(1L), dbHelper.getLLong(Long.valueOf(1L)));
    }

    @Test
    public void selectLLongTest2() throws SQLException {
        assertEquals(Long.valueOf(0L), dbHelper.getLLong(Long.valueOf(0L)));
    }

    @Test
    public void selectLLongTest3() throws SQLException {
        assertEquals(null, dbHelper.getLLong(null));
    }

    @Test
    public void selectLLongTest4() throws SQLException {
        assertEquals(Long.valueOf(Long.MAX_VALUE), dbHelper.getLLong(Long.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void selectLLongTest5() throws SQLException {
        assertEquals(Long.valueOf(Long.MIN_VALUE), dbHelper.getLLong(Long.valueOf(Long.MIN_VALUE)));
    }

    @Test
    public void selectStringTest1() throws SQLException {
        assertEquals("BOOP", dbHelper.getString("BOOP"));
    }

    @Test
    public void selectStringTest2() throws SQLException {
        assertEquals(null, dbHelper.getString(null));
    }

    @Test
    public void selectStringTest3() throws SQLException {
        assertEquals("", dbHelper.getString(""));
    }

    @Test(expected=SQLException.class)
    public void selectStringTest4() throws SQLException {
        assertEquals("", dbHelper.getSingleString(""));
    }

    @Test
    public void selectStringTest5() throws SQLException {
        List<String> strings = dbHelper.getStrings("");
        assertEquals(2, strings.size());
    }

    @Test
    public void selectStringTest6() throws SQLException {
        List<String> strings = dbHelper.getNoStrings();
        assertTrue(strings.isEmpty());
    }

    @Test
    public void selectStringTest7() throws SQLException {
        Map<String, String> map = dbHelper.getNoStringMap();
        assertTrue(map.isEmpty());
    }

    @Test
    public void selectStringTest8() throws SQLException {
        assertEquals("BOOP", dbHelper.getNoString("BOOP"));
    }

    @Test
    public void selectMapTest1() throws SQLException {
        Map<Integer, String> map = dbHelper.getMap();
        assertEquals(2, map.size());
    }

    @Test
    public void selectMapTest2() throws SQLException {
        Map<Integer, String> map = dbHelper.getMap();
        assertEquals("AAA", map.get(1));
        assertEquals("BBB", map.get(2));
    }

    @Test
    public void selectMapOfListsTest1() throws SQLException {
        Map<Integer, List<String>> map = dbHelper.getMapOfLists();
        assertEquals(2, map.size());
    }

    @Test
    public void selectMapOfListsTest2() throws SQLException {
        Map<Integer, List<String>> map = dbHelper.getMapOfLists();
        assertEquals(2, map.get(1).size());
        assertEquals(1, map.get(2).size());
    }

    @Test
    public void selectMapOfListsTest3() throws SQLException {
        Map<Integer, List<String>> map = dbHelper.getMapOfLists();
        assertEquals("XXX", map.get(1).get(0));
        assertEquals("ZZZ", map.get(1).get(1));
        assertEquals("YYY", map.get(2).get(0));
    }

    @Test
    public void getUpdateStatementTest1() throws SQLException {
        final String SQL = "UPDATE test SET %1$s WHERE %2$s;";
        String[] columnNames = {"s"};
        String sql = String.format(SQL, BaseHelper.getUpdateValuesSnippet(columnNames), BaseHelper.getUpdateValuesGuardSnippet(columnNames));
        assertEquals("UPDATE test SET s = ? WHERE (NOT (s <=> ?));", sql);
    }

    @Test
    public void getUpdateStatementTest2() throws SQLException {
        final String SQL = "UPDATE test SET %1$s WHERE %2$s;";
        String[] columnNames = {"s", "i"};
        String sql = String.format(SQL, BaseHelper.getUpdateValuesSnippet(columnNames), BaseHelper.getUpdateValuesGuardSnippet(columnNames));
        assertEquals("UPDATE test SET s = ?, i = ? WHERE (NOT (s <=> ? AND i <=> ?));", sql);
    }

    @Test
    public void selectExistsTest1() throws SQLException {
        assertTrue(dbHelper.getExists());
    }

    @Test
    public void selectNotExistsTest1() throws SQLException {
        assertFalse(dbHelper.getNotExists());
    }

    @Test
    public void selectLikeTest1() throws SQLException {
        assertTrue(dbHelper.getIsLike("ABC", "ABC"));
    }

    @Test
    public void selectLikeTest2() throws SQLException {
        assertFalse(dbHelper.getIsLike("ABC", "123"));
    }

    @Test
    public void selectLikeTest3() throws SQLException {
        assertTrue(dbHelper.getIsLike("ABC", "%B_"));
    }

    @Test
    public void selectLikeTest4() throws SQLException {
        assertFalse(dbHelper.getIsLike("ABCD", "%" + BaseHelper.escapeForLikeOperator("%") + "%"));
    }

    @Test
    public void selectLikeTest5() throws SQLException {
        assertTrue(dbHelper.getIsLike("A%CD", "%" + BaseHelper.escapeForLikeOperator("%") + "%"));
    }

    @Test
    public void selectLikeTest6() throws SQLException {
        assertTrue(dbHelper.getIsLike("ABC", "%" + BaseHelper.escapeForLikeOperator("C") + "%"));
    }

    @Test
    public void pingTest1() throws SQLException {
        Connection connection = null;
        try {
            connection = BaseHelper.createDevGdbConnection();
            assertTrue(BaseHelper.ping(connection));
            BaseHelper.close(connection);
            assertFalse(BaseHelper.ping(connection));
        }
        finally {
            BaseHelper.close(connection);
        }
    }

    private static class DbHelper extends BaseHelper {
        private final Connection connection;
        private static final String logTag = null;
        private static final String select0Sql = "SELECT NULL AS value FROM DUAL LIMIT 0;";
        private static final String select1Sql = "SELECT ? AS value FROM DUAL;";
        private static final String select1BigDecimalSql = "SELECT CAST(? AS DECIMAL(4, 2)) AS value FROM DUAL;";
        private static final String select2Sql = "SELECT ? AS value FROM DUAL UNION ALL SELECT NULL AS value FROM DUAL;";
        private static final String selectMapSql = "SELECT 1, 'AAA' FROM DUAL UNION ALL SELECT 2, 'BBB' FROM DUAL;";
        private static final String selectMapOfListsSql = "SELECT 1, 'XXX' FROM DUAL UNION ALL SELECT 2, 'YYY' FROM DUAL UNION ALL SELECT 1, 'ZZZ' FROM DUAL;";
        private static final String selectCount0Sql = "SELECT COUNT(*) FROM DUAL LIMIT 0;";
        private static final String selectCount1Sql = "SELECT COUNT(*) FROM DUAL;";
        private static final String selectLikeSql = "SELECT COUNT(*) FROM DUAL WHERE ? LIKE ?;";

        public DbHelper() throws SQLException {
            super("", true);
            connection = createDevGdbConnection();
        }

        public void close() {
            close(connection);
        }

        public BigDecimal getBigDecimal(BigDecimal bd) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1BigDecimalSql);
                statement.setBigDecimal(1, bd);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, BigDecimal.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getBoolean(boolean b) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setBoolean(1, b);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, false);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getBoolean(Object o) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", false);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getBoolean(Object o, boolean defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Boolean getBBoolean(Boolean b) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, b);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Boolean.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Character getCharacter(Character c) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, c);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Character.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public java.util.Date getDate(java.util.Date d) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setTimestamp(1, BaseHelper.toSqlTimestamp(d));
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, java.util.Date.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public java.util.Date getDate2(java.util.Date d) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setTimestamp(1, BaseHelper.toSqlTimestamp(d));
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, new DateFactory("value") {});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public java.util.Date getDate3(java.util.Date d) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setTimestamp(1, BaseHelper.toSqlTimestamp(d));
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, new DateFactory("value") {});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public double getDouble(double d) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setDouble(1, d);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, 0D);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public double getDouble(Object o) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", 0D);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public double getDouble(Object o, double defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Double getDDouble(Double d) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, d);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Double.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public float getFloat(float f) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setFloat(1, f);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, 0F);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public float getFloat(Object o) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", 0F);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public float getFloat(Object o, float defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Float getFFloat(Float f) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, f);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Float.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getInt(int i) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setInt(1, i);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getInt2(int i) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setInt(1, i);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getInt3(int i) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select2Sql);
                statement.setInt(1, i);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getInt(Object o) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getInt(Object o, int defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getNoInt() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public int getNoInt(int defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Integer getInteger(Integer i) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, i);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Integer.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Integer getNoInteger() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Integer.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public long getLong(long l) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setLong(1, l);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, 0L);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public long getLong(Object o) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", 0L);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public long getLong(Object o, long defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, o);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, "value", defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Long getLLong(Long l) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setObject(1, l);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, Long.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public String getString(String s) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select1Sql);
                statement.setString(1, s);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, String.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public String getNoString(String defaultValue) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return firstValue(resultSet, 1, String.class, defaultValue);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public String getSingleString(String s) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select2Sql);
                statement.setString(1, s);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, 1, String.class);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public List<String> getStrings(String s) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select2Sql);
                statement.setString(1, s);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, new StringFactory(1) {});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public List<String> getNoStrings() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, new StringFactory(1) {});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Map<String, String> getNoStringMap() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(select0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newMap(resultSet, new Factory<Pair<String, String>>() {
                    public Pair<String, String> newInstance(ResultSet resultSet) throws SQLException {
                        return Pair.from(null, null);
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Map<Integer, String> getMap() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(selectMapSql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newMap(resultSet, new Factory<Pair<Integer, String>>() {
                    public Pair<Integer, String> newInstance(ResultSet resultSet) throws SQLException {
                        return Pair.from(getIntegerValue(resultSet, 1), resultSet.getString(2));
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Map<Integer, List<String>> getMapOfLists() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(selectMapOfListsSql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newMapOfLists(resultSet, new Factory<Pair<Integer, String>>() {
                    public Pair<Integer, String> newInstance(ResultSet resultSet) throws SQLException {
                        return Pair.from(getIntegerValue(resultSet, 1), resultSet.getString(2));
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getExists() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(selectCount1Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return countGreaterThanZero(resultSet);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getNotExists() throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(selectCount0Sql);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return countGreaterThanZero(resultSet);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public boolean getIsLike(String a, String b) throws SQLException {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(selectLikeSql);
                statement.setString(1, a);
                statement.setString(2, b);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return countGreaterThanZero(resultSet);
            }
            finally {
                close(statement, resultSet);
            }
        }
    }
}
