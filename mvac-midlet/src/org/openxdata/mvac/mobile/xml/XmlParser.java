package org.openxdata.mvac.mobile.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;

public class XmlParser {
	
	static Hashtable myHandlers;
	
	static {
		initProcessingRules();
		
	}
	
	
	private static void initProcessingRules (){
		IElementHandler lothandler = new LotNumberHandler();
		//create handlers for different types of tags . Not all. just the important ones like question handler.
		//Its up to ur discretion / design method
		
		myHandlers = new Hashtable();
		
		myHandlers.put("VaccineLots", lothandler); // store the meta tag handler so we use it later
		
		
	}
	
	
	public Object processXml(Reader reader){
		
		//retrieve XML Doc
		Document doc = getXMLDocument(reader);
		
		// close the reader
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Error closing reader");
			e.printStackTrace();
		}
		
		
		if (doc != null) {
			try {
				return getMeta(doc);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
		
	}
	
	
	
	private Object getMeta(Document doc) {
		//Object obj= parseElement(doc.getRootElement(), myHandlers);
            Object obj=null;
                Element root = doc.getRootElement();
                
                int childCount = root.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        
                        if (root.getType(i) == Element.ELEMENT) {
                            Element el = (Element)root.getChild(i);
                            if (el.getName().equals("VaccineLots")) {
                                obj=parseElement(el, myHandlers);
                                
                            }
                            
                            break;
                        }
                        
                        
                    }
		return obj;
	}


	private Object parseElement(Element e, Hashtable myHandlers2) {
		String name = e.getName();
                Object object = null;
		
		IElementHandler eh = (IElementHandler)myHandlers2.get(name);
		
		if (eh != null && name.equals("VaccineLots")) {
			
			object = eh.handle(e);
			
		}else{
			//do noting or continue to look for next tag
                    System.out.println("Inside else");
			
		}
                
                return object;
	}


	public static Document getXMLDocument(Reader reader){
		Document doc = new Document();

		try{
			KXmlParser parser = new KXmlParser();
			parser.setInput(reader);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			
			doc.parse(parser);
		} catch(Exception e){
			System.err.println("XML Syntax Error!");
			e.printStackTrace();

			return null;
		}

		return doc;
	}

}
