/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.util.view.api;

import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author soyfactor
 */
public class LWUITDisplay implements IDisplay {

    protected Resources r = null;
    protected Resources fontRes = null;

    public static final String NEWUI_OPTIONS = "Options";
    public static final String NEWUI_SELECT = "Select";
    public static final String NEWUI_CANCEL = "Cancel";

    public LWUITDisplay(MIDlet parent) {
        //ImplementationFactory.setInstance(new MobrizImplementationFactory());
        Display.init(parent);
        initTheme();
    }

    //initalize LWUIT theme here
    private void initTheme() {
        try {
            r = Resources.open("/NDG.res");
//            fontRes = Resources.open("/fonts.res");
            UIManager.getInstance().setThemeProps(r.getTheme("SurveyList"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        

    }

    public Resources getResources(){
        return r ;
    }

    public Resources getFontResources(){
        return fontRes ;
    }

    public void setView(IView view) {
        ((Form) view).show();

    }

    public Object getDisplayObject() {
        //Not supported by LWUIT. OR can return LWUIT display stuff. Whatever... :P
        return null;
    }
}
