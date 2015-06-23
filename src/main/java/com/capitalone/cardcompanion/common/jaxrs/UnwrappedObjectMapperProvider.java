//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.capitalone.cardcompanion.common.json.UnwrappedObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Feature used to create a Jackson object mapper for unwrapped objects.
 */
@Provider
public final class UnwrappedObjectMapperProvider implements ContextResolver<ObjectMapper> {
    /**
     * Returns an object mapper that is applicable to the supplied type.
     */
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return UnwrappedObjectMapper.getInstance();
    }
}
