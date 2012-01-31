/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view;

import java.util.Enumeration;
import java.util.Vector;
import org.openxdata.mvac.mobile.util.DateParser;
import org.openxdata.workflow.mobile.model.MQuestionMap;
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

    private Vector appointments = new Vector(0);
    private Vector workItems = new Vector(0);


    public void addElement(Appointment app){
        appointments.addElement(app);

    }

    public void addWorkItem(MWorkItem workItem){
        workItems.addElement(workItem);

    }

    public MWorkItem WorkItemAt(int i){

        if(workItems.size()>0 && workItems.size()>=i&&i>-1){
            return (MWorkItem)workItems.elementAt(i);
        }else{
            return null;
        }

    }

    public Appointment elementAt(int i){

        if(appointments.size()>0 && appointments.size()>=i&&i>-1){
            return (Appointment)appointments.elementAt(i);
        }else{
            return null;
        }

    }


    public String getChild_dob() {
        return child_dob;
    }


    public String getCaretaker_nid() {
        return caretaker_nid;
    }


    public Vector getAppointments(){
        return appointments;
    }
    
    public Vector getWorkItems(){
        return workItems ;
//        return quicksort(workItems, 0, workItems.size()-1);
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


  private Vector quicksort(Vector array, int left, int right) {
        int i = left;
        int j = right;
        Object h;

        long x = -1 ;
        Object objectx = array.elementAt((right + left) / 2);
        MWorkItem mWorkItem = (MWorkItem)objectx ;
         x = getTimeStamp(mWorkItem);
         
        

        //do Partition
        do {
            
            while ( (i >= 0) && getTimeStamp(((MWorkItem)array.elementAt(i))) < x) {
                i++;
            }
            while ( (j >= 0) && getTimeStamp(((MWorkItem)array.elementAt(j))) > x) {
                j--;
            }
            if (i <= j) {

                h = array.elementAt(i);

                //swap a[i] and a[j] a[i] = a[j]

                Object tempj = array.elementAt(j);
                array.removeElementAt(i);
                array.insertElementAt(tempj, i);

                array.removeElementAt(j);
                array.insertElementAt(h, j);
                i++;
                j--;
            }

        } while (i < j);
        if (left < j) {
            quicksort(array, left, j);
        }
        if (i < right) {
            quicksort(array, i, right);
        }

        return array;
    }
        
        
        private long getTimeStamp(MWorkItem wir){
            long resp = -1 ;

            Vector v = wir.getPrefilledQns() ;
            for(Enumeration e = v.elements() ; e.hasMoreElements(); ){

                MQuestionMap qnMap = (MQuestionMap)e.nextElement();
                String quename = qnMap.getQuestion() ;
                if(quename.equals("scheduled_date")){
                    resp = DateParser.getTymeStamp(qnMap.getValue(), "-");
                    break;
                }
            }
            
            return  resp ;
        }

        public void sort(){
            quicksort(workItems, 0, workItems.size()-1);
        }




    

    

}
