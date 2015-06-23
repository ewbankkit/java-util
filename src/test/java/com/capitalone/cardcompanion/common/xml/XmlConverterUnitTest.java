//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.xml;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test XML conversion.
 */
public final class XmlConverterUnitTest {

    @Test
    public void testToXmlDocument() throws Exception {
        XmlConverter<ObjectToConvert> xmlConverter = new XmlConverter<>(ObjectToConvert.class);
        ObjectToConvert object = new ObjectToConvert();
        object.setaProp("aValue");
        object.setbProp("bValue");
        String xml = xmlConverter.toXmlDocument(object, "anObject");
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }

    @Test
    public void testToXmlFragment() throws Exception {

        XmlConverter<ObjectToConvert> xmlConverter = new XmlConverter<>(ObjectToConvert.class);
        ObjectToConvert object = new ObjectToConvert();
        object.setaProp("aValue");
        object.setbProp("bValue");
        String xml = xmlConverter.toXmlFragment(object, "anObject");
        assertNotNull(xml);
        assertTrue(xml.length() > 0);
    }
}
