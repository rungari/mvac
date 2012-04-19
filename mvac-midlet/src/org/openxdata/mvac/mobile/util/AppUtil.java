/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.util;

import com.sun.lwuit.Font;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.util.Resources;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import org.openxdata.mvac.mobile.api.MVACController_new;
import org.openxdata.mvac.mobile.api.MvacController;
import org.openxdata.mvac.mobile.util.view.api.IDisplay;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.util.view.api.LWUITDisplay;

/**
 *
 * @author soyfactor
 */
public class AppUtil {
    private static AppUtil instance;
    private Hashtable registry = new Hashtable();
    private static IDisplay display;
    static Style selc = null;

    private AppUtil(){        
    }

    public static void init(MIDlet parent){
        get().putItem(Constants.MIDLET, parent);
        get().putItem(Constants.MIDP_DISPLAY, Display.getDisplay(parent));
        get().putItem(Constants.CONTROLLER, new MvacController());
        //change this to appropriate display object. e.g for LWUIT specific stuff LWUITDisplay or MIDP specific stuff
        display=new LWUITDisplay(parent);
    }


    public static AppUtil get(){
        if (instance!=null) {
            return instance;
        }else{
            instance = new AppUtil();

        }
        return instance;
    }

    public void putItem(String key, Object obj){
        registry.put(key, obj);
    }

    public Object getItem(String key){

        return registry.get(key);
    }

    public void setView(IView view){
        display.setView(view);
    }

    public static Style getSelectStyle() {
        byte tr = (byte)255 ;
        Style selectedStyle = new Style(0x000000, 0x9fd056, Font.getDefaultFont(),tr);
        return selectedStyle ;
    }

    public static Style getunselectStyle() {
        byte tr = (byte)255 ;
        Style selectedStyle = new Style(0x000000, 0x000000, Font.getDefaultFont(),tr);
        return selectedStyle ;
    }

    public static  Resources getResources(){
        if(display instanceof LWUITDisplay){
            return ((LWUITDisplay)display).getResources();
        }else{
            return null;
        }
    }

    public static Resources getFontResources(){
        if(display instanceof LWUITDisplay){
            return ((LWUITDisplay)display).getFontResources();
        }else{
            return null;
        }
    }
    
    


    

}
