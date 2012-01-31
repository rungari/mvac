/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
//import com.sun.lwuit.Font;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import org.openxdata.communication.TransportLayerListener;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.ResponseHeader;
import org.openxdata.mvac.communication.ITransportListener;
import org.openxdata.mvac.communication.MvacTransportLayer;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.communication.model.XmlParser;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.DateParser;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.widgets.CalendarCanvas;
import org.openxdata.workflow.mobile.model.MWorkItemList;

/**
 *
 * @author mutahi
 */
public class LWUITSearchForm extends Form implements IView, StorageListener, TransportLayerListener, ActionListener,ITransportListener {

    private Container container;
    private Label lblbirthplace;
    private Label lblbirthdate;
    private Label lblfName;
    private Label lblLName;
    //private Label lblNationalID;
    private Button btnFrom;
    private Button btnTo;
    private TextField cbPlaces=null;
    private Command cmdSearch;
    private Command cmdBack;
    private Command cmdFrom;
    private Command cmdTo;
    private TextField txt_birthplace = null;
    private TextField txt_fname = null;
    private TextField txt_lname = null;
    //private TextField txt_nationalID = null;
//    private CalendarForm calendarForm = null;
    private MvacTransportLayer transportlayer;
    private FBProgressIndicator progress;
    private DownloadManager dwnLdMgr;
    private Date fromDate = null;
    private Date toDate = null;
    private String fromdateLabel = "";
    private String todateLabel = "";
    private String selectedButton = null;
    private final String fromButton = "from";
    private final String toButton = "to";
    private TransportManager tm;
//    String 
    //Test
    ValidSearchAlert alerts = new ValidSearchAlert(10);

    public LWUITSearchForm() {
        super("Search Child");
        tm = new TransportManager("GET", this);
        initSearchForm();

//        calendarForm = new CalendarForm();
    }

    private void initSearchForm() {
        setLayout(new BorderLayout());
        transportlayer = new MvacTransportLayer();
        dwnLdMgr = new DownloadManager(transportlayer);
        progress = new FBProgressIndicator(this, "Searching..");
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        lblbirthdate = new Label("Date of Birth");
        lblbirthplace = new Label("Place of Birth");
        lblfName = new Label("First Name");
        lblLName = new Label("Last Name");
        //lblNationalID = new Label("National ID");

        txt_birthplace = new TextField();
        txt_fname = new TextField();
        txt_lname = new TextField();
        //txt_nationalID = new TextField();


        cmdFrom = new Command("From Date");
        cmdTo = new Command("To Date");

        btnFrom = new Button(cmdFrom);
        btnFrom.setAlignment(CENTER);
        btnFrom.setSelectedStyle(new Style(0xffffff, 0x69b510, Font.getBitmapFont("NokiaSansWide14Bold"), (byte)255));
        btnFrom.getStyle().setBorder(Border.createBevelRaised());
        btnFrom.setWidth(20);
        btnTo = new Button(cmdTo);
        btnTo.setAlignment(CENTER);
        btnTo.setSelectedStyle(new Style(0xffffff, 0x69b510, Font.getBitmapFont("NokiaSansWide14Bold"), (byte)255));
        btnTo.getStyle().setBorder(Border.createBevelRaised());
        btnTo.setWidth(20);

        cbPlaces = new TextField();
        //Test Data
        //cbPlaces.addItem("Albania");

        container.addComponent(lblfName);
        container.addComponent(txt_fname);

        container.addComponent(lblLName);
        container.addComponent(txt_lname);

        container.addComponent(lblbirthdate);
        container.addComponent(btnFrom);
        //container.addComponent(txt_from);
        container.addComponent(btnTo);
        //container.addComponent(txt_to);

        container.addComponent(lblbirthplace);
        // container.addComponent(txt_birthplace);
        container.addComponent(cbPlaces);

        //container.addComponent(lblNationalID);
        //container.addComponent(txt_nationalID);

        this.addComponent(BorderLayout.CENTER, container);

        cmdSearch = new Command("Search", 1);
        cmdBack = new Command("Back", 2);

        addCommand(cmdBack);
        addCommand(cmdSearch);
        


        addCommandListener(this);

    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        AppUtil.get().setView(this);

        if (args != null) {

            if(args.containsKey("date")){
                if (selectedButton != null) {
                Object setDate = args.get("date");
                System.out.println("Selected Date is :" + setDate);
                if (selectedButton.equals(fromButton)) {
                    fromDate = (Date) setDate;
                    System.out.println("Set from date is :" + fromDate);

                    String d = DateParser.getMvacStringDate(fromDate);
                    btnFrom.setText(d);
                    setFromdateLabel(d);
                } else if (selectedButton.equals(toButton)) {
                    toDate = (Date) setDate;
                    System.out.println("Set to date is :" + toDate);
                    btnTo.setText(DateParser.getMvacStringDate(toDate) );
                    setTodateLabel(DateParser.getMvacStringDate(toDate));
                }
                selectedButton = null;
                 }
            }else if(args.containsKey("GEN_ALERT")){
                //System.out.println("Action is :" + SearchDialog.VIEW);
                //Show results from 11-50 range
                AppUtil.get().setView(this);

            }else if(args.containsKey(SearchDialog.VIEW)){
                System.out.println("Action is :" + SearchDialog.VIEW);
                //Show results from 11-50 range

            }else if(args.containsKey(SearchDialog.DISCARD)){
                System.out.println("Action is :" + SearchDialog.DISCARD);
                //Dont show results . return Search form
                AppUtil.get().setView(this);

            }else if(args.containsKey(SearchDialog.OK)){
                System.out.println("Action is :" + SearchDialog.OK);
                //Return Search form
                AppUtil.get().setView(this);

            }else if(args.containsKey(SearchDialog.CANCEL)){
                System.out.println("Action is :" + SearchDialog.CANCEL);

                //Return main menu

                LWUITMainMenu mainMenu = new LWUITMainMenu();
                AppUtil.get().setView(mainMenu);
            }
            



        }

    }

