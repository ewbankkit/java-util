//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

import com.google.common.base.Optional
import groovy.util.logging.Slf4j

/**
 * Groovy bootstrap.
 */
@Slf4j
final class GroovyBootstrap {
    Closure<?> init = {
        log.debug('Groovy init')

        Optional.metaClass.asBoolean = {
            (delegate != null) && (delegate as Optional).present
        }
    }

    Closure<?> destroy = {
        log.debug('Groovy destroy')
    }
}
