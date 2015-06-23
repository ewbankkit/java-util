//
// Copyright (C) Capital One Labs.
//

package com.capitalonelabs.eucalyptus.ledger.bucket.transfer;

import com.capitalonelabs.eucalyptus.ledger.JacksonTreeSerdeFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public final class BucketTransferValueSerdeFactoryUnitTest {
    private SerdeFactory<JsonNode> serdeFactory;

    @Before
    public void before() {
        serdeFactory = new JacksonTreeSerdeFactory();
    }

    @Test
    public void testSerde() {
        String s = "{\n" +
            "\"debit_bucket_id\" : \"DEBIT ID\",\n" +
            "\"credit_bucket_id\" : \"CREDIT ID\",\n" +
            "\"currency\": \"USD\",\n" +
            "\"amount\" : 110.55,\n" +
            "\"description\" : \"DESCRIPTION\",\n" +
            "\"ts\": 123456789\n" +
            "}";
        Serde<JsonNode> serde = serdeFactory.getSerde(null, null);
        JsonNode jsonNode = serde.fromBytes(s.getBytes());
        Assert.assertNotNull(jsonNode);
        BucketTransferValue bucketTransferValue = BucketTransferValue.fromJson(jsonNode);
        Assert.assertNotNull(bucketTransferValue);
        Assert.assertEquals("CREDIT ID", bucketTransferValue.creditBucketId);
        Assert.assertEquals(new BigDecimal("110.55"), bucketTransferValue.amount);
    }
}