    private void download() {
        progress.showModeless();
        System.out.println("My task is abt started");
        new BackgroundTask() {

            public void performTask() {
                dwnLdMgr.downloadWorkItemsByIISID(LWUITSearchForm.this);
            }

            public void taskStarted() {
                System.out.println("My task is started");
            }
        }.start();


    }

    public void errorOccured(String string, Exception excptn) {
    }

    public void uploaded(Persistent prstnt, Persistent prstnt1) {
    }

    public void downloaded(Persistent dataOutParams, Persistent dataOut) {
        progress.dispose();
        System.out.println("inside downloaded");
        ResponseHeader rh = (ResponseHeader) dataOutParams;
        if (!rh.isSuccess()) {
            System.out.println("inside !issuccess");
            return;
        } else if (dataOut instanceof MWorkItemList) {
            System.out.println("inside instance of");
            saveAndDisplayWorkItems((MWorkItemList) dataOut);
        }
    }

    private void saveAndDisplayWorkItems(MWorkItemList dataOut) {
        if (dataOut.getmWorkItems().isEmpty()) {
            //view.showMsg("No WorkItems Available");
            System.out.println("inside is empty");
            GroupList appList = new GroupList("Saved appointments");
            AppUtil.get().setView(appList);
        }
        WFStorage.saveMWorkItemList(dataOut, this);
        GroupList appList = new GroupList("Saved appointments");
        AppUtil.get().setView(appList);

    }

    public void cancelled() {
    }

    public void updateCommunicationParams() {
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == cmdBack) {
            AppUtil.get().setView(new LWUITMainMenu());
        } else if (actionEvent.getSource() == cmdSearch) {
            System.out.println("Search");
            
            if(paramsCorrect()){
                search();
            }else{
                GenericAlert genericAlert = new GenericAlert(LWUITSearchForm.this, "Please enter Date information. Try Again");
                genericAlert.show();
            }

            

//            alerts.show();

            /***
             * Test
             */
        } else if (actionEvent.getSource() == cmdFrom) {
            selectedButton = fromButton;
            CalendarCanvas calfoForm = new CalendarCanvas(this);
            ((javax.microedition.lcdui.Display) AppUtil.get().getItem(Constants.MIDP_DISPLAY)).setCurrent(calfoForm);
        } else if (actionEvent.getSource() == cmdTo) {
            selectedButton = toButton;
            CalendarCanvas calfoForm = new CalendarCanvas(this);
            ((javax.microedition.lcdui.Display) AppUtil.get().getItem(Constants.MIDP_DISPLAY)).setCurrent(calfoForm);
        }

    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void messageSent(Object args) {
        //throw new UnsupportedOperationException("Not supported yet.");
        
        tm.closeConnections();
        String response = (String)args;
        
        System.out.println("Search response=>"+response);
        if (response.startsWith("<DocumentElement")) {
            /***
             * Test
             */
            XmlParser xmlParser = new XmlParser();
            InputStream is = new ByteArrayInputStream(response.getBytes());
            
            Reader reader = new InputStreamReader(is);
            Object object = xmlParser.processXml(reader);

            if(object instanceof Vector) {
                Vector searchResults = (Vector)object ;
                System.out.println("Number of results :" + searchResults.size());
                if(searchResults.size() <= 10){
                    //Display Results here
                    SearchList searchList = new SearchList(searchResults);
                    AppUtil.get().setView(searchList);
                }else if(searchResults.size() <= 50){
                    String msg = searchResults.size()+" results found . Do you want to view?";
                    SearchDialog searchDialog = new SearchDialog(this, msg, SearchDialog.RANGE_11_50);
                    searchDialog.show();
                }else if(searchResults.size() > 50){
                    String msg = "Invalid search results .Please refine the search." ;
                    SearchDialog searchDialog = new SearchDialog(this,msg, SearchDialog.RANGE_OVER50);
                    searchDialog.show();
                }
                
            }
            
        }else{
            GenericAlert genericAlert = new GenericAlert(LWUITSearchForm.this, "Error processing response from server. Try Again");
                genericAlert.show();
        }
        
    }

