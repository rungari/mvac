package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.QuestionData;
import org.openxdata.mvac.communication.ITransportListener;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.mobile.api.MvacController;
import org.openxdata.mvac.mobile.api.SavingListener;
import org.openxdata.mvac.mobile.xml.XmlParser;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.model.AppointmentWrapper;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.DateParser;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.widgets.CalendarCanvas;
import org.openxdata.mvac.mobile.view.widgets.DetailsTextField;
import org.openxdata.mvac.mobile.model.LotNumbers;
import org.openxdata.workflow.mobile.model.MQuestionMap;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author mutahi
 */
public class RegAppointmentForm extends Form implements ActionListener, IView, ITransportListener, StorageListener, SavingListener {

    private MWorkItem mWorkItem = null;
    private Container mainCont;
    private Container notesCont;
    private Container dateCont;
    private Container statusCont;
    private Container lotCont;
    private Container reasonCont;
    private Container lotComboCont;
    private Container reasonComboCont;
    private Label statusLbl;
    private Label dateLbl;
    private Label lotLbl;
    private Label notesLbl;
    private Label reasonLbl;
    private Label mandatorymark;
    private Label mandatorymark1;
    private DetailsTextField dateValue;
    private ComboBox statusCombo;
    private ComboBox lotCombo;
    private ComboBox reasonCombo;
    private Button dateButton;
    private Button dwnldButton;
    private Command dwnldCmd;
    private Command dateCmd;
    private TextArea notesTxt;
    private String[] reasons = {"Refusal", "Permanent contraindication"};
    private String[] statusOptions = {"Yes", "No"};
    private Date selectedDate = null;
    private String[] defaultOpt = {"No Selection"};
    private FBProgressIndicator progress = null;
    private TransportManager tm = null;
    private String[] lotNumArray;
    private QuestionListObj[] ques;
    private Vector dipslayedQues = new Vector(0);
    private QuestionListObj reasonQues = null;
    private QuestionListObj notesQues = null;
    private QuestionListObj lotQues = null;
    private QuestionListObj dateQues = null;
    private QuestionListObj statusQues = null;
    private Command cmdBack;
    private Command cmdSave;

    private TextField titleLbl ;

    public RegAppointmentForm(MWorkItem workItem) {
        super();
        this.mWorkItem = workItem;
        System.out.println(" Childname :" + getChildName() + " Vaccine Dose :" + getVaccine_dose());
        super.setTitle(getChildName());
        super.getTitleComponent().setAlignment(LEFT);
        super.getTitleComponent().getStyle().setFgColor(0x000000);
        init();
        addComponent(BorderLayout.CENTER, mainCont);
    }

    private void init() {
        setLayout(new BorderLayout());
        initContainers();
        try {
            initItems();
        } catch (Exception ex) {
            System.out.println(" ERROR . Failed to init data ." + ex.getMessage());
            ex.printStackTrace();
        }

        titleLbl = new TextField();
        titleLbl.setText(getVaccine_dose());
        titleLbl.setFocusable(false);
        titleLbl.getStyle().setBgColor(0xe1e1e1, true);
        titleLbl.getStyle().setMargin(0, 0, 0, 0);
        titleLbl.getStyle().setPadding(0, 0, 5, 0);
        addComponent(BorderLayout.NORTH , titleLbl);

        initElements();

        /**
         * form Commands
         */
        cmdBack = new Command("Back");
        cmdSave = new Command("Save");
        addCommandListener(this);
        addCommand(cmdBack);
        addCommand(cmdSave);

        if (statusQues.getValue() != null) {
            String status = statusQues.getValue();
            renderForm(stringToBoolean(status));

        } else {
            renderForm(true);
        }
    }

    private void initContainers() {
        mainCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        notesCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        dateCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        statusCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        lotCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        lotComboCont = new Container(new BorderLayout());
        reasonCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        reasonComboCont = new Container(new BorderLayout());
    }

