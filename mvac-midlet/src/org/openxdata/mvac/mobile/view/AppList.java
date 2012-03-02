/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
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
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author soyfactor
 */
public class AppList extends Form implements IView, StorageListener, ActionListener, FocusListener {

    private Appointment[] apps;
    private List list;
    private TextField field = new TextField("Click here to search", 22);
//    private Font lblFont = Font.getBitmapFont("mvaccalibri13");
    private Label searchlbl = new Label("Search:");
    private Vector mWorkItemsList = new Vector(0);
    private Command uploadselected;
    private Command uploadall;
    private FormUtil formutil;
    private FormDef formDef;
    private FormData formData;
    private int currentPageIndex;
    private int currentQuestionIndex;
    private QuestionData currentQuestion;
    private Vector displayedQuestions;
    private boolean dirty;
    private PageData currentPage;


    public AppList(String title) {
        super(title);

        initView();
    }

    public AppList(AppointmentWrapper wrapper) {
        super(wrapper.getName());
        formutil = new FormUtil();
        mWorkItemsList = getWorkItems(wrapper.getWorkItems());
        initView();
    }

    private void initView() {
        initItems();
        uploadselected = new Command("upload selected", 1);
        uploadall = new Command("Upload All", 2);

        //addCommand(uploadselected);
        //addCommand(uploadselected);
        addCommandListener(this);


        
        AppListModel appListModel = new AppListModel(apps);
        final FilterProxyListModel proxyModel = new FilterProxyListModel(appListModel);
        list = new List(proxyModel);
        AppRender appRender = null;
        list.setListCellRenderer(appRender = new AppRender());
        list.setFixedSelection(List.FIXED_NONE_CYCLIC);
        list.addActionListener(this);
        setLayout(new BorderLayout());

        field.setConstraint(TextField.ANY);
//        searchlbl.getStyle().setFont(lblFont);
        field.setLabelForComponent(searchlbl);
        field.addFocusListener(this);
        field.addDataChangeListener(new DataChangedListener() {

            public void dataChanged(int i, int i1) {
                if (!field.getText().equals("Click here to search")) {
                    proxyModel.filter(field.getText());
                }

            }
        });

        addComponent(BorderLayout.NORTH, field);
        addComponent(BorderLayout.CENTER, list);

        addCommand(new Command("Back") {

            public void actionPerformed(ActionEvent ae) {
                System.out.println("Back Command Pressed");
                GroupList appList = new GroupList("Saved appointments");
                AppUtil.get().setView(appList);
            }
        });
    }

    private int getAppSize() {
        return 5;
    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        //AppUtil.get().setView(this);
//        this.removeAll();
//        initView();
//        AppUtil.get().setView(this);
    }

    private void initItems() {
        //refreshList();
        apps = new Appointment[mWorkItemsList.size()];
        for (int i = 0; i < mWorkItemsList.size(); i++) {
            Appointment app = new Appointment();

            MWorkItem wir = (MWorkItem) mWorkItemsList.elementAt(i);
            formDef = formutil.getFormDef(wir);
            formData = formutil.getFormData(formDef, wir);
            
            app.setRecord_id(wir.getRecordId());
            


            /**
             * TODO : Need to sort this list in chronological order 
             */
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

                }else if (questionName.equals("date_of_immunization")) {
                    app.setImmunization_date(qnMap.getValue());

                }
            }
            
            wir.setDataRecId(new Integer(formData.getRecordId()));
            
            
            currentPageIndex = 0;
        currentQuestionIndex = 0;
        currentQuestion = null;
        displayedQuestions = new Vector();
        dirty = false;

        Vector pages = formData.getPages();
        System.out.println("Size of pages=>"+pages.size());
        //we will always heave one page for this app.
        //makes my life simpler
        currentPage = (PageData)pages.elementAt(0);
        int numQuestions = currentPage.getNumberOfQuestions();

        System.out.println("Size of questions=>"+numQuestions);

        Vector qtns = currentPage.getQuestions();
        QuestionData qd;

        for (int t = 0; t < qtns.size(); t++) {
            qd=(QuestionData)qtns.elementAt(t);
            if(ifDateImmQue(qd)){
                //displayedQuestions.addElement(qd);
                app.setImmunization_date(qd.getTextAnswer());

            }

        }
            apps[i] = app;


        }

    }
    
    public boolean ifDateImmQue(QuestionData qd){
        String text =  qd.getText();
        ///*||text.equalsIgnoreCase("Reason for not Immunizing")*/
        if(text.equals("Date of Immunization")){
            return true;
        }else{
            return false;
        }

    }

//    private void refreshList() {
//        Vector items = WFStorage.getMWorkItemList(this);
//        if(items!=null){
//            mWorkItemsList=items;
//        }else{
//            mWorkItemsList= new Vector(0);
//        }
//        System.out.println("Size of my objects =>"+items.size());
//    }
    public void errorOccured(String string, Exception excptn) {
    }

    public void actionPerformed(ActionEvent ae) {
        System.out.println("@ Applist Selected this item=>" + list.getSelectedIndex());
        Object src = ae.getSource();
        if (src == list) {
            int wirIndex = list.getSelectedIndex();
            MWorkItem slectedWir = null;

            try {
                
                if(mWorkItemsList != null){
                    slectedWir = (MWorkItem) mWorkItemsList.elementAt(wirIndex);
                }else{
                    System.out.println(" Failed to execute . Mworkitem list is null");
                }
                

                if (slectedWir != null) {

                    AppointmentForm appForm = new AppointmentForm(slectedWir , this.getTitle());
                    AppUtil.get().setView(appForm);
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
            if (cmd == uploadselected) {
                System.out.println("uploading selected");
            } else if (cmd == uploadall) {
                System.out.println("uploading all");
            }
        }
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void focusGained(Component cmpnt) {
        if (cmpnt.equals(field)) {
            if (field.getText().equals("Click here to search")) {
                field.setText("");
            }

        }
    }

    public void focusLost(Component cmpnt) {
        if (cmpnt.equals(field)) {
            if (field.getText().equals("")) {
                field.setText("Click here to search");
            }

        }
    }

    private Vector getWorkItems(Vector ids){
        Vector resp = new Vector();

        for(Enumeration enumids = ids.elements() ; enumids.hasMoreElements();){
            int index = Integer.parseInt(enumids.nextElement().toString());
            resp.addElement(WFStorage.getWorkItem(index, this));
        }
        return resp ;
    }
}
