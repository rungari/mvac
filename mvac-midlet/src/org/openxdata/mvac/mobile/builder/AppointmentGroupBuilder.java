/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.builder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.model.Appointment;
import org.openxdata.mvac.mobile.model.AppointmentWrapper;
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author mutahi
 */
public class AppointmentGroupBuilder implements StorageListener{

    private AppointmentWrapper[] apps = null;;
    private Hashtable appGroups = new Hashtable();
    
    private static AppointmentGroupBuilder instance = null;
    
    private AppointmentGroupBuilder() {
    }

    public static AppointmentGroupBuilder getInstance() {
        if(instance == null) instance = new AppointmentGroupBuilder();
        return instance;
    }

    public AppointmentWrapper[] build() {

        if(apps != null){
            return apps ;
        }

        for (RecordEnumeration re = WFStorage.getWorkItemEnum(); re.hasNextElement();) {
            AppointmentWrapper wrapper = new AppointmentWrapper(this);
            Appointment appointment = new Appointment();
            String child_id = "";

            MWorkItem wir = null;
            try {
                wir = WFStorage.getWorkItem(re.nextRecordId(), this);
                if (wir != null) {
                    appointment.setRecord_id(wir.getDataRecId());

                    Vector preFilledQns = wir.getPrefilledQns();
                    for (int k = 0; k < preFilledQns.size(); k++) {
                        MQuestionMap qnMap = (MQuestionMap) preFilledQns.elementAt(k);
                        String questionName = qnMap.getQuestion();
                        if (questionName.equals("child_name")) {
                            wrapper.setName(qnMap.getValue());
                            wrapper.setName(qnMap.getValue());

                        } else if (questionName.equals("caretaker_name")) {
                            wrapper.setCaretaker(qnMap.getValue());
                            wrapper.setCaretaker(qnMap.getValue());

                        } else if (questionName.equals("child_iis_id")) {
                            child_id = qnMap.getValue();
                            appointment.setChild_id(qnMap.getValue());
                        } else if (questionName.equals("child_dob")) {
                            String dob = qnMap.getValue();
                            if (dob.indexOf("T") >= 0) {
                                dob = dob.substring(0, dob.indexOf("T"));
                            }
                            wrapper.setChild_dob(dob);
                        } else if (questionName.equals("caretaker_nid")) {
                            wrapper.setCaretaker_nid(qnMap.getValue());
                        }
                    }

                    if (appGroups.containsKey(child_id)) {
                        ((AppointmentWrapper) appGroups.get(child_id)).addElement(appointment);
                        ((AppointmentWrapper) appGroups.get(child_id)).addWorkItemID(wir.getRecordId());


                    } else {
                        wrapper.addElement(appointment);
                        appGroups.put(child_id, wrapper);
                        ((AppointmentWrapper) appGroups.get(child_id)).addWorkItemID(wir.getRecordId());
                    }

                }
            } catch (InvalidRecordIDException exception) {
                System.out.println(" ERROR . Exception thrown when fetching item from store ." + exception.getMessage());
            }
        }

        apps = new AppointmentWrapper[appGroups.size()];

        Enumeration e = appGroups.keys();
        int count = 0;

        while (e.hasMoreElements()) {
            String me = e.nextElement().toString();
            AppointmentWrapper wrapper = (AppointmentWrapper) appGroups.get(me);
            apps[count] = wrapper;
            wrapper = null;
            count++;
        }

        appGroups.clear();
        appGroups = null;

        return apps ;

    }

    public void errorOccured(String arg0, Exception arg1) {
        System.out.println(" ERROR : SOMETHING WENT WRONG ");
    }
}
