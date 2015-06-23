//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.UUIDs
import net.sf.log4jdbc.sql.rdbmsspecifics.OracleRdbmsSpecifics
import org.junit.Before
import org.junit.Test

class DatabaseSqlFormatterTest {

    private DatabaseSqlFormatter formatter

    @Before
    void setUp() {
        formatter = new DatabaseSqlFormatter(new OracleRdbmsSpecifics())
    }

    @Test
    void testFormatBytesUUID() {
        // random, but repeatable, UUID
        String uuidString = "4b45435c-aecc-429e-9840-6eff7e9acf50"
        UUID uuid = UUID.fromString(uuidString)
        byte[] uuidBytes = UUIDs.toByteArray(uuid)
        String formattedString = formatter.formatParameterObject(uuidBytes)
        // UUIDs converted to upper case and dashes removed since that how oracle
        // stores them and we can use the logs to directly query oracle
        assert formattedString == "'4B45435CAECC429E98406EFF7E9ACF50'"
    }

    @Test
    void testFormatBytesNonUUID() {
        String notAUUID = "I am not a UUID"
        String formattedString = formatter.formatParameterObject(notAUUID.bytes)
        assert formattedString == notAUUID
    }

    @Test
    void testNotAByteArray() {
        Integer i = 5
        String intString = formatter.formatParameterObject(i)
        assert intString == i.toString()
    }

}
