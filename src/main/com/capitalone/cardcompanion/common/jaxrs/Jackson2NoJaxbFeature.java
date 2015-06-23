//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.CommonProperties;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Feature used to register Jackson 2.x JSON provider without support for JAXB annotations.
 */
@Provider
public final class Jackson2NoJaxbFeature implements Feature {
    @Override
    public boolean configure(final FeatureContext context) {
        String runtimeType = context.getConfiguration().getRuntimeType().name().toLowerCase();
        final String disableMoxy = String.format("%s.%s", CommonProperties.MOXY_JSON_FEATURE_DISABLE, runtimeType);
        context.property(disableMoxy, true);

        context.register(JsonParseExceptionMapper.class);
        context.register(JsonMappingExceptionMapper.class);
        context.register(JacksonJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);

        return true;
    }
}
