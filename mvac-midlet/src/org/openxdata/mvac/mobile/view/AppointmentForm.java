/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import org.openxdata.mvac.mobile.model.AppointmentWrapper;
import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import org.openxdata.communication.TransportLayerListener;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.QuestionData;
import org.openxdata.model.UserListStudyDefList;
import org.openxdata.mvac.communication.ITransportListener;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.api.MvacController;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.date.DateField;
import org.openxdata.mvac.mobile.view.widgets.CalendarCanvas;
import org.openxdata.workflow.mobile.model.MWorkItem;
import org.openxdata.mvac.mobile.api.SavingListener;
import org.openxdata.mvac.mobile.xml.XmlParser;
import org.openxdata.mvac.model.LotNumbers;

/**
 *
 * @author soyfactor
 */
public class AppointmentForm extends Form implements IView, StorageListener, ActionListener, TransportLayerListener, SavingListener, ITransportListener {

    private QuestionListObj[] ques;
    //private IView parent;
    private MWorkItem wir;
    private Vector dipslayedQues = new Vector(0);
    private DownloadManager dwnLdMgr;
    private FBProgressIndicator progress;
    private String titleString = "Register Immunization";
    private Command doneCmd = new Command("Save", 1);
    private Command cancelCmd = new Command("Back", 2);
    private Command lotCmd = new Command("Refresh");
    String[] lotNumArray;// = {"Refresh to update..."};
    LotNumbers lots;
    boolean isnew;
    TransportManager tm;
    //The containers
    Container mainCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    Container notesCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    Container dateCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    Container statusCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    Container lotContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    Container reasonCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
    int notesContID = 3;
    int dateContID = 1;
    int statusContID = 2;
    int lotContID = 0;
    int reasonContID = 4;
    //The question widgets
    Label notesLabel = new Label("");
    Label datelabel = new Label("");
    Label statusLabel = new Label("");
    Label lotLabel = new Label("");
    Label resonLabel = new Label("");
    Button lotButton = new Button("Refresh");
    TextField notesField = new TextField("");
    //Status
    String[] comboOpts = {"Yes", "No"};
    ComboBox statusCombo = new ComboBox(comboOpts);
    String[] reasonOpts = {"Refusal", "Permanent contraindication"};
    ComboBox reasonCombo = new ComboBox(reasonOpts);
    //date stuff
    DateField appDate = new DateField(DateField.DDMMYYYY, '/');
    Button selectDate = null;
    Command selectDateCmd = new Command("Change Date");
    ComboBox lotCombo;
    private boolean isAdded = false;
    private CalendarForm calendarForm = new CalendarForm();
    private QuestionListObj reasonQues = null;
    private QuestionListObj notesQues = null;
    private QuestionListObj lotQues = null;
    private QuestionListObj dateQues = null;
    private QuestionListObj statusQues = null;

    public AppointmentForm(String title) {
        super(title);
        super.getTitleComponent().setAlignment(LEFT);
        lots = WFStorage.getLotNumbers(this);

        System.out.println("Gotten Lots=>" + lots.getLotNumbers());
        if (lots.getLotNumbers().equals("Refresh to update...,")) {
            isnew = true;
        } else {
            isnew = false;
        }
        lotNumArray = split(lots.getLotNumbers(), ",");


        lotCombo = new ComboBox(lotNumArray);
        lotCombo.getStyle().setFgColor(0X000000);
        DefaultListCellRenderer dlcr =
                (DefaultListCellRenderer)lotCombo.getRenderer();
        dlcr.setSelectedStyle(AppUtil.getSelectStyle());


        try {
            initItems();
        } catch (Exception e) {
            System.out.println("Exception ");
            e.getMessage();
            e.printStackTrace();
        }
    }

