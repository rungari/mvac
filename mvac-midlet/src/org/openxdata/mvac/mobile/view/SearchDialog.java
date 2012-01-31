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
public class SearchDialog extends Dialog implements ActionListener{

    private Container container = null;
    private TextArea msg = null;
    private String message = null;
    private Command cmdOk = null;
    private Command cmdView = null;
    private Command cmdDiscard = null;
    private Command cmdCancel = null;
    private IView parent = null;

    public static final int RANGE_11_50 = 100 ;
    public static final int RANGE_OVER50 = 101 ;

    public static final String OK = "ok" ;
    public static final String CANCEL = "cancel" ;
    public static final String VIEW = "view" ;
    public static final String DISCARD = "discard" ;

    public SearchDialog(IView iView ,String message , int type) {
        this.message = message ;
        this.parent = iView ;
        setAutoDispose(false);
        setLayout(new BorderLayout());
        init();
        addCommandListener(this);

        if(type == RANGE_11_50){
            addCommand(cmdView);
            addCommand(cmdDiscard);
        }else if(type == RANGE_OVER50){
            addCommand(cmdOk);
            addCommand(cmdCancel);
        }
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
        cmdCancel = new Command("CANCEL");
        cmdView = new Command("VIEW");
        cmdDiscard = new Command("DISCARD");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object src = actionEvent.getSource() ;
        Hashtable resp = new Hashtable();
        if(src == cmdOk){
            resp.put(OK, "ok");
        }else if(src == cmdCancel){
            resp.put(CANCEL, "cancel");
        }else if(src == cmdView){
            resp.put(VIEW, "view");
        }else if(src == cmdDiscard){
            resp.put(DISCARD, "discard");
        }
        parent.resume(resp);
    }

}
