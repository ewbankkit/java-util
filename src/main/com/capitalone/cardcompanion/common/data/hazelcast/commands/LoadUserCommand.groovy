//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.google.common.base.Optional
import com.hazelcast.core.IMap

final class LoadUserCommand extends AbstractHazelcastLoadCommand<UUID, User> {
    LoadUserCommand(IMap<UUID, User> map, UUID userId) {
        super(map, userId)
    }

    @Override
    protected Optional<User> getFallback() {
        new UserSqlDAOWrapper().findByUserId(key)
    }
}
