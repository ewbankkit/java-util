//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.google.common.base.Optional
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test

class UserHazelcastDAOUnitTest {

    private UserHazelcastDAO dao

    private final static UUID USER_ID = UUID.randomUUID()

    /** user to use for testing */
    private static final User USER = new User(
            alternateCustomerId: "alternateId",
            directBankCIF: "directBankCIF",
            enterpriseSSOId: "enterpriseSSOId",
            userId: USER_ID,
            username: "username"
    )

    @Before
    void before() {
        dao = new UserHazelcastDAO(createHazelcastInstanceMock())
    }

    @Test
    void testFindByDirectBankCIFWhenInCache() {
        dao.insert(USER)
        User user = dao.findByDirectBankCIF("directBankCIF").get()
        assert user == USER
    }

    @Test
    void testFindByEnterpriseSSOIdWhenInCache() {
        dao.insert(USER)
        User user = dao.findByEnterpriseSSOId("enterpriseSSOId").get()
        assert user == USER
    }

    @Test
    void testFindByDirectBankCIFWhenNotInCache() {
        MockFor userSqlDAOMock = new MockFor(UserSqlDAOWrapper)
        userSqlDAOMock.demand.findByDirectBankCIF { String cif -> assert cif == "directBankCIF"; Optional.of(USER) }
        User user
        userSqlDAOMock.use {
             user = dao.findByDirectBankCIF("directBankCIF").get()
        }
        assert user == USER

        // now the user should be in the cache
        assert dao.findByUserId(USER_ID).get() == user
    }

    @Test
    void testFindByEnterpriseSSOIdWhenNotInCache() {
        MockFor userSqlDAOMock = new MockFor(UserSqlDAOWrapper)
        userSqlDAOMock.demand.findByEnterpriseSSOId { String ssoId -> assert ssoId == "enterpriseSSOId"; Optional.of(USER) }
        User user
        userSqlDAOMock.use {
            user = dao.findByEnterpriseSSOId("enterpriseSSOId").get()
        }
        assert user == USER

        // now the user should be in the cache
        assert dao.findByUserId(USER_ID).get() == USER
    }

    @Test
    void testInsertAndRetrieveUser() {
        dao.insert(USER)
        User user = dao.findByUserId(USER_ID).get()
        assert user == USER
    }

    @Test
    void testRemoveByUserId() {
        dao.insert(USER)
        User user = dao.findByUserId(USER_ID).get()
        assert user

        // remove the user and then make sure it does not exist
        dao.removeByUserId(USER_ID)

        assert !dao.findByUserId(USER_ID).isPresent()
    }

    @Test
    void testUpdateUser() {
        dao.insert(USER)
        User user = dao.findByUserId(USER_ID).get()
        assert user == USER

        User newUser = new User(alternateCustomerId: USER.alternateCustomerId, directBankCIF: USER.directBankCIF,
                enterpriseSSOId: USER.enterpriseSSOId, userId: USER.userId, username: "updatedUsername")

        dao.update(newUser)
        user = dao.findByUserId(USER_ID).get()
        assert user == newUser
    }

    @Test
    void testUpdateOrInsertUser() {
        dao.insertOrUpdate(USER)
        User user = dao.findByUserId(USER_ID).get()
        assert user == USER

        User newUser = new User(alternateCustomerId: USER.alternateCustomerId, directBankCIF: USER.directBankCIF,
                enterpriseSSOId: USER.enterpriseSSOId, userId: USER.userId, username: "updatedUsername")

        dao.insertOrUpdate(newUser)
        user = dao.findByUserId(USER_ID).get()
        assert user == newUser
    }

    @Test
    void testUserNotFound() {
        assert !dao.findByUserId(UUID.randomUUID()).isPresent()
    }


    private static HazelcastInstance createHazelcastInstanceMock() {
        IMap<?,?> hazelcastMapMock = new IMapMock<>()
        def imap = [:]

        // delegate all of the method of IMap to the mock
        IMap.methods.each {
            String methodName = it.name
            imap."$methodName" = { Object[] args ->
                return hazelcastMapMock."$methodName"(*args)
            }
        }

        return [
          getMap: { name -> imap.asType(IMap) }
        ] as HazelcastInstance

    }

}
