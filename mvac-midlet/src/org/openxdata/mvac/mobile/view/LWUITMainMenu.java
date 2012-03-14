/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import com.jcraft.jzlib.ZStreamException;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import org.openxdata.communication.TransportLayerListener;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.ResponseHeader;
import org.openxdata.model.UserListStudyDefList;
import org.openxdata.mvac.communication.MvacTransportLayer;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.WIRDownload;
import org.openxdata.mvac.mobile.api.FormUtil;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.NurseSettings;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.workflow.mobile.model.MWorkItem;
import org.openxdata.workflow.mobile.model.MWorkItemList;

/**
 *
 * @author mutahi
 */
public class LWUITMainMenu extends Form implements ActionListener, IView, TransportLayerListener, StorageListener {

    private Container container;
    private Button btnDownload;
    private Button btnAppointments;
    private Button btnSearch;
    private Command cmdDownload;
    private Command cmdAppointments;
    private Command cmdSearch;
    private MvacTransportLayer transportlayer;
    private FBProgressIndicator progress;
    private DownloadManager dwnLdMgr;
    private Command cmdLogout = null;
    private AppointmentsDownloadDialog downloadDialog = null;
    

    public LWUITMainMenu() {
        super("Main Menu");
        super.getTitleComponent().setAlignment(LEFT);
        transportlayer = new MvacTransportLayer();
        dwnLdMgr = new DownloadManager(transportlayer);
        progress = new FBProgressIndicator(this, "Downloading workitems..");
        this.addCommandListener(this);
        initMainMenu();

    }