    public AppointmentForm(MWorkItem wir, String childname) {
        super("");
        super.getTitleComponent().setAlignment(LEFT);
        System.out.println("After form util=>");

        lots = WFStorage.getLotNumbers(this);

        if (lots.getLotNumbers().equals("Refresh to update...,")) {
            isnew = true;
        } else {
            isnew = false;
        }

        System.out.println("Gotten Lots=>" + lots.getLotNumbers());
        lotNumArray = split(lots.getLotNumbers(), ",");

        reasonCombo.getStyle().setFgColor(0X000000);
        DefaultListCellRenderer dlcr =
                (DefaultListCellRenderer) reasonCombo.getRenderer();
        dlcr.setSelectedStyle(AppUtil.getSelectStyle());

        lotCombo = new ComboBox(lotNumArray);
        lotCombo.getStyle().setFgColor(0X000000);
        lotCombo.setFocusPainted(true);
        DefaultListCellRenderer dlcr2 =
                (DefaultListCellRenderer) lotCombo.getRenderer();
        dlcr2.setSelectedStyle(AppUtil.getSelectStyle());


        if (childname != null) {
            setTitle(childname);
        } else {
            setTitle(titleString);
        }
        this.wir = wir;
        tm = new TransportManager("GET", this);

        try {
            initItems();
        } catch (Exception e) {
            System.out.println("Exception ");
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void initView() {

        //The containers
        mainCont.removeAll();
        notesCont.removeAll();
        dateCont.removeAll();
        statusCont.removeAll();
        lotContainer.removeAll();
        reasonCont.removeAll();
        this.removeAll();
        lotCombo = new ComboBox(lotNumArray);
        lotCombo.getStyle().setFgColor(0X000000);
        lotCombo.setFocusPainted(true);
        DefaultListCellRenderer dlcr2 =
                (DefaultListCellRenderer) lotCombo.getRenderer();
        dlcr2.setSelectedStyle(AppUtil.getSelectStyle());

    }


    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        if (args != null) {
            if (args.containsKey("date")) {
                Date myDate = (Date) args.get("date");
                System.out.println("Selected Date ->" + myDate.toString());
                if (appDate != null) {
                    appDate.setDate(myDate);
                    AppUtil.get().setView(this);
                }
            } else if (args.containsKey("GEN_ALERT")) {
                AppointmentWrapper apr = (AppointmentWrapper) AppUtil.get().getItem(Constants.APWR);
                AppList appList = new AppList(apr);
                AppUtil.get().setView(appList);
            }
        } 
        else {
            AppUtil.get().setView(this);
        }
    }

    private void initItems() throws Exception {

        if (((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).newsetWorkItem(this.wir)) {
            ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).afterSetWorkItems(this.wir);
            nowShow();
        } else {
            downloadForm();
        }


    }

    private void nowShow() {
        selectDate = new Button(selectDateCmd);
        selectDate.addActionListener(this);
        int size = ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).getDisplayQues().size();
        ques = new QuestionListObj[size - 1];
        System.out.println("@ QuestionList : initItems() : Size of question array :" + ques.length);


        int queCount = 0;
        this.dipslayedQues = ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).getDisplayQues();
        for (int i = 0; i < this.dipslayedQues.size(); i++) {
            QuestionListObj que = new QuestionListObj();

            QuestionData qd = (QuestionData) dipslayedQues.elementAt(i);
            String question = qd.getText();

            if (!question.equals("Dose ID")) {
                que.setQuestion(question);
            }
            if (question.equals("Lot Number")) {
                que.setType("dropdown");
            } else if (question.equals("Date of Immunization")) {
                que.setType("Date");
            } else if (question.equals("Status")) {
                que.setType("Check box");
            } else if (question.equals("Notes")) {
                que.setType("textarea");
            } else if (question.equals("Reason")) {
                que.setType("dropdown1");
            }

            if (!question.equals("Dose ID")) {
                System.out.println("@ QuestionList :initItems : Text :" + qd.getText() + "  Text Answer :" + qd.getTextAnswer());
                que.setValue(qd.getTextAnswer());
                ques[queCount] = que;
                queCount++;
            } else {
                System.out.println("Found Dose ID=>" + qd.getTextAnswer());
            }
        }


        for (int k = 0; k < ques.length; k++) {
            if (ques[k].getType().equals("Check box")) {
                statusQues = ques[k];
                appendStatus(ques[k]);

            } else if (ques[k].getType().equals("Date")) {
                dateQues = ques[k];
                appendDateofImmunization(ques[k]);

            }else if (ques[k].getType().equals("dropdown")) {
                lotQues = ques[k];
                appendLotNumber(ques[k]);

            }else if (ques[k].getType().equals("textarea")) {
                notesQues = ques[k];
                appendNotes(ques[k]);
            } else if (ques[k].getType().equals("dropdown1")) {
                reasonQues = ques[k];
            }
        }

        try {
            String value = statusCombo.getSelectedItem().toString();
            if (value != null) {
                if (value.equals("No")) {
                    isAdded = true;
                    enableResCont();

                } else if (value.equals("Yes")) {
//                    if (isAdded) {
                        disableResCont();
//                    }
                }
            }

        } catch (Exception e) {
            System.out.println(" Failed to set default valie @ append status ." + e.toString());
        }

