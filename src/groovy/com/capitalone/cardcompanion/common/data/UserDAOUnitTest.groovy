//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.google.common.base.Optional
import org.junit.Before
import org.junit.Test

import java.sql.SQLException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class UserDAOUnitTest {
    DataSource dataSource
    UUID       uuid1
    UUID       uuid2

    @Before
    void startup() {
        dataSource = new DataSource(new SimpleDataSource())
        uuid1 = UUID.fromString('11dc62c4-f96a-4187-be0d-183e7ec5d5e4')
        uuid2 = UUID.fromString('27701b31-dbb4-481c-957f-50f5bbbe1e23')
    }

    @Test
    void testFind1() {
        Optional<Tuple> optionalUserAndCreatedDate = dataSource.withConnection {
            UserDAO dao = new UserSqlDAO(it)
            dao.findByUserId(uuid1)
        }
        assertNotNull optionalUserAndCreatedDate
        assertFalse optionalUserAndCreatedDate.present
    }

    @Test
    void testFind2() {
        Optional<User> optionalUser =  dataSource.withConnection {
            UserDAO dao = new UserSqlDAO(it)
            dao.findByEnterpriseSSOId('DUMMY SSO ID')
        }
        assertNotNull optionalUser
        assertFalse optionalUser.present
    }

    @Test
    void testFind3() {
        Optional<User> optionalUser = dataSource.withConnection {
            UserDAO dao = new UserSqlDAO(it)
            dao.findByDirectBankCIF('DUMMY CIF')
        }
        assertNotNull optionalUser
        assertFalse optionalUser.present
    }

    @Test
    void testRemove1() {
        int removed = dataSource.withConnection {
            UserDAO dao = new UserSqlDAO(it)
            dao.removeByUserId(uuid1)
        }
        assertEquals 0, removed
    }

    @Test
    void testInsertAndUpdateSSOId() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid2, enterpriseSSOId: 'UNIT TEST SSO ID', username: 'UNIT TEST EOS USERNAME')

            int inserted = dao.insert(user1)
            assertEquals 1, inserted

            Optional<User> optionalUser = dao.findByUserId(uuid2)
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertEquals user1, optionalUser.get()

            optionalUser = dao.findByEnterpriseSSOId('UNIT TEST SSO ID')
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertEquals user1, optionalUser.get()

            User user2 = new User(userId: user1.userId, enterpriseSSOId: user1.enterpriseSSOId, username: 'ANOTHER UNIT TEST EOS USERNAME')
            int updated = dao.update(user2)
            assertEquals 1, updated

            optionalUser = dao.findByUserId(uuid2)
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertNotEquals user1, optionalUser.get()
            assertEquals user2, optionalUser.get()

            int removed = dao.removeByUserId(uuid2)
            assertEquals 1, removed
        }
    }

    @Test
    void testInsertAndUpdateCIF() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid2, directBankCIF: 'UNIT TEST DIRECT BANK CIF', username: 'UNIT TEST DIRECT BANK SAVER ID')

            int inserted = dao.insert(user1)
            assertEquals 1, inserted

            Optional<User> optionalUser = dao.findByUserId(uuid2)
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertEquals user1, optionalUser.get()

            optionalUser = dao.findByDirectBankCIF('UNIT TEST DIRECT BANK CIF')
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertEquals user1, optionalUser.get()

            User user2 = new User(userId: user1.userId, directBankCIF: user1.directBankCIF, username: 'ANOTHER UNIT TEST EOS USERNAME')
            int updated = dao.update(user2)
            assertEquals 1, updated

            optionalUser = dao.findByUserId(uuid2)
            assertNotNull optionalUser
            assertTrue optionalUser.present
            assertNotEquals user1, optionalUser.get()
            assertEquals user2, optionalUser.get()

            int removed = dao.removeByUserId(uuid2)
            assertEquals 1, removed
        }
    }

    @Test(expected = SQLException.class)
    void testInsertNoSSOIdOrCIF() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid2, username: 'UNIT TEST DIRECT BANK SAVER ID')

            int inserted = dao.insert(user1)
            assertEquals 0, inserted
        }
    }

    @Test(expected = SQLException.class)
    void testInsertSSOIdAndCIF() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid2, enterpriseSSOId: 'UNIT TEST SSO ID', directBankCIF: 'UNIT TEST DIRECT BANK CIF', username: 'UNIT TEST DIRECT BANK SAVER ID')

            int inserted = dao.insert(user1)
            assertEquals 1, inserted

            int removed = dao.removeByUserId(uuid2)
            assertEquals 1, removed
        }
    }

    @Test(expected = SQLException.class)
    void testInsertDuplicateSSOId() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid1, enterpriseSSOId: 'UNIT TEST SSO ID', username: 'UNIT TEST USERNAME 1')

            int inserted = dao.insert(user1)
            assertEquals 1, inserted

            User user2 = new User(userId: uuid2, enterpriseSSOId: 'UNIT TEST SSO ID', username: 'UNIT TEST USERNAME 2')

            inserted = dao.insert(user2)
            assertEquals 1, inserted

            int removed = dao.removeByUserId(uuid2)
            assertEquals 1, removed

            removed = dao.removeByUserId(uuid1)
            assertEquals 1, removed
        }
    }

    @Test(expected = SQLException.class)
    void testInsertDuplicateCIF() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid1, directBankCIF: 'UNIT TEST DIRECT BANK CIF', username: 'UNIT TEST USERNAME 1')

            int inserted = dao.insert(user1)
            assertEquals 1, inserted

            User user2 = new User(userId: uuid2, directBankCIF: 'UNIT TEST DIRECT BANK CIF', username: 'UNIT TEST USERNAME 2')

            inserted = dao.insert(user2)
            assertEquals 1, inserted

            int removed = dao.removeByUserId(uuid2)
            assertEquals 1, removed

            removed = dao.removeByUserId(uuid1)
            assertEquals 1, removed
        }
    }

    @Test
    void testUpdateOrInsert() {
        dataSource.withTransaction {
            UserDAO dao = new UserSqlDAO(it)

            User user1 = new User(userId: uuid1, directBankCIF: 'UNIT TEST UPDATE OR INSERT', username: 'UNIT TEST USERNAME')
            int inserted = dao.insertOrUpdate(user1)
            assertEquals 1, inserted
            assertEquals 'UNIT TEST USERNAME', dao.findByUserId(uuid1).get().username

            User user1Modified = new User(userId: uuid1, directBankCIF: 'UNIT TEST UPDATE OR INSERT', username: 'CHANGED THE USERNAME')
            int updated = dao.insertOrUpdate(user1Modified)
            assertEquals 1, updated
            assertEquals 'CHANGED THE USERNAME', dao.findByUserId(uuid1).get().username

            int removed = dao.removeByUserId(uuid1)
            assertEquals 1, removed
        }
    }
}
