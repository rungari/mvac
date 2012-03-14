/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view.renderers;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import org.openxdata.mvac.mobile.model.AppointmentWrapper;

/**
 *
 * @author mutahi
 */
public class GroupListCellRenderer extends DefaultMVACListCellRenderer {

    private Label child_dob;
    private Label caretaker_id;
    private Label name;
    private Label caretaker;
    private Container cnt;
    private Container childcnt;
    private Container caretakercnt;

    public GroupListCellRenderer() {
        super();
    }

    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {

        cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        childcnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        caretakercnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));



        AppointmentWrapper appointment = (AppointmentWrapper) value;
        name = new Label("Childname :" + appointment.getName());

        child_dob = new Label("Child DOB :" + appointment.getChild_dob());

        caretaker_id = new Label("Caretaker ID :" + (appointment.getCaretaker_nid() != null ? (appointment.getCaretaker_nid().length() > 1 ? appointment.getCaretaker_nid() : "None") : "None"));

        caretaker = new Label("Caretaker :" + appointment.getCaretaker());

        childcnt.addComponent(name);
        childcnt.addComponent(child_dob);

        caretakercnt.addComponent(caretaker);
        caretakercnt.addComponent(caretaker_id);


        cnt.addComponent(childcnt);
        cnt.addComponent(caretakercnt);
        Border bd = Border.createLineBorder(1, 0x7799bb);

        cnt.getStyle().setMargin(5,5, 0, 0);
        cnt.getStyle().setBorder(bd, true);

        if (isSelected) {
            cnt.getStyle().setBgColor(0x9fd056, true);
            cnt.getStyle().setBgTransparency(150, true);
        } else {
            cnt.getStyle().setBgColor(0xe1e1e1, true);
            cnt.getStyle().setBgTransparency(150, true);
        }
        addComponent(BorderLayout.CENTER, cnt);

        return this;
    }
}
