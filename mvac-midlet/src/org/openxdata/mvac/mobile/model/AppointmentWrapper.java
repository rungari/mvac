/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.model;


import org.openxdata.mvac.mobile.model.Appointment;
import java.util.Vector;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author soyfactor
 */
public class AppointmentWrapper {

    private String name;
    private String caretaker;
    private String child_dob;
    private String caretaker_nid;
    private StorageListener listener = null;
    private Vector appointments = new Vector(0);
    private Vector workItemsIDs = new Vector(0);

    public AppointmentWrapper(StorageListener listener) {
        this.listener = listener;
    }

    public void addElement(Appointment app) {
        appointments.addElement(app);

    }

    public void addWorkItemID(int workItem) {
        workItemsIDs.addElement(Integer.toString(workItem));

    }

    public MWorkItem WorkItemAt(int i) {

        if (workItemsIDs.size() > 0 && workItemsIDs.size() >= i && i > -1) {
            return WFStorage.getWorkItem(Integer.parseInt(workItemsIDs.elementAt(i).toString()), listener);
        } else {
            return null;
        }

    }

    public Appointment elementAt(int i) {

        if (appointments.size() > 0 && appointments.size() >= i && i > -1) {
            return (Appointment) appointments.elementAt(i);
        } else {
            return null;
        }

    }

    public String getChild_dob() {
        return child_dob;
    }

    public String getCaretaker_nid() {
        return caretaker_nid;
    }

    public Vector getAppointments() {
        return appointments;
    }

    public Vector getWorkItems() {
        return workItemsIDs;
    }

    public String getCaretaker() {
        return caretaker;
    }

    public String getName() {
        return name;
    }

    public void setCaretaker_nid(String caretaker_nid) {
        this.caretaker_nid = caretaker_nid;
    }

    public void setChild_dob(String child_dob) {
        this.child_dob = child_dob;
    }

    public void setCaretaker(String caretaker) {
        this.caretaker = caretaker;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
