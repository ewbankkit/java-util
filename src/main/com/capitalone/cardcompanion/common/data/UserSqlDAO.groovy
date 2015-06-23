//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.capitalone.cardcompanion.common.base.UUIDs
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import groovy.util.logging.Slf4j

import java.sql.Connection
import java.sql.SQLException

/**
 * User data access object.
 */
@Slf4j
final class UserSqlDAO extends AbstractSqlDAO implements UserDAO {
    private final static String DELETE_USER_SQL       = '''
        DELETE FROM "USER" WHERE USER_ID = ?
    '''

    private final static String INSERT_USER_SQL_INTO_FIELDS = '''(
          USER_ID              ,
          ENTERPRISE_SSO_ID    ,
          DIRECT_BANK_CIF      ,
          ALTERNATE_CUSTOMER_ID,
          USERNAME             ,
          CREATED_DATE
          )
    '''

    private final static String INSERT_USER_SQL_VALUES_CLAUSE = '''
        VALUES (
          ?           ,
          ?           ,
          ?           ,
          ?           ,
          ?           ,
          SYSTIMESTAMP
        )
'''

    private final static String INSERT_USER_SQL       = """
        INSERT INTO "USER" ${INSERT_USER_SQL_INTO_FIELDS} ${INSERT_USER_SQL_VALUES_CLAUSE}
    """
    private final static String SELECT_USER_SQL       = '''
        SELECT USER_ID              ,
               ENTERPRISE_SSO_ID    ,
               DIRECT_BANK_CIF      ,
               ALTERNATE_CUSTOMER_ID,
               USERNAME             ,
               CREATED_DATE
        FROM   "USER"
    '''
    private final static String SELECT_BY_CIF_SQL     = whereSql SELECT_USER_SQL, 'DIRECT_BANK_CIF'
    private final static String SELECT_BY_SSO_ID_SQL  = whereSql SELECT_USER_SQL, 'ENTERPRISE_SSO_ID'
    private final static String SELECT_BY_USER_ID_SQL = whereSql SELECT_USER_SQL, 'USER_ID'

    private final static String UPDATE_USER_SQL_SET_CLAUSE = '''
        SET    ENTERPRISE_SSO_ID     = ?,
               DIRECT_BANK_CIF       = ?,
               ALTERNATE_CUSTOMER_ID = ?,
               USERNAME              = ?

        '''

    private final static String UPDATE_USER_SQL       = """
        UPDATE "USER"
        ${UPDATE_USER_SQL_SET_CLAUSE}
        WHERE  USER_ID = ?
    """

    private final static String UPSERT_USER_SQL       = """
        MERGE INTO "USER"
          USING dual
          ON (USER_ID = ?)
        WHEN MATCHED THEN
            UPDATE ${UPDATE_USER_SQL_SET_CLAUSE}
        WHEN NOT MATCHED THEN
            INSERT ${INSERT_USER_SQL_INTO_FIELDS}
                   ${INSERT_USER_SQL_VALUES_CLAUSE}
    """

    /**
     * Returns the user for the specified CIF.
     */
    @Override
    Optional<User> findByDirectBankCIF(String directBankCIF) {
        Preconditions.checkNotNull directBankCIF

        findOne(SELECT_BY_CIF_SQL, [directBankCIF]) {
            user it
        }
    }

    /**
     * Returns the user for the specified SSO ID.
     */
    @Override
    Optional<User> findByEnterpriseSSOId(String enterpriseSSOId) {
        Preconditions.checkNotNull enterpriseSSOId

        findOne(SELECT_BY_SSO_ID_SQL, [enterpriseSSOId]) {
            user it
        }
    }

    /**
     * Constructor.
     */
    UserSqlDAO(Connection connection) {
        super('user', connection)
    }

    /**
     * Returns the user for the specified user ID.
     */
    @Override
    Optional<User> findByUserId(UUID userId) {
        Preconditions.checkNotNull userId

        findOne(SELECT_BY_USER_ID_SQL, [UUIDs.toByteArray(userId)]) {
            user it
        }
    }

    @Override
    int insert(User user) throws SQLException {
        Preconditions.checkNotNull user

        executeUpdate(INSERT_USER_SQL,
            [UUIDs.toByteArray(user.userId),
             user.enterpriseSSOId,
             user.directBankCIF,
             user.alternateCustomerId,
             user.username]
        )
    }

    @Override
    int removeByUserId(UUID userId) throws SQLException {
        Preconditions.checkNotNull userId

        removeById DELETE_USER_SQL, UUIDs.toByteArray(userId)
    }

    @Override
    int update(User user) throws SQLException {
        Preconditions.checkNotNull user

        executeUpdate(UPDATE_USER_SQL,
            [user.enterpriseSSOId,
             user.directBankCIF,
             user.alternateCustomerId,
             user.username,
             UUIDs.toByteArray(user.userId)]
        )
    }

    @Override
    int insertOrUpdate(User user) throws SQLException {
        Preconditions.checkNotNull user
        byte[] userId = UUIDs.toByteArray(user.userId)
        executeUpdate(UPSERT_USER_SQL,

        [
         // match on the user id
         userId,

         // if it exists these fields are used for the update
         user.enterpriseSSOId,
         user.directBankCIF,
         user.alternateCustomerId,
         user.username,

         // if it does not, these fields are used for the insert
         userId,
         user.enterpriseSSOId,
         user.directBankCIF,
         user.alternateCustomerId,
         user.username
        ])
    }
/**
     * Creates a user from a result set.
     */
    private static User user(def result) {
        new User(
            userId              : UUIDs.fromByteArray(result.USER_ID as byte[]),
            enterpriseSSOId     : result.ENTERPRISE_SSO_ID,
            directBankCIF       : result.DIRECT_BANK_CIF,
            alternateCustomerId : result.ALTERNATE_CUSTOMER_ID,
            username            : result.USERNAME
        )
    }
}
