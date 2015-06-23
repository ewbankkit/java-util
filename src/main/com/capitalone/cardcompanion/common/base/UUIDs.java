//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.UUID;

/**
 * Static utility methods pertaining to UUID instances.
 */
public final class UUIDs {
    private static final int UUID_BYTE_LENGTH = 16;

    /**
     * Constructor.
     */
    private UUIDs() {}

    /**
     * Returns a byte array representation of the specified UUID.
     */
    public static byte[] toByteArray(UUID uuid) {
        Preconditions.checkNotNull(uuid);

        ByteArrayDataOutput output = ByteStreams.newDataOutput(UUID_BYTE_LENGTH);
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
        return output.toByteArray();
    }

    /**
     * Returns a UUID created from the specified byte array.
     */
    public static UUID fromByteArray(byte[] bytes) {
        Preconditions.checkNotNull(bytes);
        Preconditions.checkArgument(bytes.length == UUID_BYTE_LENGTH);

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        return new UUID(input.readLong(), input.readLong());
    }
}