    void initMainMenu() {
        setLayout(new BorderLayout());


        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        cmdDownload = new Command("Plan Appointments");
        cmdAppointments = new Command("Open Appointments");
        cmdSearch = new Command("Search");

        btnAppointments = new Button(cmdAppointments);        
        btnAppointments.addActionListener(this);
        


        btnDownload = new Button(cmdDownload);        
        btnDownload.addActionListener(this);

        btnSearch = new Button(cmdSearch);        
        btnSearch.addActionListener(this);

        container.addComponent(btnDownload);
        container.addComponent(btnAppointments);
        container.addComponent(btnSearch);

        this.addComponent(BorderLayout.CENTER, container);

        this.cmdLogout = new Command("Logout");

        this.addCommand(cmdLogout);

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand() == cmdDownload) {

            /**
             * TODO : Use this flow
             * 1. Show download dialog , user selects date range
             * 2. When user selects yes to Download - Upload done data
             * 3. if Uploaded : Download new data
             */
//            uploadCompletedItems();
            downloadDialog = new AppointmentsDownloadDialog(this);
            AppUtil.get().setView(downloadDialog);


        } else if (ae.getCommand() == cmdAppointments) {
            System.out.println("Appointments");
            AppUtil.get().setView(new GroupList());

        } else if (ae.getCommand() == cmdSearch) {
            System.out.println("Search");
            LWUITSearchForm sform = new LWUITSearchForm();
            AppUtil.get().setView(sform);

        } else if (ae.getCommand() == progress.cancel) {
            //transportlayer.
            this.resume(null);

        } else if (ae.getSource() == cmdLogout) {
            LWUITLoginForm loginForm = new LWUITLoginForm("User Login");
            AppUtil.get().setView(loginForm);
        } 
    }

    private void download() {

        progress = new FBProgressIndicator(this, "Downloading Items.. Please wait...");
        progress.showModeless();

        
        new BackgroundTask() {

            public void performTask() {
                try {
                    
                    dwnLdMgr.downloadWorkItems(LWUITMainMenu.this);
                } catch (Error error) {
                    System.out.println("Error . Failed to download workitems ." + error.toString());
                } catch (Throwable t) {
                    System.out.println(" Something thrown :" + t);
                }
            }

            public void taskStarted() {
                System.out.println("My task is started");
            }
        }.start();


    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        System.gc();
//        transportlayer = null;
//        transportlayer = new MvacTransportLayer();
        if (args != null) {
            String msg = (String) args.get("msg");
            if (msg.trim().equalsIgnoreCase("Upload Success")) {

                /**
                 * Download data now 
                 */
                System.out.println("About to download appointments");
                downloadAppointments();
            }

        } else {
            AppUtil.get().setView(this);
        }

    }


    public void uploaded(Persistent dataOutParams, Persistent dataOut) {

        System.out.println("@ uploaded");
        if (progress != null) {
            progress.dispose();
            progress = null;
        }

        transportlayer = null;
        transportlayer = new MvacTransportLayer();



        Hashtable args = new Hashtable();
        args.put("msg", "Upload Success");
        this.resume(args);

    }

    public void downloaded(Persistent dataOutParams, Persistent dataOut) {

        transportlayer = null;
        transportlayer = new MvacTransportLayer();
        ResponseHeader rh = (ResponseHeader) dataOutParams;
        if (!rh.isSuccess()) {
            System.out.println("inside !issuccess");
            return;
        } else if (dataOut instanceof MWorkItemList) {
            try{
                saveAndDisplayWorkItems((MWorkItemList) dataOut);
            }catch(Exception e){
               errorOccured("Failed to download appointments", null);
            }
            
        } else if (dataOut instanceof UserListStudyDefList) {
            System.out.println("Saving the forms");
            try{
               handleStudyAndUserDownloaded((UserListStudyDefList) dataOut);
            }catch(Throwable t){
                t.printStackTrace();
            }
            
            dataOut = null;
            AppUtil.get().setView(new GroupList());

        }else if(dataOut instanceof WIRDownload){
            System.out.println(" WIRDownload object received . About to process ");
            WIRDownload wIRDownload = (WIRDownload)dataOut ;
            Vector wirSummary = wIRDownload.getWirSummaries();
            if(wirSummary != null && wirSummary.size()>0){
                dataOut = null;
                wIRDownload = null;
                wirSummary = null;
                downloadAssociatedForm();
            }
            

        }
        else {
            System.out.println("some other object returned ");

        }
    }



    private void saveAndDisplayWorkItems(MWorkItemList dataOut) {
        if (dataOut.getmWorkItems().isEmpty()) {
            //view.showMsg("No WorkItems Available");
            System.out.println("inside is empty");
            
            AppUtil.get().setView(new GroupList());
        }
        WFStorage.saveMWorkItemList(dataOut, this);
        dataOut = null;
        downloadAssociatedForm();
    }

    public void downloadAssociatedForm() {

        int storageSize = WFStorage.getWorkItemStorageSize(this);
        if(storageSize > 0){
            MWorkItem workItem = null;
            try{
                workItem = (MWorkItem)WFStorage.getWorkItem(1, this);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(workItem != null ){
                FormUtil formUtil = new FormUtil();
                if(formUtil.getFormDef(workItem) == null){
                    System.out.println(" <<<<<<<<<<<<<<<<< About to download associated forms >>>>>>>>>>>");
                    downloadForm();
                }else{
                    progress.dispose();
                    AppUtil.get().setView(new GroupList());
                }
            }
            workItem = null;
        }
    }

    private void handleStudyAndUserDownloaded(UserListStudyDefList userListStudyDefList) {
        WFStorage.saveUserListStudyDefList(userListStudyDefList);
    }

    public void downloadForm() {
        progress = new FBProgressIndicator(this, "Downloading associated studies...");
        progress.showModeless();


        new BackgroundTask() {

            public void performTask() {
                dwnLdMgr.downloadStudies(LWUITMainMenu.this);
            }

            public void taskStarted() {
                System.out.println("My task is started");
            }
        }.start();

    }

    public void errorOccured(String string, Exception excptn) {

        System.out.println("Error Occured :" + string);
        if (excptn != null) {
            System.out.println("My Error-->" + excptn.getMessage());
            excptn.printStackTrace();
            if (excptn instanceof ZStreamException) {
                System.out.println("Uploaded but have a ZLIB Exection. Overiding the Norm");
                //progress.dispose();

//                transportlayer = null;
//                transportlayer = new MvacTransportLayer();
//
//                WFStorage_old.deleteAllWorkItems(this, true);
//
//                System.out.println("In upload=>returning to view");
//
//                Hashtable args = new Hashtable();
//                args.put("msg", "success");
//                this.resume(args);

            } else if (excptn instanceof ConnectionNotFoundException || excptn instanceof IOException) {
                ErrorAlert errorAlert = new ErrorAlert(this, " Failed to connect to server ");
                errorAlert.show();
            } else {
                ErrorAlert errorAlert = new ErrorAlert(this, excptn.getMessage());
                errorAlert.show();

            }

        } else {
            System.out.println("My Error with null excp");
            ErrorAlert eA = new ErrorAlert(this, " Error occured ");
            eA.show();
        }
        progress.dispose();
        AppUtil.get().setView(this);
    }

    public void cancelled() {
    }

    public void updateCommunicationParams() {
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void uploadCompletedItems() {
        progress = new FBProgressIndicator(this, "Uploading Data...");
        progress.showModeless();

        new BackgroundTask() {

            public void performTask() {
                try {
                    dwnLdMgr.uploadWorkItems(LWUITMainMenu.this);
                } catch (Exception e) {
                    System.out.println("Error . Failed to upload workitems ." + e.toString());
                } catch (Error e) {
                    System.out.println("Error . Failed to upload workitems ." + e.toString());
                }
            }

            public void taskStarted() {
            }
        }.start();
    }

    public void queryReturned(boolean yesNo) {
        System.out.println("Query Returned");
        AppUtil.get().setView(this);


        /**
         * Upload Data first
         */
        if (yesNo) {
            uploadCompletedItems();
//                    downloadAppointments();

        }



    }

    private void downloadAppointments() {
        Object object = null;
        try {
            object = AppUtil.get().getItem(Constants.NURSENAME);
            if (object != null && object.toString().length() > 0 && (!object.toString().equals(""))) {

                download();

            } else {
                NurseSettings nurseSettings = new NurseSettings();
                AppUtil.get().setView(nurseSettings);
            }
        } catch (Exception e) {
            System.out.println("Exception thrown when fetching nurse name :" + e.getMessage());
            NurseSettings settings = new NurseSettings();
            AppUtil.get().setView(settings);
        }
    }
}
