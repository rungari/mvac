
package org.openxdata.mvac.util;

/**
 * Other general utility functions
 *
 * @author mutahi
 */
public class Utils {

    private Utils() {
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public static boolean isEmpty(String s){
        return  s == null ? true :
            (s.length() == 0 ? true :
                (s.trim().equals("") ? true :
                    false) );
    }

   
}
