/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.CoordinateLayout;
import java.util.Hashtable;
import org.openxdata.mvac.mobile.util.view.api.IView;






/**
 *
 * @author mutahi
 */
public class DownloadDialog extends Dialog implements ActionListener {
    
    private Container container = null;
    private TextArea msg = null;
    private String message = null;
    private Command cmdOk = null;
    private Command cmdback = null;
    private IView parent = null;

    public static final String OK = "ok" ;
    public static final String BACK = "Back" ;

    public DownloadDialog(IView iView ,String message) {
        this.message = message ;
        this.parent = iView ;
        setAutoDispose(false);
        setLayout(new BorderLayout());
        init();
        addCommandListener(this);
    }

    private void init(){
        int w = getWidth();
        int h = getHeight();

        container = new Container(new CoordinateLayout(w, h));

        msg = new TextArea(4,4);
        msg.setText(message);
        msg.setAlignment(CENTER);
        msg.setFocusable(false);
        msg.setX(5);
        msg.setY(h/3);
        msg.setPreferredW(140);

        container.addComponent(msg);
        addComponent(BorderLayout.CENTER ,container);

        cmdOk = new Command("OK");
        cmdback = new Command("BACK");

        addCommand(cmdOk);
        addCommand(cmdback);


    }

    public void actionPerformed(ActionEvent actionEvent) {
        Hashtable resp = new Hashtable();
        if(actionEvent.getSource() == cmdOk){
            resp.put(OK, "Ok");
            
        }else if(actionEvent.getSource() == cmdback){
            resp.put(BACK, "back");
        }
        parent.resume(resp);
    }

    



}
