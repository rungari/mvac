/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordComparator;
import org.openxdata.db.util.StorageListener;
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author mutahi
 */
public class WFDateSorter implements RecordComparator , StorageListener{


    public int compare(byte[] rec1, byte[] rec2) {
        int resp = -1 ;
        try{
        ByteArrayInputStream bais1 = new ByteArrayInputStream(rec1);
        DataInputStream dis1 = new DataInputStream(bais1);
        MWorkItem mwi1 = new MWorkItem();
        mwi1.read(dis1);

        ByteArrayInputStream bais2 = new ByteArrayInputStream(rec2);
        DataInputStream dis2 = new DataInputStream(bais2);
        MWorkItem mwi2 = new MWorkItem();
        mwi2.read(dis2);

        /**
         * TIME TO COMPARE DATES
         */
        if((getTimeStamp(mwi1)) > (getTimeStamp(mwi2))){
            resp = RecordComparator.FOLLOWS ;
        }else if((getTimeStamp(mwi1)) == (getTimeStamp(mwi2))){
            resp = RecordComparator.EQUIVALENT;
        }else if((getTimeStamp(mwi1)) < (getTimeStamp(mwi2))){
            resp = RecordComparator.PRECEDES;
        }


        }catch(Exception e){
            System.out.println(" ERROR . When sorting dates ." + e.getMessage());
        }
        return resp ;

    }

    public void errorOccured(String arg0, Exception arg1) {
        System.out.println(" ERROR occured in WRDAteSorter");
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

}
