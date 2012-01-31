package org.openxdata.mvac.mobile.xml;

import java.util.Vector;
import org.kxml2.kdom.Element;

public class LotNumberHandler implements IElementHandler {
        StringBuffer sf = new StringBuffer();
        //VaccineLots
	
	

                
	//in this funtion u retrieve from the metadata tag and construct ur metadata object
	public Object handle(Element e) {
		
		
		for (int i = 0; i < e.getChildCount(); i++) {
			if (e.getType(i) == Element.ELEMENT) {
                            Element child = (Element)e.getChild(i);
				if(child.getName().equals("CODE")){
                                        sf.append(child.getText(0)+",") ;
                                        //System.out.println("LOT NUMBER "+ i+1 +" ="+child.getName());
                                        //System.out.println("LOT NUMBER "+ (i+1) +" ="+child.getText(0));
                                        
				}
			}
		}
		return sf.toString();

	}

}
