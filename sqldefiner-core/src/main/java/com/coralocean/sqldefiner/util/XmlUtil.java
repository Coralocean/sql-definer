package com.coralocean.sqldefiner.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;

public class XmlUtil {
    public static Document parseXmlToDom(String sqlPath) throws SAXException, DocumentException {
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return reader.read(new File(sqlPath));
    }
}
