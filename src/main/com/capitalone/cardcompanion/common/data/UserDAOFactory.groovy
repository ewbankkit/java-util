//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.data.hazelcast.HazelcastClientWrapper
import com.capitalone.cardcompanion.common.data.hazelcast.UserHazelcastDAO
import groovy.util.logging.Slf4j

/**
 * Creates the DAO to access users. The backing store may be a database directly or a Hazelcast cache backed by a database.
 */
@Slf4j
class UserDAOFactory {
    final UserDAO userDAO

    private UserDAOFactory() {
        if (HazelcastClientWrapper.hazelcastEnabled) {
            log.debug 'Using hazelcast user DAO'
            userDAO = new UserHazelcastDAO(HazelcastClientWrapper.instance)
        }
        else {
            log.debug 'Using JDBC user DAO'
            userDAO = new UserSqlDAOWrapper()
        }
    }

    /**
     * Returns the single instance.
     */
    static UserDAOFactory getInstance() {
        LazyHolder.INSTANCE
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        final static UserDAOFactory INSTANCE = new UserDAOFactory()
    }
}
