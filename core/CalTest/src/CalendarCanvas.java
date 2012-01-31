/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Date;
import java.util.Hashtable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;


/**
 *
 * @author openmrs
 */
public class CalendarCanvas extends Canvas implements CommandListener{

    CalendarWidget calendar = null;
    MIDlet midlet = null;
    
    private Command cmdback = new Command("Back", Command.BACK, 0);

    public CalendarCanvas(CalTestMidlet mIDlet) {
        this.midlet = mIDlet ;
        calendar = new CalendarWidget(new Date());

        calendar.headerFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
        calendar.weekdayFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        calendar.weekdayBgColor = 0xccccff;
        calendar.weekdayColor = 0x0000ff;
        calendar.headerColor = 0xffffff;
        calendar.initialize();

        this.addCommand(cmdback);
        this.setCommandListener(this);

    }

    protected void keyPressed(int key) {
        System.out.println(" Keycode is :" + key);
        int keyCode = getGameAction(key);
        System.out.println(" Keycode is :" + keyCode);
        if (keyCode == FIRE || keyCode == KEY_NUM5) {
//			Display.getDisplay(midlet).setCurrent(
//				new Alert("Selected date", calendar.getSelectedDate().toString(), null, AlertType.CONFIRMATION)
//			);
            System.out.println(" Selected date is :" + calendar.getSelectedDate());
        } else {
            calendar.keyPressed(keyCode);

            repaint();
        }
    }

    protected void paint(Graphics g) {
        g.setColor(0xffffff);
        g.fillRect(0, 0, getWidth(), getHeight());

        calendar.paint(g);
    }

    public void commandAction(Command c, Displayable d) {
        if(c == cmdback){
            
        }
    }
}
