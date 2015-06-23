//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.UUIDs
import groovy.transform.PackageScope
import net.sf.log4jdbc.sql.rdbmsspecifics.RdbmsSpecifics

/**
 * A formatter for SQL that wraps whatever formatter is being used and adds custom
 * processing of additional datatypes.
 */
@PackageScope
class DatabaseSqlFormatter extends RdbmsSpecifics {
    @Delegate
    private final RdbmsSpecifics rdbmsSpecifics

    DatabaseSqlFormatter(RdbmsSpecifics rdbmsSpecifics) {
        this.rdbmsSpecifics = rdbmsSpecifics
    }

    @Override
    String formatParameterObject(Object object) {
        (object instanceof byte[]) ?
            formatByteArray(object)
            :
            rdbmsSpecifics.formatParameterObject(object)
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    private String formatByteArray(byte[] bytes) {
        // we generally only store byte arrays as
        try {
            "'${UUIDs.fromByteArray(bytes).toString().replaceAll('-', '').toUpperCase()}'"
        }
        catch (Exception ignored) {
            // if it's not a UUID, just return it as a String
            new String(bytes)
        }
    }
}
