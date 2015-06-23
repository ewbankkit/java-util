//
// Copyright (C) Capital One Labs.
//

package com.capitalonelabs.eucalyptus.ledger.bucket;

import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class BucketValueSerdeFactoryUnitTest {
    private SerdeFactory<BucketValue> serdeFactory;

    @Before
    public void before() {
        serdeFactory = new BucketValueSerdeFactory();
    }

    @Test
    public void testSerde() {
        String s = "{\n" +
            "\"description\" : \"DESCRIPTION ABC\",\n" +
            "\"owner_id\" : \"OWNER 001\",\n" +
            "\"currency\": \"USD\",\n" +
            "\"private_key\" : [1, 2, 3],\n" +
            "\"public_key\" : [9, 8, 7]\n" +
            "}";

        Serde<BucketValue> serde = serdeFactory.getSerde(null, null);
        BucketValue bucketValue = serde.fromBytes(s.getBytes());
        Assert.assertNotNull(bucketValue);
        Assert.assertEquals("DESCRIPTION ABC", bucketValue.description);
    }
}
