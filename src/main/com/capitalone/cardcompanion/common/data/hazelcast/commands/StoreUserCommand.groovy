//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.hazelcast.core.IMap

final class StoreUserCommand extends AbstractHazelcastStoreCommand<UUID, User> {
    StoreUserCommand(IMap<UUID, User> map, UUID userId, User user) {
        super(map, userId, user)
    }

    @Override
    protected Void getFallback() {
        new UserSqlDAOWrapper().insertOrUpdate(value)
        null
    }
}
