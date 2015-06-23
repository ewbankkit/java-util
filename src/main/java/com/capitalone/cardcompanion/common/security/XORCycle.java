//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;

/**
 * XOR-cycle.
 * Thread-safe.
 */
@ThreadSafe
public final class XORCycle {
    private XORCycle() {}

    public static void apply(byte[] data, byte[] K) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(K);
        Preconditions.checkArgument(K.length > 0);

        for (int i = 0; i < data.length; i++) {
            data[i] ^= K[i % K.length];
        }
    }
}
