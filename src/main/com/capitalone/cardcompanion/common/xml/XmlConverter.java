//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.xml;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * XML converter.
 */
@ThreadSafe
public final class XmlConverter<T> {
    private final Class<T>    classOfT;
    private final JAXBContext jaxbContext;

    /**
     * Constructor.
     */
    public XmlConverter(Class<T> classOfT) throws JAXBException {
        this.classOfT = classOfT;
        jaxbContext = JAXBContext.newInstance(classOfT);
    }

    /**
     * Returns an XML document representation of the specified object.
     */
    public String toXmlDocument(T t, String qualifiedName) throws IOException, JAXBException {
        return toXml(t, qualifiedName, false);
    }

    /**
     * Returns an XML fragment representation of the specified object.
     */
    public String toXmlFragment(T t, String qualifiedName) throws IOException, JAXBException {
        return toXml(t, qualifiedName, true);
    }

    /**
     * Returns an XML representation of the specified object.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    private String toXml(T t, String qualifiedName, boolean fragment) throws IOException, JAXBException {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(qualifiedName);

        JAXBElement<T> jaxbElement = new JAXBElement<>(new QName(qualifiedName), classOfT, t);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(fragment));
        try (Writer writer = new StringWriter()) {
            marshaller.marshal(jaxbElement, writer);
            return writer.toString();
        }
    }
}
