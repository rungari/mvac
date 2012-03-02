/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Font;
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
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import org.openxdata.db.util.Storage;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author soyfactor
 */
public class GroupList extends  Form implements IView,StorageListener,ActionListener,FocusListener{
    private AppointmentWrapper[] apps;
    private List list;
    private  TextField field = new TextField("Click here to search", 22);
    
//    private Font lblFont = Font.getBitmapFont("mvaccalibri13");
    private Label searchlbl = new Label("Search:");
    private Vector mWorkItemsList = new Vector(0);
    private Hashtable appGroups = new Hashtable();
    private AppListModel appListModel;
    private GroupFilterProxyListModel proxyModel;
    private Command back;

    



    public GroupList(String title) {
        super("Children Due");
        initView();
    }

    

    private void initView() {
        initItems();
        back = new Command("Back", 1);
        addCommand(back);
        addCommandListener(this);
        appListModel=new AppListModel(apps);
        proxyModel = new GroupFilterProxyListModel(appListModel);
        list =  new List(proxyModel);
        list.addActionListener(this);
        list.setListCellRenderer(new AppGroupRender());
        list.setFixedSelection(List.FIXED_NONE_CYCLIC);
        setLayout(new BorderLayout());

        field.setConstraint(TextField.ANY);
//        searchlbl.getStyle().setFont(lblFont);
        field.setLabelForComponent(searchlbl);
        field.addFocusListener(this);
        field.addDataChangeListener(new DataChangedListener() {

            public void dataChanged(int i, int i1) {
                if(!field.getText().equals("Click here to search")){
                    proxyModel.filter(field.getText());
                }
                
            }
        });

        addComponent(BorderLayout.NORTH, field);
        addComponent(BorderLayout.CENTER, list);
    }

    private int getAppSize() {
        return 5;
    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        AppUtil.get().setView(this);
    }

    private void initItems() {
        refreshList();
//        System.out.println("Almost looping work items");

        /***
         * TEST
         */
        
        for(RecordEnumeration re = WFStorage.getWorkItemEnum() ; re.hasNextElement();){
            AppointmentWrapper wrapper = new AppointmentWrapper(this);
            Appointment appointment = new Appointment();
            String child_id = "";

            MWorkItem wir = null;
            try{
                wir = WFStorage.getWorkItem(re.nextRecordId(), this);
                if(wir != null){
                    appointment.setRecord_id(wir.getDataRecId());

                    Vector preFilledQns = wir.getPrefilledQns();
                    for (int k = 0; k < preFilledQns.size(); k++) {
                        MQuestionMap qnMap        = (MQuestionMap) preFilledQns.elementAt(k);
                        String       questionName = qnMap.getQuestion();
                        if (questionName.equals("child_name")) {
                            wrapper.setName(qnMap.getValue());
                            wrapper.setName(qnMap.getValue());

                        }else if(questionName.equals("caretaker_name")){
                            wrapper.setCaretaker(qnMap.getValue());
                            wrapper.setCaretaker(qnMap.getValue());

                        }else if(questionName.equals("child_iis_id")){
                            child_id=qnMap.getValue();
                            appointment.setChild_id(qnMap.getValue());
                        }else if(questionName.equals("child_dob")){
                            String dob = qnMap.getValue();
                            if(dob.indexOf("T")>=0){
                                dob = dob.substring(0, dob.indexOf("T"));
                            }
                            wrapper.setChild_dob(dob);
                        }else if(questionName.equals("caretaker_nid")){
                            wrapper.setCaretaker_nid(qnMap.getValue());
                        }
                    }

                     if(appGroups.containsKey(child_id)){
                        ((AppointmentWrapper)appGroups.get(child_id)).addElement(appointment);
                        ((AppointmentWrapper)appGroups.get(child_id)).addWorkItemID(wir.getRecordId());
//                          System.out.println("Adding existing");


                    }else{
                        wrapper.addElement(appointment);
                        appGroups.put(child_id, wrapper);
                        ((AppointmentWrapper)appGroups.get(child_id)).addWorkItemID(wir.getRecordId());
//                System.out.println("Adding New");
            }

                }
            }catch(InvalidRecordIDException exception){
                System.out.println(" ERROR . Exception thrown when fetching item from store ." + exception.getMessage());
            }
        }

        apps = new AppointmentWrapper[appGroups.size()];

        Enumeration e = appGroups.keys();
        int count=0;

        while(e.hasMoreElements()){
            String me= e.nextElement().toString();
            AppointmentWrapper wrapper = (AppointmentWrapper)appGroups.get(me);
//            wrapper.sort();
            apps[count]=wrapper ;
            wrapper = null;
            count++;

        }

        /**
         * END OF TEST
         */




    }

    private void refreshList() {
        //Vector items = new Vector(0);
        //items= WFStorage_old.getMWorkItemList(this);
        //System.out.println("after items"+items);
//        System.out.println("Abt to check");
//        if(WFStorage.getSomeMWorkItemList(this) !=null){
//            mWorkItemsList=WFStorage.getMWorkItemList(this);
//
//        }else{
//            mWorkItemsList= new Vector(0);
//        }
        mWorkItemsList = new Vector();
//        System.out.println("Size of my objects =>"+mWorkItemsList.size());
    }

    public void errorOccured(String string, Exception excptn) {
    }

    public void actionPerformed(ActionEvent ae) {
        Object src =ae.getSource();
        if(src==list){
            AppointmentWrapper apwr= (AppointmentWrapper)list.getSelectedItem();
System.out.println("Just selected=>"+apwr.getName());
            AppUtil.get().putItem(Constants.APWR, apwr);
            AppList myList = new AppList(apwr);
            AppUtil.get().setView(myList);
        }else{
            Command cmd = ae.getCommand();
            if (cmd==back) {
                LWUITMainMenu mainMenu = new LWUITMainMenu();
                AppUtil.get().setView(mainMenu);
            }
        }
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void focusGained(Component cmpnt) {
        if(cmpnt.equals(field)){
            if(field.getText().equals("Click here to search")){
                field.setText("");
            }
            
        }
    }

    public void focusLost(Component cmpnt) {
        if(cmpnt.equals(field)){
            if(field.getText().equals("")){
                field.setText("Click here to search");
            }
            
        }
    }

    

}
