//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.google.common.base.Optional
import com.hazelcast.core.IMap

final class FindByEnterpriseSSOIdCommand extends AbstractHazelcastFindByValueCommand<UUID, User, String> {
    FindByEnterpriseSSOIdCommand(IMap<UUID, User> map, String enterpriseSSOId) {
        super(map, 'enterpriseSSOId', enterpriseSSOId)
    }

    @Override
    protected Optional<User> getFallback() {
        new UserSqlDAOWrapper().findByEnterpriseSSOId(value)
    }
}
