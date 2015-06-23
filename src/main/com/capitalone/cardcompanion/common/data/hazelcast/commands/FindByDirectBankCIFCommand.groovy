//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.User
import com.capitalone.cardcompanion.common.data.UserSqlDAOWrapper
import com.google.common.base.Optional
import com.hazelcast.core.IMap

class FindByDirectBankCIFCommand extends AbstractHazelcastFindByValueCommand<UUID, User, String> {
    FindByDirectBankCIFCommand(IMap<UUID, User> map, String directBankCIF) {
        super(map, 'directBankCIF', directBankCIF)
    }

    @Override
    protected Optional<User> getFallback() {
        new UserSqlDAOWrapper().findByDirectBankCIF(value)
    }
}