    private void initElements() {
        /**
         * Mandatory mark
         */
        mandatorymark = new Label("*");
        mandatorymark.getStyle().setFgColor(0xcc0000, true);
        mandatorymark.getStyle().setMargin(0, 0, 5, 0);
        mandatorymark1 = new Label("*");
        mandatorymark1.getStyle().setFgColor(0xcc0000, true);
        mandatorymark1.getStyle().setMargin(0, 0, 5, 0);


        /**
         * Status field
         */
        statusLbl = new Label(" Status ");
        statusCombo = new ComboBox(statusOptions);
        statusCombo.addActionListener(this);
        DefaultListCellRenderer dlcr = (DefaultListCellRenderer) statusCombo.getRenderer();
        dlcr.setSelectedStyle(AppUtil.getSelectStyle());
        statusCombo.setFocus(true);
        statusCont.addComponent(statusLbl);
        statusCont.addComponent(statusCombo);

        /**
         * date field
         */
        dateLbl = new Label(" Date of Immunization");
        dateValue = new DetailsTextField();
        dateValue.getStyle().setMargin(0, 0, 10, 10);
        dateValue.setFocusable(false);
        dateValue.getStyle().setBgColor(0xffffff);
        if (dateQues.getValue() != null) {
            System.out.println(" Set date is :" + dateQues.getValue());
            if (selectedDate != null) {
                dateValue.setText(DateParser.getMvacStringDate(selectedDate));
            } else {
                dateValue.setText(dateQues.getValue());
            }
        } else {
            dateValue.setText(getCurrentDate());
        }
        dateCmd = new Command("Change Date");
        dateButton = new Button(dateCmd);
        dateButton.getStyle().setMargin(0, 0, 10, 10);
        dateCont.addComponent(dateLbl);
        dateCont.addComponent(dateValue);
        dateCont.addComponent(dateButton);


        /**
         * lot Numbers
         */
        lotLbl = new Label(" Lot Number");
        dwnldCmd = new Command("Download Lots");
        dwnldButton = new Button(dwnldCmd);
        dwnldButton.getStyle().setMargin(0, 10, 10, 10);
        populateLotCombo();
        if (lotCombo.size() > 1) {
            if (lotQues.getValue() != null) {
                try {
                    lotCombo.setSelectedItem(lotQues.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        DefaultListCellRenderer dlcr1 = (DefaultListCellRenderer) lotCombo.getRenderer();
        dlcr1.setSelectedStyle(AppUtil.getSelectStyle());
        lotCont.addComponent(lotLbl);
        lotCont.addComponent(dwnldButton);
        lotComboCont.addComponent(BorderLayout.WEST, mandatorymark);
        lotCombo.getStyle().setMargin(10, 0, 0, 10);
        lotComboCont.addComponent(BorderLayout.CENTER, lotCombo);
        lotCont.addComponent(lotComboCont);

        /**
         * Notes Field
         */
        notesLbl = new Label(" Notes ");
        notesTxt = new TextArea(3, 5);
        notesTxt.setConstraint(TextField.ANY);
        notesTxt.getStyle().setMargin(0, 0, 10, 10);
        if (notesQues.getValue() != null) {
            notesTxt.setText(notesQues.getValue());
        }
        Border notesBorder = Border.createLineBorder(2, 0xe1e1e1);
        notesTxt.getStyle().setBorder(notesBorder, true);
        notesCont.addComponent(notesLbl);
        notesCont.addComponent(notesTxt);


        /**
         * Reason field
         */
        reasonLbl = new Label(" Reason ");
        reasonCombo = new ComboBox(reasons);
        DefaultListCellRenderer dlcr12 = (DefaultListCellRenderer) reasonCombo.getRenderer();
        dlcr12.setSelectedStyle(AppUtil.getSelectStyle());
        reasonCombo.getStyle().setMargin(10, 0, 0, 10);
        if(reasonQues!=null && reasonQues.getValue()!=null){
            try{
                reasonCombo.setSelectedItem(reasonQues.getValue());
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
        reasonComboCont.addComponent(BorderLayout.WEST, mandatorymark1);
        reasonComboCont.addComponent(BorderLayout.CENTER, reasonCombo);
        reasonCont.addComponent(reasonLbl);
        reasonCont.addComponent(reasonComboCont);




    }

    private void initItems() throws Exception {

        if (((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).newsetWorkItem(this.mWorkItem)) {
            ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).afterSetWorkItems(this.mWorkItem);
            initData();
        } else {
//            downloadForm();
        }


    }

    private void initData() {

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
            } else if (ques[k].getType().equals("Date")) {
                dateQues = ques[k];
            } else if (ques[k].getType().equals("dropdown")) {
                lotQues = ques[k];
            } else if (ques[k].getType().equals("textarea")) {
                notesQues = ques[k];
            } else if (ques[k].getType().equals("dropdown1")) {
                reasonQues = ques[k];
            }
        }

    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println(" @ Action Performed ");
        Object src = actionEvent.getSource();
        if (src == dateCmd) {
            CalendarCanvas calfoForm = new CalendarCanvas(this);
            ((Display) AppUtil.get().getItem(Constants.MIDP_DISPLAY)).setCurrent(calfoForm);
        } else if (src == dwnldCmd) {
            refreshLot();
        } else if (src == statusCombo) {
            String selection = statusCombo.getSelectedItem().toString();
            boolean status = true;
            if (selection.equalsIgnoreCase("Yes")) {
                status = true;
            } else if (selection.equalsIgnoreCase("No")) {
                status = false;
            }
            renderForm(status);
        } else if (src == cmdBack) {
            AppointmentWrapper apr = (AppointmentWrapper) AppUtil.get().getItem(Constants.APWR);
            AppList appList = new AppList(apr);
            AppUtil.get().setView(appList);
        } else if (src == cmdSave) {
            
            if(stringToBoolean(statusCombo.getSelectedItem().toString())){
                if(lotCombo.getSelectedIndex() > 0){
                    saveData();
                }else{
                   ErrorAlert errorAlert = new ErrorAlert(this, "ERROR \n Select lot number");
                errorAlert.show();
                }
            }else{
                saveData();
            }

            
        }
    }

    private void renderForm(boolean selection) {
        if (selection) {
            mainCont.removeAll();
            mainCont.addComponent(statusCont);
            mainCont.addComponent(dateCont);
            mainCont.addComponent(lotCont);
            mainCont.addComponent(notesCont);
        } else {
            mainCont.removeAll();
            statusCombo.setSelectedIndex(1, true);
            mainCont.addComponent(statusCont);
            mainCont.addComponent(reasonCont);
            mainCont.addComponent(notesCont);
        }

    }

    public Object getScreenObject() {
        return this;
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void resume(Hashtable args) {
        if (args != null) {
            if (args.containsKey(Constants.DATE)) {
                selectedDate = (Date) args.get(Constants.DATE);
                if (selectedDate != null) {
                    dateValue.setText(DateParser.getMvacStringDate(selectedDate));
                }
                AppUtil.get().setView(this);
            } else if (args.containsKey(Constants.RESUME)) {
                AppUtil.get().setView(this);
            } else if (args.containsKey(Constants.OK_RESUME)) {
                /**
                 * Successfully saved , return to appointment list
                 */
                AppointmentWrapper apr = (AppointmentWrapper) AppUtil.get().getItem(Constants.APWR);
                AppList appList = new AppList(apr);
                AppUtil.get().setView(appList);
            } else if (args.containsKey(Constants.ERROR)) {
                AppUtil.get().setView(this);
            }
        }
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        Date d = c.getTime();
        String date = DateParser.getMvacStringDate(d);
        return date;
    }

    private void refreshLot() {
        new BackgroundTask() {

            public void performTask() {
                progress = new FBProgressIndicator(RegAppointmentForm.this, "Refreshing Lot Information...");
                progress.showModeless();
                tm = new TransportManager("GET", RegAppointmentForm.this);
                String url = Constants.LOT_URL + (String) AppUtil.get().getItem(Constants.NURSENAME);
                Message msg = new Message(url);
                tm.sendMessage(msg);

            }
        }.start();
    }

    public void messageSent(Object args) {
        System.out.println(" Message Sent :" + (args != null ? args.toString() : "Null response"));
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
                refresh();
                initElements();

            }
        }
        progress.dispose();
        AppUtil.get().setView(this);
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
//                System.out.println(result[loop]);
            }
        }
        return result;
    }

    public void requestFailed(Object error) {
        if (error != null) {
            System.out.println(" ERROR . Request failed ." + error.toString());
        }
    }

    public void errorOccured(String arg0, Exception arg1) {
        System.out.println(" ERROR . Something went wrong " + (arg0 != null ? arg0 : "null string"));
        if (arg1 != null) {
            arg1.printStackTrace();
        }

    }

    /**
     * Clears combo and populates with fresh values
     * @param lotNumbers
     */
    private void populateLotCombo() {
        lotCombo = new ComboBox();
        lotCombo.addItem(defaultOpt[0]);
        LotNumbers lotNumbers = WFStorage.getLotNumbers(this);

        if (lotNumbers != null && lotNumbers.getLotNumbers() != null && lotNumbers.getLotNumbers().length() > 0) {
            lotNumArray = split(lotNumbers.getLotNumbers(), ",");
            if (lotNumArray.length > 0) {
                for (int i = 0; i < lotNumArray.length; i++) {
                    if (lotNumArray[i] != null && lotNumArray[i].length() > 0) {
                        lotCombo.addItem(lotNumArray[i]);
                    }
                }
            }
        }
    }

    private void saveData() {
        System.out.println("Question size:=>" + dipslayedQues.size());
        for (int i = 0; i < dipslayedQues.size(); i++) {
            QuestionData qd = (QuestionData) dipslayedQues.elementAt(i);
            String question = qd.getText();

            if (question.equals("Lot Number")) {
                qd.setTextAnswer((String) lotCombo.getSelectedItem());
            } else if (question.equals("Date of Immunization")) {
                if (selectedDate != null) {
                    qd.setTextAnswer(DateParser.getMvacStringDate(selectedDate));
                } else {
                    Calendar c = Calendar.getInstance(TimeZone.getDefault());
                    qd.setTextAnswer(DateParser.getMvacStringDate(c.getTime()));
                }
            } else if (question.equals("Status")) {
                qd.setTextAnswer((String) statusCombo.getSelectedItem());
            } else if (question.equals("Notes")) {
                qd.setTextAnswer(notesTxt.getText());
            } else if (question.equals("Reason")) {
                if (stringToBoolean(statusCombo.getSelectedItem().toString())) {
                    qd.setAnswer(null);
                } else {
                    qd.setTextAnswer((String) reasonCombo.getSelectedItem());
                }

            }
            dipslayedQues.setElementAt(qd, i);

        }
        ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).setListener(this);
        ((MvacController) AppUtil.get().getItem(Constants.CONTROLLER)).saveQtnDataFromForm(dipslayedQues, this.mWorkItem);

    }

