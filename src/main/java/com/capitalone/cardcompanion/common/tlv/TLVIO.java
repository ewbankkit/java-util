//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.tlv;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * TLV I/O.
 */
public final class TLVIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(TLVIO.class);

    /**
     * Parses TLV-encoded data.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Optional<? extends Map<Integer, TLV>> read(ByteSource byteSource) throws IOException {
        Preconditions.checkNotNull(byteSource);

        byte[] encoded = byteSource.read();
        ImmutableMap.Builder<Integer, TLV> mapBuilder = ImmutableMap.builder();
        int i = 0;
        while (i < encoded.length) {
            try {
                // Tag.
                int tag = encoded[i++] & 0xFF;
                if ((tag & 0x1F) == 0x1F) {
                    tag <<= 8;
                    tag |= encoded[i++] & 0xff;
                }

                // Length.
                int valueLen = encoded[i++] & 0xFF;
                if (valueLen > 127) {
                    int n = valueLen - 128;
                    valueLen = 0;
                    while (n > 0) {
                        valueLen = (valueLen << 8) + (encoded[i++] & 0xFF);
                        n--;
                    }
                }

                // Value.
                byte[] value = new byte[valueLen];
                System.arraycopy(encoded, i, value, 0, valueLen);
                i += valueLen;
                mapBuilder.put(Integer.valueOf(tag), new TLV(tag, value));
            }
            catch (IndexOutOfBoundsException ex) {
                LOGGER.warn("Invalid TLV encoding", ex);
                return Optional.absent();
            }
        }

        return Optional.of(mapBuilder.build());
    }

    /**
     * Writes a TLV.
     */
    public static void write(TLV tlv, ByteSink byteSink) throws IOException {
        Preconditions.checkNotNull(tlv);
        Preconditions.checkNotNull(byteSink);

        try (OutputStream outputStream = byteSink.openStream()) {
            int tag = tlv.getTag();
            if (tag > 0xFF) {
                outputStream.write((tag >> 8) & 0xFF);
            }
            outputStream.write(tag & 0xFF);
            outputStream.write(tlv.getLength());
            outputStream.write(tlv.getValue());
        }
    }
}
