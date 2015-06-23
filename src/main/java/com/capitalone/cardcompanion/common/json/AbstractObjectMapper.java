//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Abstract base class for Jackson object mappers.
 */
@ThreadSafe
abstract class AbstractObjectMapper {
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     */
    protected AbstractObjectMapper(boolean wrapped) {
        objectMapper = new ObjectMapper();
        // Equivalent to @JsonIgnoreProperties(ignoreUnknown = true).
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, wrapped);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapped);
        objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.registerModule(new JodaModule());
        // Include only non-null.
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