        addCommandListener(this);
        setLayout(new BorderLayout());
        getStyle().setBgColor(0xffffff);
        addComponent(BorderLayout.CENTER, mainCont);
        addCommand(cancelCmd);
        addCommand(doneCmd);

    }

    public void downloadForm() {
        dwnLdMgr = ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).getDM();
        progress = new FBProgressIndicator(this, "Downloading studies...");
        progress.showModeless();


        new BackgroundTask() {

            public void performTask() {
                dwnLdMgr.downloadStudies(AppointmentForm.this);
            }

            public void taskStarted() {
                System.out.println("My task is started");
            }
        }.start();

    }

    public void errorOccured(String string, Exception excptn) {
        excptn.printStackTrace();
        //progress.dispose();
        AppUtil.get().setView(this);
    }

    public void actionPerformed(ActionEvent ae) {

        Object src = ae.getSource();
        System.out.println("Selected source=>" + src.getClass().toString());
        if (src == lotButton) {
            //refresh the Lot List
            refreshLot();

        } else if (src == doneCmd) {
            //save items
            saveData();


        } else if (src == cancelCmd) {
            AppointmentWrapper apr = (AppointmentWrapper) AppUtil.get().getItem(Constants.APWR);
            AppList appList = new AppList(apr);
            AppUtil.get().setView(appList);

        } else if (src == statusCombo) {
            String selection = statusCombo.getSelectedItem().toString();
            System.out.println("Combo selected :" + selection);
            if (selection.equals("No")) {
                isAdded = true;
                enableResCont();


            } else if (selection.equals("Yes")) {
                if (isAdded) {
                    disableResCont();
                }
            }
        } else if (src == selectDateCmd) {
            //Load Calendar here
            CalendarCanvas calfoForm = new CalendarCanvas(this);
            ((Display) AppUtil.get().getItem(Constants.MIDP_DISPLAY)).setCurrent(calfoForm);

        } else if (src == calendarForm.okcmd) {
            System.out.println("Selected Date :" + calendarForm.getDate());

        } else if (src == calendarForm.cmdBack) {
            System.out.println("Back Command");
            AppUtil.get().setView(this);
        } else if (ae.getCommand() == progress.cancel) {
            //transportlayer.
            this.resume(null);

        }

    }


    private void disableResCont() {
        mainCont.removeAll();

        mainCont.addComponent(statusCont);
        mainCont.addComponent(dateCont);
        mainCont.addComponent(lotContainer);
        mainCont.addComponent(notesCont);
    }

    private void enableResCont() {
        mainCont.removeAll();
        if (statusCont != null) {
            mainCont.addComponent(statusCont);
        } else {
            appendStatus(statusQues);
        }
        if (dateCont != null) {
            mainCont.addComponent(dateCont);
        } else {
            appendDateofImmunization(dateQues);
        }        
        appendReason();
        if (notesCont != null) {
            mainCont.addComponent(notesCont);
        } else {
            appendNotes(notesQues);
        }
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public String getTitleString() {
        return titleString;
    }

    public void uploaded(Persistent prstnt, Persistent prstnt1) {
    }

    public void downloaded(Persistent dataOutParams, Persistent dataOut) {
        progress.dispose();
        if (dataOut instanceof UserListStudyDefList) {
            System.out.println("Saving the forms");
            handleStudyAndUserDownloaded((UserListStudyDefList) dataOut);
        } else if (dataOut instanceof Object) {
            //do Lot number stuff here
        }
        try {
            nowShow();
        } catch (Exception e) {
            System.out.println(" Error failed to show form .");
        }

    }

    private void handleStudyAndUserDownloaded(UserListStudyDefList userListStudyDefList) {
        WFStorage.saveUserListStudyDefList(userListStudyDefList);
    }

    public void cancelled() {
    }

    public void updateCommunicationParams() {
    }

    private void appendLotNumber(QuestionListObj obj) {
        lotLabel.setText(obj.getQuestion());
        lotContainer.addComponent(lotLabel);
        lotButton.addActionListener(this);
        lotContainer.addComponent(lotCombo);
        lotContainer.addComponent(lotButton);
        String value = obj.getValue();



        if (obj.getValue() != null) {
            lotCombo.setSelectedItem(obj.getValue());
        }

        Border bd = Border.createLineBorder(1, 0x7799bb);
        lotContainer.getStyle().setBorder(bd, true);
//        mainCont.addComponent(lotContainer);

    }

    private void appendDateofImmunization(QuestionListObj obj) {
        datelabel.setText(obj.getQuestion());
        dateCont.addComponent(datelabel);
        if (obj.getValue() != null && appDate != null) {
            String d = obj.getValue();
            System.out.println("Date is :" + d);
            long timestamp = org.openxdata.mvac.mobile.util.DateParser.getTimestamp(d, "/");
            Date date = new Date(timestamp);
            System.out.println(" Date is :" + date.toString());
            appDate.setDate(date);
        }

        dateCont.addComponent(appDate);
        dateCont.addComponent(selectDate);
    }

    private void appendStatus(QuestionListObj obj) {
        statusLabel.setText(obj.getQuestion());
        statusCont.addComponent(statusLabel);
        statusCombo.getStyle().setFgColor(0X000000);
        String status = obj.getValue();

        DefaultListCellRenderer dlcr =
                (DefaultListCellRenderer)statusCombo.getRenderer();
        dlcr.setSelectedStyle(AppUtil.getSelectStyle());



        statusCombo.addActionListener(this);
        if (obj.getValue() != null) {
            String value = obj.getValue();
            statusCombo.setSelectedItem(obj.getValue());
        }
        statusCont.addComponent(statusCombo);
//        mainCont.addComponent(statusCont);

    }

    private void appendNotes(QuestionListObj obj) {
        notesLabel.setText(obj.getQuestion());
        notesField.setText((obj.getValue() == null) ? "" : "" + obj.getValue());
        notesCont.addComponent(notesLabel);
        notesCont.addComponent(notesField);
//        mainCont.addComponent(notesCont);
    }

    private void appendReason() {
        if (reasonCont != null) {
            reasonCont.removeAll();
            resonLabel.setText("Reason");
            reasonCont.addComponent(resonLabel);
            reasonCombo.getStyle().setFgColor(0X000000);

            DefaultListCellRenderer dlcr =
                (DefaultListCellRenderer)reasonCombo.getRenderer();
                dlcr.setSelectedStyle(AppUtil.getSelectStyle());

            //if(reasonQues.getValue() != null) reasonCombo.setSelectedItem(reasonQues.getValue());
            reasonCont.addComponent(reasonCombo);
//            mainCont.addComponent(reasonCont);
        }
    }

    private void saveData() {
        System.out.println("Question size:=>" + dipslayedQues.size());
        for (int i = 0; i < dipslayedQues.size(); i++) {
            QuestionData qd = (QuestionData) dipslayedQues.elementAt(i);
            String question = qd.getText();
            //que.setQuestion(question);

            if (question.equals("Lot Number")) {
                //que.setType("dropdown");
                qd.setTextAnswer((String) lotCombo.getSelectedItem());
            } else if (question.equals("Date of Immunization")) {
                //que.setType("Date");
                qd.setTextAnswer(appDate.getText());
            } else if (question.equals("Status")) {
                //que.setType("Check box");
                qd.setTextAnswer((String) statusCombo.getSelectedItem());
            } else if (question.equals("Notes")) {
                //que.setType("textarea");
                qd.setTextAnswer(notesField.getText());
            } else if (question.equals("Reason")) {
                qd.setTextAnswer((String) reasonCombo.getSelectedItem());
            }
            dipslayedQues.setElementAt(qd, i);

        }
        ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).setListener(this);
        ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).saveQtnDataFromForm(dipslayedQues, this.wir);

    }

    private void refreshLot() {
        //Load Refresh
        new BackgroundTask() {

            public void performTask() {
                progress = new FBProgressIndicator(AppointmentForm.this, "Refreshing Lot Information...");
                progress.showModeless();
                tm = new TransportManager("GET", AppointmentForm.this);
                String url = Constants.LOT_URL + (String) AppUtil.get().getItem(Constants.NURSENAME);
                Message msg = new Message(url);
                tm.sendMessage(msg);

            }
        }.start();
    }

    public void dataSaved(boolean resp) {
        if (resp) {
            GenericAlert genericAlert = new GenericAlert(AppointmentForm.this, "Successfully saved");
            genericAlert.show();
        } else {
            System.out.println(" Error when saving");
            ErrorAlert errorAlert = new ErrorAlert(AppointmentForm.this, "Error when saving");
            errorAlert.show();
        }
    }

    public void messageSent(Object args) {
        if (args instanceof String) {
            XmlParser parser = new XmlParser();
            String response = (String) args;

            String rp = response.trim();


            InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(rp.getBytes()));

            Object lotobj = parser.processXml(isr);

            if (lotobj != null) {
                String lotStr = (String) lotobj;
                lotNumArray = split(lotStr, ",");
                LotNumbers downLots = new LotNumbers();
                downLots.setLotNumbers(lotStr);
                downLots.setIsnew(true);

                WFStorage.saveLotNumbers(downLots, this);

            }

            initView();

        }


        progress.dispose();
        nowShow();
        AppUtil.get().setView(this);
        //this.show();
    }

    private String[] split(String original, String regex) {
        Vector nodes = new Vector();
        String separator = regex;

        //Parse Nodes into the vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
                System.out.println(result[loop]);
            }

        }

        return result;
    }

    void resetUI() {
        this.removeAll();

    }

    public void requestFailed(Object error) {
        if (error != null) {
            System.out.println(" ERROR . Request failed ." + error.toString());
        }
    }
}
