/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author mutahi
 */
public class DateParser {

    /**
     * Parses Date Strying 'yyyy-mm-dd'
     * @param original
     * @return
     */
    private static String[] split(String original , String sep) {
        Vector nodes = new Vector();
        String separator = sep;
        original = original.substring(0, original.indexOf("T"));
//        System.out.println("split start................... :" + original);

        //Parse Nodes into the vector
        int index = original.indexOf(separator);
        /**
         * TODO : Define better splitter 
         */
        
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }

        }

        return result;
    }



    /**
     * Converts 'string' date to timestamp
     * String Fromat 'yyyy-mm-dd'
     * @param date - String Date
     * @param separator 
     * @return
     */

    public static long getTimestamp(String date , String separator) {

        Calendar cal = Calendar.getInstance();
        Date d = new Date(0);

        String[] dA = split(date , separator);

        if(dA[0] != null ) cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dA[0]));
        System.out.println(" Month is : " + Integer.parseInt(dA[1]));
        if(dA[1] != null )cal.set(Calendar.MONTH, Integer.parseInt(dA[1])-1);
        if(dA[2] != null )cal.set(Calendar.YEAR, Integer.parseInt(dA[2]));

        d = cal.getTime();
        return d.getTime();
    }

    public static String dateToString(long timestamp){
        String resp = null;
        Calendar c = Calendar.getInstance();
        Date d = new Date(timestamp);

        c.setTime(d);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        resp = ""+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec ;


        return resp ;
    }
    
    public static String getStringDate(Date date){
        String respDate = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH) ;
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR) ;
        
        String dayStr="";
        
        

        respDate = day+"-"+month+"-"+year ;


        return respDate;
    }
    
    public static String getMvacStringDate(Date date){
        String respDate = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH) ;
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR) ;
        
        String dayStr="";
        
        String monthStr="";
        
        if(day<10){
            dayStr="0"+day;
        }else{
            dayStr=""+day;
        }
        
        if(month<10){
            monthStr="0"+month;
        }else{
            monthStr=""+month;
        }
        
        

        respDate = year+"-"+monthStr+"-"+dayStr ;


        return respDate;
    }


    /**
     * 
     * @param date
     * @param separator
     * @return
     */
    public static long getTymeStamp(String date , String separator){

        Calendar cal = Calendar.getInstance();
        Date d = new Date(0);

        String[] dA = split(date , separator);

        if(dA[0] != null ) cal.set(Calendar.YEAR, Integer.parseInt(dA[0]));
        if(dA[1] != null )cal.set(Calendar.MONTH, Integer.parseInt(dA[1])-1);
        if(dA[2] != null )cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dA[2]));

        d = cal.getTime();
        return d.getTime();

    }
}