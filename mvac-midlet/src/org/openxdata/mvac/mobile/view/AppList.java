/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import java.io.IOException;
import org.openxdata.mvac.mobile.model.listmodel.FilterProxyListModel;
import org.openxdata.mvac.mobile.model.AppointmentWrapper;
import org.openxdata.mvac.mobile.model.Appointment;
import org.openxdata.mvac.mobile.model.listmodel.AppListModel;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.FormData;
import org.openxdata.model.FormDef;
import org.openxdata.model.PageData;
import org.openxdata.model.QuestionData;
import org.openxdata.mvac.mobile.api.FormUtil;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.renderers.AppointmentListCellRenderer;
import org.openxdata.mvac.util.DebugLog;
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author soyfactor
 */
public class AppList extends Form implements IView, StorageListener, ActionListener, FocusListener {

    private Appointment[] apps;
    private List list;
    private TextField field = null;
    private Label searchlbl = null;
    private Vector mWorkItemsList = new Vector(0);
    private Command uploadselected;
    private Command uploadall;
    private FormUtil formutil;
    private FormDef formDef;
    private FormData formData;
    private PageData currentPage;
    private Container searchCont = null;
    private Image searchIcon = null;
    private FilterProxyListModel proxyModel = null;
    private Command cmdBack ;

    public AppList(String title) {
        super(title);
        super.getTitleComponent().setAlignment(LEFT);
        initView();
    }

    public AppList(AppointmentWrapper wrapper) {
        super(wrapper.getName());
        super.getTitleComponent().setAlignment(LEFT);
        formutil = new FormUtil();
        mWorkItemsList = getWorkItems(wrapper.getWorkItems());
        initView();
    }

    private void initView() {
        initItems();

        super.getTitleStyle().setFgColor(0x000000);

        uploadselected = new Command("upload selected", 1);
        uploadall = new Command("Upload All", 2);
        addCommandListener(this);



        AppListModel appListModel = new AppListModel(apps);
        proxyModel = new FilterProxyListModel(appListModel);
        list = new List(proxyModel);
        list.setListCellRenderer(new AppointmentListCellRenderer());
        list.setFixedSelection(List.FIXED_NONE);
        list.addActionListener(this);
        setLayout(new BorderLayout());

        setFocused(list);
        list.setSelectedIndex(0, true);


        initSearchCont();
        addComponent(BorderLayout.NORTH, searchCont);
        addComponent(BorderLayout.CENTER, list);

        cmdBack = new Command("Back");
        addCommand(cmdBack);
        addCommandListener(this);

        
    }

    private void initSearchCont() {
        searchCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
        try {
            searchIcon = Image.createImage("/search.png");
        } catch (IOException ex) {
            System.out.println(" ERROR . Failed to load search icon ." + ex.toString());
        }
        searchlbl = new Label();
        if (searchIcon != null) {
            searchlbl.setIcon(searchIcon);
        } else {
            searchlbl.setText(" Search :");
        }


        field = new TextField("Click here to search", 22);
        field.setConstraint(TextField.ANY);
        field.setLabelForComponent(searchlbl);
        field.addFocusListener(this);
        field.addDataChangeListener(new DataChangedListener() {

            public void dataChanged(int i, int i1) {
                if (!field.getText().equals("Click here to search")) {
                    proxyModel.filter(field.getText());
                }

            }
        });

        field.getStyle().setMargin(5, 5, 5, 5);
        Border fieldBorder = Border.createLineBorder(1, 0xe1e1e1);
        field.getStyle().setBorder(fieldBorder);

        searchCont.addComponent(searchlbl);
        searchCont.addComponent(field);

    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
    }

