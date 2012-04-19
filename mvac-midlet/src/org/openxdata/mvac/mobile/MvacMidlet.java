/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.view.LWUITLoginForm;
import org.openxdata.mvac.util.DebugLog;

/**
 *
 * @author soyfactor
 */
public class MvacMidlet extends MIDlet {

    public MvacMidlet() {
        AppUtil.init(this);
    }
    
    protected void startApp() throws MIDletStateChangeException {
        DebugLog.getInstance().log(" SYSTEM READY . About to display ");
        LWUITLoginForm loginForm = new LWUITLoginForm("User Login");
        AppUtil.get().setView(loginForm);
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
    }

    public void exitApp(boolean bln) throws MIDletStateChangeException{
        this.destroyApp(bln);
    }


}