    public void dataSaved(boolean resp) {
        if (resp) {
            GenericAlert genericAlert = new GenericAlert(RegAppointmentForm.this, "Successfully saved");
            genericAlert.addCommandListener(this);
            genericAlert.show();
        } else {
            System.out.println(" Error when saving");
            ErrorAlert errorAlert = new ErrorAlert(RegAppointmentForm.this, "Error when saving");
            errorAlert.show();
        }
    }

    private void refresh() {
        /**
         * Get temp data
         */
        if (statusQues == null) {
            statusQues = new QuestionListObj();
        }
        statusQues.setValue(statusCombo.getSelectedItem().toString());
        if (dateQues == null) {
            dateQues = new QuestionListObj();
        }
        if (selectedDate != null) {
            dateQues.setValue(selectedDate.toString());
        }
        if (lotQues == null) {
            lotQues = new QuestionListObj();
        }
        lotQues.setValue(lotCombo.getSelectedItem().toString());
        if (notesQues == null) {
            notesQues = new QuestionListObj();
        }
        notesQues.setValue(notesTxt.getText());
        if (reasonQues == null) {
            reasonQues = new QuestionListObj();
        }
        reasonQues.setValue(reasonCombo.getSelectedItem().toString());
        this.statusCont.removeAll();
        this.dateCont.removeAll();
        this.lotComboCont.removeAll();
        this.lotCont.removeAll();
        this.notesCont.removeAll();
        this.reasonComboCont.removeAll();
        this.reasonCont.removeAll();
    }

