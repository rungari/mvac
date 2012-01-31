/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.communication.model;

/**
 *
 * @author mutahi
 */
public class SearchObject {
    
    private String appointment_ID = null;
    private String child_name = null;
    private String scheduled_date = null;
    private String IIS_ID = null;
    private String caretaker_name = null;
    private String location = null;

    public SearchObject(String appointmentID , String childName , String scheduledDate , String IISID , String caretakerName , String location) {
        this.appointment_ID = appointmentID ;
        this.child_name = childName ;
        this.scheduled_date = scheduledDate ;
        this.IIS_ID = IISID ;
        this.caretaker_name = caretakerName ;
        this.location = location ;
    }

    public String getAppointment_ID() {
        return appointment_ID;
    }

    public void setAppointment_ID(String appointment_ID) {
        this.appointment_ID = appointment_ID;
    }

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }

    public String getScheduled_date() {
        return scheduled_date;
    }

    public void setScheduled_date(String scheduled_date) {
        this.scheduled_date = scheduled_date;
    }

    public String getIIS_ID() {
        return IIS_ID;
    }

    public void setIIS_ID(String IIS_ID) {
        this.IIS_ID = IIS_ID;
    }

    public String getCaretaker_name() {
        return caretaker_name;
    }

    public void setCaretaker_name(String caretaker_name) {
        this.caretaker_name = caretaker_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
