/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import org.openxdata.mvac.communication.model.SearchObject;

/**
 *
 * @author mutahi
 */
public class SearchResultRenderer extends Container implements ListCellRenderer{

    private Label childName = new Label();
    private Label chid_IISID = new Label();
    private Label caretakerName = new Label();
    private Label scheduled_date = new Label();
    private Label location = new Label();

    private Label focus = new Label("");
    
    private Container cnt;
    private Container childcnt;
    private Container dateCnt;
    private Container caretakercnt;
    private Container locationCnt ;

    public SearchResultRenderer() {
        setLayout(new BorderLayout());
        init();
    }

    private void init(){
        cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        childcnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        dateCnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        caretakercnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        locationCnt = new Container(new BoxLayout(BoxLayout.X_AXIS));

        childName.getStyle().setBgTransparency(0);
        caretakerName.getStyle().setBgTransparency(0);

        childcnt.addComponent(childName);
        childcnt.addComponent(chid_IISID);

        dateCnt.addComponent(scheduled_date);


        caretakercnt.addComponent(caretakerName);

        locationCnt.addComponent(location);


        cnt.addComponent(childcnt);
        cnt.addComponent(dateCnt);
        cnt.addComponent(caretakercnt);
        cnt.addComponent(locationCnt);
        Border bd = Border.createLineBorder(1, 0x7799bb);

        cnt.getStyle().setBorder(bd, true);
        cnt.getStyle().setBgColor(0xffffff);
        cnt.getStyle().setFgColor(0xffffff);

        addComponent(BorderLayout.CENTER,cnt);

        focus.getStyle().setBgColor(0x7AE969,true);
        focus.getStyle().setFgColor(0x7AE969,true);
        focus.getStyle().setBgTransparency(100);
    }



    public Component getListCellRendererComponent(List list, Object object, int i, boolean arg3) {
        SearchObject searchObject = (SearchObject)object ;

        childName.setText(searchObject.getChild_name());
        chid_IISID.setText(searchObject.getIIS_ID());
        scheduled_date.setText(searchObject.getScheduled_date().substring(0, searchObject.getScheduled_date().indexOf("T")));

        caretakerName.setText(searchObject.getCaretaker_name());
        location.setText(searchObject.getLocation());

        return this;
    }

    public Component getListFocusComponent(List arg0) {
        return focus ;
    }

}
