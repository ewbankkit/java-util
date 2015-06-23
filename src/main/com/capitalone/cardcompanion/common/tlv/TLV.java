//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.tlv;

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import java.io.IOException;
import java.util.Map;

/**
 * TagLengthValue.
 */
public final class TLV {
    private       Optional<? extends Map<Integer, TLV>> children = Optional.absent();
    private final int                                   tag;
    private final byte[]                                value;

    /**
     * Constructor.
     */
    public TLV(int tag, byte[] value) {
        Preconditions.checkNotNull(value);

        this.tag = tag;
        this.value = value.clone();
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public Optional<TLV> getChild(int tag) {
        Optional<? extends Map<Integer, TLV>> children = getChildren();
        return children.isPresent() ? Optional.fromNullable(children.get().get(Integer.valueOf(tag))) : Optional.<TLV>absent();
    }

    public Optional<? extends Map<Integer, TLV>> getChildren() {
        if (!children.isPresent()) {
            try {
                children = TLVIO.read(ByteSource.wrap(value));
            }
            catch (IOException ignore) {}
        }
        return children;
    }

    public int getLength() {
        return value.length;
    }

    public int getTag() {
        return tag;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }
}