    private boolean paramsCorrect() {
        System.out.println("LABELS AS=>"+getTodateLabel()+""+getFromdateLabel());
        if (getTodateLabel()!="" && getFromdateLabel()!="") {
            return true;
        }else{
            return false;
        }
        
    }

    private void search() {
        //throw new UnsupportedOperationException("Not yet implemented");
        new BackgroundTask() {

            public void performTask() {
                progress = new FBProgressIndicator(LWUITSearchForm.this,"Searching...");
                progress.showModeless();
                tm = new TransportManager("GET", LWUITSearchForm.this);
                //String url =  + (String)AppUtil.get().getItem(Constants.NURSENAME);
                Message msg = new Message(Constants.SEARCH_URL);
                msg.setParam("datefrom", LWUITSearchForm.this.getFromdateLabel());
                msg.setParam("dateto", LWUITSearchForm.this.getTodateLabel());
                msg.setParam("firstname", LWUITSearchForm.this.getFirstname());
                msg.setParam("lastname", LWUITSearchForm.this.getLastname());
                msg.setParam("birthplace", LWUITSearchForm.this.getPlaceOfBirth());
                tm.sendMessage(msg);
                
                //dwnLdMgr=((MvacController)AppUtil.get().getItem(Constants.CONTROLLER)).getDM();
                //dwnLdMgr.downloadLotNames(AppointmentForm.this);
            }
        }.start();
        
    }

    public void requestFailed(Object error) {
        if(error!=null){
            /**
             * TODO : Handle request failed 
             */
        }
    }

    public abstract class BackgroundTask {

        /**
         * Start this task
         */
        public final void start() {
            if (Display.getInstance().isEdt()) {
                taskStarted();
            } else {
                Display.getInstance().callSeriallyAndWait(new Runnable() {

                    public void run() {
                        taskStarted();
                    }
                });
            }
            new Thread(new Runnable() {

                public void run() {
                    if (Display.getInstance().isEdt()) {
                        taskFinished();
                    } else {
                        performTask();
                        Display.getInstance().callSerially(this);
                    }
                }
            }).start();
        }

        /**
         * Invoked on the LWUIT EDT before spawning the background thread, this allows
         * the developer to perform initialization easily.
         */
        public void taskStarted() {
        }

        /**
         * Invoked on a separate thread in the background, this task should not alter
         * UI except to indicate progress.
         */
        public abstract void performTask();

        /**
         * Invoked on the LWUIT EDT after the background thread completed its
         * execution.
         */
        public void taskFinished() {
        }
    }

    private String getLabel(String date) {
        String day = date.substring(0, 3);
        String month = date.substring(4, 7);
        String d = date.substring(8, 10);
        String year = date.substring(24, date.length());
        //String label = day.trim() + " " + month.trim() + " " + d.trim() + " " + year.trim();
        if(month.trim().length()<2){
            month = "0"+month.trim();
        }
        
        if(day.trim().length()<2){
            day = "0"+day.trim();
        }
        String label = year.trim()+"-"+ month+"-"+day;
        return label;
    }
    
    private String getSearchDateLabel(String date) {
        String day = date.substring(0, 3);
        String month = date.substring(4, 7);
        String d = date.substring(8, 10);
        String year = date.substring(24, date.length());
        String label = year.trim()+"-"+ month.trim()+"-"+day.trim();
        return label;
    }

    public void setToDate(Date toDate) {
        setTodateLabel(getLabel(toDate.toString()));
        this.toDate = toDate;
    }

    public void setFromDate(Date fromDate) {
        setFromdateLabel(getLabel(fromDate.toString()));
        this.fromDate = fromDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setFromdateLabel(String fromdateLabel) {
        this.fromdateLabel = fromdateLabel;
    }

    public String getFromdateLabel() {
        return fromdateLabel;
    }

    public void setTodateLabel(String todateLabel) {
        this.todateLabel = todateLabel;
    }

    public String getTodateLabel() {
        return todateLabel;
    }

    public String getLastname() {
        return txt_lname.getText();
    }

    public String getFirstname() {
        return txt_fname.getText();
    }

    public String getPlaceOfBirth() {
        return cbPlaces.getText().toString();
    }

    
}
