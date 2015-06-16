/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.restclient;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.netsol.adagent.util.IOUtil;

/**
 * XML message converter.
 */
public abstract class AbstractXmlMessageConverter implements MessageConverter {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:27 AbstractXmlMessageConverter.java NSI";

    @SuppressWarnings("unchecked")
    public final <T> T fromString(String string, Class<T> classOfT) throws Exception {
        Reader reader = null;
        try {
            reader = new StringReader(string);
            return (T)unmarshal(reader, classOfT);
        }
        finally {
            IOUtil.close(reader);
        }
    }

    public final String getMediaType() {
        return "text/xml";
    }

    public final String toString(Object object) throws Exception {
        Writer writer = new StringWriter();
        try {
            marshal(object, writer);
            return writer.toString();
        }
        finally {
            IOUtil.close(writer);
        }
    }

    protected abstract void marshal(Object object, Writer writer) throws Exception;
    protected abstract Object unmarshal(Reader reader, Class<?> clazz) throws Exception;
}
