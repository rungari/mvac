package org.openxdata.mvac.mobile.xml;

import org.kxml2.kdom.Element;


//each major tag is handled by an element handler e.g for questions or meta data
public interface IElementHandler {
	
	public Object handle (Element e);

}