    private boolean stringToBoolean(String s) {
        boolean resp = false;
        if (s != null) {
            if (s.equalsIgnoreCase("Yes")) {
                resp = true;
            }
        }
        return resp;
    }

    private String getVaccine_dose() {
        String vaccine_dose = null;

        if (this.mWorkItem != null) {
            String vaccine = null;
            String dose = null;
            for (Enumeration e = mWorkItem.getPrefilledQns().elements(); e.hasMoreElements();) {
                MQuestionMap questionMap = (MQuestionMap) e.nextElement();
                if (questionMap.getParameter().equalsIgnoreCase("dose_name") ) {
                    dose = questionMap.getValue() ;
                }else if(questionMap.getParameter().equalsIgnoreCase("vaccine_name")){
                    vaccine = questionMap.getValue();
                }
            }
            vaccine_dose = vaccine+" - "+dose ;
        }

        return vaccine_dose;
    }

    private String getChildName(){
        String name = null ;
        for(Enumeration e = mWorkItem.getPrefilledQns().elements() ; e.hasMoreElements();){
            MQuestionMap questionMap = (MQuestionMap)e.nextElement() ;            
            if(questionMap.getParameter().equalsIgnoreCase("child_name")){
                name = questionMap.getValue();
            }
        }
        return name;
    }
}
