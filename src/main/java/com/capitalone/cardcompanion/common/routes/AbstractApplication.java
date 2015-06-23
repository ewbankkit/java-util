//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.jaxrs.Jackson2NoJaxbFeature;
import com.capitalone.cardcompanion.common.jaxrs.UnwrappedObjectMapperProvider;
import com.google.common.reflect.Reflection;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

/**
 * Defines the components of the Jersey service.
 */
public abstract class AbstractApplication<T extends AbstractApplication<T>> extends ResourceConfig {
    /**
     * Constructor.
     */
    protected AbstractApplication(Class<T> classOfT, String applicationName) {
        setApplicationName(applicationName);
        // Scan just this package for components.
        packages(true, Reflection.getPackageName(AbstractApplication.class));
        // Recursively scan the application's package for components.
        packages(true, Reflection.getPackageName(classOfT));
        register(Jackson2NoJaxbFeature.class);
        register(UnwrappedObjectMapperProvider.class);
        property(ServerProperties.BV_FEATURE_DISABLE, true);
        property(ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }
}

