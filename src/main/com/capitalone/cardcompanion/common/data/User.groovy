//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation
import groovy.transform.Immutable

/**
 * User data.
 */
@Immutable
final class User implements Serializable {
    private static final long serialVersionUID = 7129617254526797273L

    String alternateCustomerId
    String directBankCIF

    String enterpriseSSOId
    UUID   userId
    String username

    @Override
    String toString() {
        ReflectiveRepresentation.toString(this)
    }
}
