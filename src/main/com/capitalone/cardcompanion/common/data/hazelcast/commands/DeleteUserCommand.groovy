//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.hazelcast.core.IMap

final class DeleteUserCommand extends AbstractHazelcastDeleteCommand<UUID, User> {
    DeleteUserCommand(IMap<UUID, User> map, UUID userId) {
        super(map, userId)
    }

    @Override
    protected Integer getFallback() {
        new UserSqlDAOWrapper().removeByUserId(key)
    }
}
