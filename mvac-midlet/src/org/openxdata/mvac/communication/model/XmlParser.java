/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.communication.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * @author mutahi
 */
public class XmlParser {

    private static Hashtable myHandlers;

    public XmlParser() {
        initProcessingRules();
    }

    private static void initProcessingRules() {
        IElementHandler searchHandler = (IElementHandler) new SearchHandler();
        myHandlers = new Hashtable();
        myHandlers.put("DocumentElement", searchHandler);
    }

    public Object processXml(Reader reader) {
System.out.println("<<<<<<<<<<<<<<<< @ processXML");
        //retrieve XML Doc
        Document doc = getXMLDocument(reader);

        // closing the reader
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader :" + e.toString());
        }


        if (doc != null) {
System.out.println("<<<<<<<<<<<<<<<<<<<<<< Parsing was successful");
            try {
                return getResults(doc);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

        

    }
    
    private Object getResults(Document doc) {

        return parseElement(doc.getRootElement(), myHandlers);

    }

    private Object parseElement(Element e, Hashtable myHandlers2) {
        String name = e.getName();
System.out.println("Root Name :" + name);

        IElementHandler eh = (IElementHandler) myHandlers2.get(name);

        if (eh != null && name.equals("DocumentElement")) {
            return eh.handle(e);

        } else{
            //do noting or continue to look for next tag
            return null;
        }

    }

    public static Document getXMLDocument(Reader reader) {

        Document doc = new Document();

        try {
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            doc.parse(parser);
        } catch (Exception e) {
            System.err.println("XML Syntax Error!");
            e.printStackTrace();

            return null;
        }

        return doc;
    }

}
