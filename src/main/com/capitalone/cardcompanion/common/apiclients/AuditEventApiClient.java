//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.apiclients;

import com.capitalone.cardcompanion.common.restclient.PostRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;
import java.util.LinkedList;
import java.util.List;

/**
 * Client for posting audit events.
 */
@ThreadSafe
public final class AuditEventApiClient extends InnovationApiClient {
    private AuditEventApiClient() {
        super(String.format("%s/auditevent/v1", BASE_URL));
    }

    public static AuditEventApiClient getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Posts the specified audit event
     * @param event
     */
    public void postAuditEvent(AuditEvent event) throws RestClientException {
        Preconditions.checkNotNull(event);
        PostRequest<AuditEvent, Void> request = new PostRequest<>();
        request.setRequestEntity(event);
        request.setClassOfResponse(Void.class);
        request.setPath("/auditevents");
        request.setHttpHeaders(httpHeaders());
        invoke(request);
    }

    @SuppressWarnings("unused")
    public static final class AuditEvent {
        @JsonProperty("eventName")
        private final String eventName;

        @JsonProperty("nameValuePairs")
        private final List<NameValuePair> nameValuePairsList = new LinkedList<>();

        public AuditEvent(String eventName) {
            this.eventName = eventName;
        }

        /**
         * Adds the specified name/value pairs to the event.
         * @param nameValuePairs Alternating name/value pairs, e.g. k1, v1, k2, v2...
         *                       The toString methods will be called before storing sending them to the audit client
         */
        public void addNameValuePairs(Object...nameValuePairs) {
            Preconditions.checkArgument(nameValuePairs.length % 2 == 0, "nameValuePairs must have an even number of values");
            for ( int i = 0; i < nameValuePairs.length; i+=2) {
                NameValuePair nameValuePair = new NameValuePair(nameValuePairs[i].toString(),nameValuePairs[i+1].toString());
                nameValuePairsList.add(nameValuePair);
           }
        }

    }

    @SuppressWarnings("unused")
    public static final class NameValuePair {
        @JsonProperty("name")
        private final String name;

        @JsonProperty("value")
        private final String value;

        private NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final AuditEventApiClient INSTANCE = new AuditEventApiClient();
    }
}
