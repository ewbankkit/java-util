//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.servlet

import com.capitalone.cardcompanion.common.Config
import com.capitalone.cardcompanion.common.Descriptor
import com.capitalone.cardcompanion.common.TomcatDomain
import com.google.common.base.Optional
import groovy.util.logging.Slf4j

import javax.annotation.Nullable
import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.util.jar.Attributes
import java.util.jar.Manifest

import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION

/**
 * Base class for listening starting/stopping orchestrator servlets. It initializes the name/version, sets up logging
 * and provides hooks for doing custom startup/shutdown.
 */
@Slf4j
abstract class AbstractServletContextListener implements ServletContextListener {
    /** the name for the app if not found in the manifest */
    private final String name

    protected AbstractServletContextListener() {
        this(null)
    }

    protected AbstractServletContextListener(@Nullable String name) {
        if (name) {
            Config.name = name
        }
        this.name = name
    }

    @Override
    final void contextInitialized(ServletContextEvent sce) {
        setDescriptor sce
        setTomcatDomain sce

        Descriptor descriptor = Descriptor.instance
        log.info '{} {}/{} starting...', descriptor.name, descriptor.version, Config.environmentName

        doStart sce
    }

    @Override
    final void contextDestroyed(ServletContextEvent sce) {
        doStop sce

        Descriptor descriptor = Descriptor.instance
        log.info '{} {}/{} finished', descriptor.name, descriptor.version, Config.environmentName
    }

    protected abstract void doStart(ServletContextEvent sce)
    protected abstract void doStop(ServletContextEvent sce)

    private void setDescriptor(ServletContextEvent sce) {
        ServletContext sc = sce.servletContext
        try {
            URL mainfestUrl = sc.getResource("/META-INF/MANIFEST.MF")
            if (mainfestUrl) {
                mainfestUrl.withInputStream {
                    inputStream ->
                        Manifest manifest = new Manifest(inputStream)
                        Attributes attributes = manifest.mainAttributes
                        Descriptor descriptor = Descriptor.instance
                        descriptor.name = attributes.getValue IMPLEMENTATION_TITLE
                        descriptor.version = attributes.getValue IMPLEMENTATION_VERSION
                }
            }
            else {
                sc.log 'Warning: no manifest file found (exploded war?)'
                Descriptor descriptor = Descriptor.instance
                descriptor.name = name
                descriptor.version = 'Unknown.Version'
            }
        }
        catch (IOException ex) {
            sc.log 'Unable to read manifest', ex
        }
    }

    private static void setTomcatDomain(ServletContextEvent sce) {
        // Set the domain name.
        ServletContext sc = sce.servletContext
        final String domainNameProperty = 'tomcat.domain'
        if (!System.getProperty(domainNameProperty)) {
            Optional<String> tomcatDomainName = TomcatDomain.instance.name
            if (tomcatDomainName.present) {
                System.setProperty(domainNameProperty, tomcatDomainName.get())
            }
            else {
                sc.log 'Unable to determine Tomcat domain name'
            }
        }
    }
}
