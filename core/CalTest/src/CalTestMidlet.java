
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mutahi
 */
public class CalTestMidlet extends MIDlet{

    private Display d = null;

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void pauseApp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void startApp() throws MIDletStateChangeException {
        d = Display.getDisplay(this);
        d.setCurrent(new CalendarCanvas( this));
    }

}
