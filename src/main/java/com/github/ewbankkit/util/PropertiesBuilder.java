/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesBuilder {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:20 PropertiesBuilder.java NSI";

    private final Properties properties = new Properties();

    public PropertiesBuilder load(File file) throws IOException {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            return load(inStream);
        }
        finally {
            IOUtil.close(inStream);
        }
    }

    public PropertiesBuilder load(InputStream inStream) throws IOException {
        properties.load(inStream);
        return this;
    }

    public PropertiesBuilder setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public Properties toProperties() {
        return (Properties)properties.clone();
    }
}
