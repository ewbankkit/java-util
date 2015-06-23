package com.capitalone.cardcompanion.common.apiclients;

import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AuditEventApiClientUnitTest {

    private AuditEventApiClient auditEventApiClient;

    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "local");
        auditEventApiClient = AuditEventApiClient.getInstance();
    }

    @Test
    public void testPostAuditEvent() throws Exception {
        // just a basic test to make sure that posting an audit event doesn't error out
        AuditEventApiClient.AuditEvent auditEvent = new AuditEventApiClient.AuditEvent("testEvent");
        auditEvent.addNameValuePairs("name1","value1");
        auditEvent.addNameValuePairs("name2","value2");
        auditEventApiClient.postAuditEvent(auditEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuditEventFailsWithOddNumberOfParameters() {
        AuditEventApiClient.AuditEvent auditEvent = new AuditEventApiClient.AuditEvent("testEvent");
        auditEvent.addNameValuePairs("name1","value1","name2");
    }

    @Test
    public void testEventWithNonStringValues() throws Exception {
        Object anObject = new Object() {
            @Override
            public String toString() {
                return "testString";
            }
        };
        AuditEventApiClient.AuditEvent auditEvent = new AuditEventApiClient.AuditEvent("testEvent");
        auditEvent.addNameValuePairs("name1",anObject);
        List<AuditEventApiClient.NameValuePair> nameValuePairs = (List<AuditEventApiClient.NameValuePair>)
                PrivateAccessor.getField(auditEvent,"nameValuePairsList");
        assertEquals(1,nameValuePairs.size());
        AuditEventApiClient.NameValuePair nameValuePair = nameValuePairs.get(0);
        assertEquals("name1",PrivateAccessor.getField(nameValuePair,"name"));
        assertEquals("testString",PrivateAccessor.getField(nameValuePair,"value"));
    }

}
