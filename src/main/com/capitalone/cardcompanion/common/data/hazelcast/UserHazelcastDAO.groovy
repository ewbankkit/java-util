//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserDAO
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.capitalone.cardcompanion.common.data.hazelcast.commands.DeleteUserCommand
import com.capitalone.cardcompanion.common.data.hazelcast.commands.FindByDirectBankCIFCommand
import com.capitalone.cardcompanion.common.data.hazelcast.commands.FindByEnterpriseSSOIdCommand
import com.capitalone.cardcompanion.common.data.hazelcast.commands.LoadUserCommand
import com.capitalone.cardcompanion.common.data.hazelcast.commands.StoreUserCommand
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap

/**
 * User DAO that looks for a user in a Hazelcast cache. The Hazelcast configuration
 * determines what the actual backing store is.
 */
final class UserHazelcastDAO implements UserDAO {
    private final IMap<UUID, User> userMap

    public UserHazelcastDAO(final HazelcastInstance hazelcastInstance) {
        Preconditions.checkNotNull(hazelcastInstance)

        userMap = new LazyMap<>(hazelcastInstance, HazelcastClientWrapper.USER_MAP_NAME)
    }

    @Override
    Optional<User> findByDirectBankCIF(String directBankCIF) {
        Optional<User> user = new FindByDirectBankCIFCommand(userMap, directBankCIF).execute()
        // This method is a lookup on a secondary index, so if it's not in the cache, go back to the database to get the
        // user and write the user back to the cache
        if ( !user.isPresent() ) {
            user = new UserSqlDAOWrapper().findByDirectBankCIF(directBankCIF)
            if ( user.isPresent() ) {
                insertOrUpdate(user.get())
            }
        }
        return user
    }

    @Override
    Optional<User> findByEnterpriseSSOId(String enterpriseSSOId) {
        Optional<User> user = new FindByEnterpriseSSOIdCommand(userMap, enterpriseSSOId).execute()
        // This method is a lookup on a secondary index, so if it's not in the cache, go back to the database to get the
        // user and write the user back to the cache
        if ( !user.isPresent() ) {
            user = new UserSqlDAOWrapper().findByEnterpriseSSOId(enterpriseSSOId)
            if ( user.isPresent() ) {
                insertOrUpdate(user.get())
            }
        }
        return user
    }

    @Override
    Optional<User> findByUserId(UUID userId) {
        new LoadUserCommand(userMap, userId).execute()
    }

    @Override
    int insert(User user) {
        insertOrUpdate(user)
    }

    @Override
    int update(User user) {
        insertOrUpdate(user)
    }

    @Override
    int insertOrUpdate(User user) {
        new StoreUserCommand(userMap, user.userId, user).execute()
        // If the operation succeeds, return 1 to indicate 1 row was updated.
        1
    }

    @Override
    int removeByUserId(UUID userId) {
        new DeleteUserCommand(userMap, userId).execute()
    }

}