    private void initItems() {
        apps = new Appointment[mWorkItemsList.size()];
        for (int i = 0; i < mWorkItemsList.size(); i++) {
            Appointment app = new Appointment();

            MWorkItem wir = (MWorkItem) mWorkItemsList.elementAt(i);
            formDef = formutil.getFormDef(wir);
            formData = formutil.getFormData(formDef, wir);

            app.setRecord_id(wir.getRecordId());


            Vector preFilledQns = wir.getPrefilledQns();
            for (int k = 0; k < preFilledQns.size(); k++) {
                MQuestionMap qnMap = (MQuestionMap) preFilledQns.elementAt(k);
                String questionName = qnMap.getQuestion();
//                System.out.println("Question Name :" + questionName);
                if (questionName.equals("vaccine_name")) {
                    app.setVaccine_name(qnMap.getValue());

                } else if (questionName.equals("scheduled_date")) {
                    app.setSchedule_date(qnMap.getValue());

                } else if (questionName.equals("dose_name")) {
                    app.setDose(qnMap.getValue());

                } else if (questionName.equals("date_of_immunization")) {
                    app.setImmunization_date(qnMap.getValue());

                }
            }

            wir.setDataRecId(new Integer(formData.getRecordId()));


            Vector pages = formData.getPages();
            System.out.println("Size of pages=>" + pages.size());
            //we will always heave one page for this app.
            //makes my life simpler
            currentPage = (PageData) pages.elementAt(0);
            int numQuestions = currentPage.getNumberOfQuestions();

            System.out.println("Size of questions=>" + numQuestions);

            Vector qtns = currentPage.getQuestions();
            QuestionData qd;

            for (int t = 0; t < qtns.size(); t++) {
                qd = (QuestionData) qtns.elementAt(t);
                if (ifDateImmQue(qd)) {
                    //displayedQuestions.addElement(qd);
                    app.setImmunization_date(qd.getTextAnswer());

                }

            }
            apps[i] = app;


        }

    }

    public boolean ifDateImmQue(QuestionData qd) {
        String text = qd.getText();
        ///*||text.equalsIgnoreCase("Reason for not Immunizing")*/
        if (text.equals("Date of Immunization")) {
            return true;
        } else {
            return false;
        }

    }

    public void errorOccured(String string, Exception excptn) {
    }

    public void actionPerformed(ActionEvent ae) {
        DebugLog.getInstance().log("@ Applist . Action Performed .  Selected this item=>" + list.getSelectedIndex() );
       
        Object src = ae.getSource();
        if (src == list) {
            int wirIndex = list.getSelectedIndex();
            MWorkItem slectedWir = null;

            try {

                if (mWorkItemsList != null) {
                    slectedWir = (MWorkItem) mWorkItemsList.elementAt(wirIndex);
                } else {
                    System.out.println(" Failed to execute . Mworkitem list is null");
                }
                if (slectedWir != null) {
                    
                    RegAppointmentForm appointmentForm = new RegAppointmentForm(slectedWir);
                    AppUtil.get().setView(appointmentForm);
                } else {
                    System.out.println("Error : Failed to load selected workitem");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(" Error . Failed to load immunization form .");
            }


//System.out.println("@ applist : action performed Selected this item=>"+slectedWir.getDescription() + " "+ slectedWir.getTaskName() + " " + slectedWir.getDisplayName() + " ");

//            ((MvacController)AppUtil.get().getItem(Constants.CONTROLLER)).showform(slectedWir);
        } else {
            Command cmd = ae.getCommand();
            if(cmd == cmdBack){
                System.out.println("Back Command Pressed");
                AppUtil.get().setView(new GroupList());
            }
        }
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void focusGained(Component cmpnt) {
        if (cmpnt.equals(field)) {
            Border fieldBorder = Border.createLineBorder(1, 0x9fd056);
            field.getStyle().setBorder(fieldBorder);
            if (field.getText().equals("Click here to search")) {
                field.setText("");
            }
            setNextFocusDown(list);
            list.setSelectedIndex(0, true);
        }
    }

    public void focusLost(Component cmpnt) {
        if (cmpnt.equals(field)) {
            Border fieldBorder = Border.createLineBorder(1, 0xe1e1e1);
            field.getStyle().setBorder(fieldBorder);
            if (field.getText().equals("")) {
                field.setText("Click here to search");
            }

        }
    }

    private Vector getWorkItems(Vector ids) {
        Vector resp = new Vector();

        for (Enumeration enumids = ids.elements(); enumids.hasMoreElements();) {
            int index = Integer.parseInt(enumids.nextElement().toString());
            resp.addElement(WFStorage.getWorkItem(index, this));
        }
        return resp;
    }
}
