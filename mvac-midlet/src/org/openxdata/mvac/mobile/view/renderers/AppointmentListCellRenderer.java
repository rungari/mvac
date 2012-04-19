package org.openxdata.mvac.mobile.view.renderers;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.io.IOException;
import org.openxdata.mvac.mobile.model.Appointment;

/**
 *
 * @author mutahi
 */
public class AppointmentListCellRenderer extends DefaultMVACListCellRenderer {

    private Label scheduledlbl;
    private Label vaccine_name;
    private Label scheduled_date;
    private Container markCont;
    private Container cnt;
    private Container vaccineCnt;
    private Container scheduleCnt;
    private String vaccine_dose = new String();
    private Label mark;
    private Image checkImage;

    public AppointmentListCellRenderer() {
        super();
    }

    private void init() {
        cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        vaccineCnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        scheduleCnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        markCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
    }

    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        init();

        Appointment appointment = (Appointment) value;
        if (appointment.getVaccine_name() != null && appointment.getDose() != null) {
            vaccine_dose = appointment.getVaccine_name() + " : " + appointment.getDose();
        } else {
            if (appointment.getVaccine_name() != null) {
                vaccine_dose = appointment.getVaccine_name();
            } else if (appointment.getDose() != null) {
                vaccine_dose = appointment.getDose();
            }
        }


        vaccine_name = new Label(vaccine_dose);

        String date = appointment.getSchedule_date();
        if (date.indexOf("T") >= 0) {
            date = date.substring(0, date.indexOf("T"));
        }

        scheduledlbl = new Label("Scheduled:");
        scheduled_date = new Label(date);

        String imm_date = appointment.getImmunization_date();
        if (imm_date == null || imm_date.length() < 1) {
            imm_date = "Pending";
        }


        vaccineCnt.addComponent(vaccine_name);
        scheduleCnt.addComponent(scheduledlbl);
        scheduleCnt.addComponent(scheduled_date);


        cnt.addComponent(vaccineCnt);
        cnt.addComponent(scheduleCnt);
        Border bd = Border.createLineBorder(1, 0x7799bb);
        cnt.getStyle().setBorder(bd, true);
        cnt.getStyle().setMargin(0, 10, 0, 0);

        if (isSelected) {
            cnt.getStyle().setBgColor(0x9fd056, true);
            cnt.getStyle().setBgTransparency(150, true);
        } else {
            cnt.getStyle().setBgColor(0xe1e1e1, true);
            cnt.getStyle().setBgTransparency(150, true);
        }


        mark = new Label();
        mark.getStyle().setPadding(0, 0, 0, 5);
        try {
            checkImage = Image.createImage("/greencheck.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if (checkImage != null) {            
            if (appointment.getImmunization_date() == null || appointment.getImmunization_date().length() < 1) {
                Image blank = Image.createImage(checkImage.getWidth(), checkImage.getHeight(),0xffffff);
                mark.setIcon(blank);
            }else{
                 mark.setIcon(checkImage);
            }
        } 


        markCont.addComponent(mark);
        addComponent(BorderLayout.WEST, markCont);
        addComponent(BorderLayout.CENTER, cnt);

        return this;
    }

    public String getVaccine_dose() {
        return vaccine_dose;
    }
}
