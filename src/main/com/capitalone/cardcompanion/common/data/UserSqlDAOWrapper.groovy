//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.google.common.base.Optional

import java.sql.Connection

/**
 * A class that generates a new UserSqlDAO for each class using the current data source connection. This is
 * useful for classes that need to maintain a reference to a UserDAO.
 */
class UserSqlDAOWrapper implements UserDAO {
    @Override
    Optional<User> findByDirectBankCIF(String directBankCIF) {
        DatabaseConnectionPool.instance.dataSource.withConnection {
            new UserSqlDAO(it as Connection).findByDirectBankCIF(directBankCIF)
        }
    }

    @Override
    Optional<User> findByEnterpriseSSOId(String enterpriseSSOId) {
        DatabaseConnectionPool.instance.dataSource.withConnection {
            new UserSqlDAO(it as Connection).findByEnterpriseSSOId(enterpriseSSOId)
        }
    }

    @Override
    Optional<User> findByUserId(UUID userId) {
        DatabaseConnectionPool.instance.dataSource.withConnection {
            new UserSqlDAO(it as Connection).findByUserId(userId)
        }
    }

    @Override
    int insert(User user) {
        DatabaseConnectionPool.instance.dataSource.withTransaction {
            new UserSqlDAO(it as Connection).insert(user)
        }
    }

    @Override
    int removeByUserId(UUID userId) {
        DatabaseConnectionPool.instance.dataSource.withTransaction {
            new UserSqlDAO(it as Connection).removeByUserId(userId)
        }
    }

    @Override
    int update(User user) {
        DatabaseConnectionPool.instance.dataSource.withTransaction {
            new UserSqlDAO(it as Connection).update(user)
        }
    }

    @Override
    int insertOrUpdate(User user) {
        DatabaseConnectionPool.instance.dataSource.withTransaction {
            new UserSqlDAO(it as Connection).insertOrUpdate(user)
        }
    }
}
