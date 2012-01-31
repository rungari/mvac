/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.communication.model;

import java.util.Vector;
import org.kxml2.kdom.Element;

/**
 *
 * @author mutahi
 */
public class SearchHandler implements IElementHandler{

    private String appID = null;
    private String childName = null;
    private String scheduledDate = null;
    private String iisID = null;
    private String caretakerName = null;
    private String location = null;

    private Vector resp = new Vector(0);

    public Object handle(Element e) {
System.out.println("Searchhandler.handle");
        for (int i = 0; i < e.getChildCount(); i++) {

            if (e.getType(i) != Element.ELEMENT) {
                continue;
            }

            Element kid = e.getElement(i);

            if (!kid.getName().equals("Appointments")) {

                continue;
            }

            for (int j = 0; j < kid.getChildCount(); j++) {


                if (kid.getType(j) != Element.ELEMENT) {
                    continue;
                }

                Element body = kid.getElement(j);

                if(body.getName().equals("appointment_id")){
                    this.appID = body.getText(0).trim();
                }
                else if(body.getName().equals("child_name")){
                    childName = body.getText(0).trim();
                }
                else if(body.getName().equals("scheduled_date")){
                    scheduledDate = body.getText(0).trim();
                }
                else if(body.getName().equals("child_iis_id")){
                    iisID = body.getText(0).trim();
                }
                else if(body.getName().equals("caretaker_name")){
                    caretakerName = body.getText(0).trim();
                }
                else if(body.getName().equals("location")){
                    location = body.getText(0).trim();
                }

            }

            SearchObject searchObject = new SearchObject(appID, childName, scheduledDate, iisID, caretakerName, location);
            resp.addElement(searchObject);



        }
        return resp;
    }

}
