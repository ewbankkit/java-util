//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data;

import com.google.common.base.Optional;

import java.util.UUID;

/**
 * User data access object.
 */
public interface UserDAO {
    public abstract Optional<User> findByDirectBankCIF(String directBankCIF);
    public abstract Optional<User> findByEnterpriseSSOId(String enterpriseSSOId);
    public abstract Optional<User> findByUserId(UUID userId);
    public abstract int insert(User user);
    public abstract int removeByUserId(UUID userId);
    public abstract int update(User user);
    public abstract int insertOrUpdate(User user);
}
