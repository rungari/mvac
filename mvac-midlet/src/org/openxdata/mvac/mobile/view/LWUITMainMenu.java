/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import java.util.Hashtable;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.communication.MvacTransportLayer;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.api.ControllerListener;
import org.openxdata.mvac.mobile.api.MVACController_new;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.NurseSettings;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.util.DebugLog;

/**
 *
 * @author mutahi
 */
public class LWUITMainMenu extends Form implements ActionListener, IView, ControllerListener {

    private Container container;
    private Button btnDownload;
    private Button btnAppointments;
    private Button btnSearch;
    private Command cmdDownload;
    private Command cmdAppointments;
    private Command cmdSearch;
    private FBProgressIndicator progress;
    private Command cmdLogout = null;
    private AppointmentsDownloadDialog downloadDialog = null;

    public LWUITMainMenu() {
        super("Main Menu");
        super.getTitleComponent().setAlignment(LEFT);
        progress = new FBProgressIndicator(this, "Downloading workitems..");
        this.addCommandListener(this);
        org.openxdata.mvac.util.DebugLog.getInstance().log(" About to init mainMenu ");
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
            this.resume(null);

        } else if (ae.getSource() == cmdLogout) {
            LWUITLoginForm loginForm = new LWUITLoginForm("User Login");
            AppUtil.get().setView(loginForm);
        }
    }

    private void download() {        
        MVACController_new.getInstance().downloadAppointments(this);
    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        System.gc();
        AppUtil.get().setView(this);

    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    /**
     * Flow :
     * 1. Check if there are any items to upload .
     * 2. If items anailable , call ngTask
     * 3. Else return
     */
    public void uploadCompletedItems() {
        MVACController_new.getInstance().uploadCompletedItems(this);
    }

    public void queryReturned(boolean yesNo) {
        DebugLog.getInstance().log("Query Returned");
        AppUtil.get().setView(this);
        if (yesNo) {
            uploadCompletedItems();
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
            DebugLog.getInstance().log("Exception thrown when fetching nurse name :" + e.getMessage());
            NurseSettings settings = new NurseSettings();
            AppUtil.get().setView(settings);
        }
    }

    public void requestComplete(Object response) {
        if (response.toString().equals(Constants.DOWNLOADED)) {
            GroupList groupList = new GroupList();
            if(progress != null ){
                progress.dispose(); progress = null;
            }
            AppUtil.get().setView(groupList);
        } else if (response.toString().equals(Constants.UPLOADED)) {            
            downloadAppointments();
        }
    }

    public void error(Object error) {
        if (error != null) {
            ErrorAlert errorAlert = new ErrorAlert(this, error.toString().trim());
            errorAlert.show();
        }else{
            ErrorAlert errorAlert = new ErrorAlert(this, "Some error occured !") ;
            errorAlert.show();
        }
    }

    public void showProgress(boolean visible, Object msg) {
        if (visible) {
            progress = new FBProgressIndicator(this, msg.toString());
            progress.showModeless();
        } else {
            if(progress != null ){
                progress.dispose();
                progress = null;
            }
            AppUtil.get().setView(this);
            if(msg != null ){
                if(msg.toString().trim().equals(Constants.DOWNLOADED.trim())){
                    /**
                     * Show loading progress
                     */
                    progress = new FBProgressIndicator(this, "Loading ...");
                    progress.disableCommands();
                    progress.showModeless();
                }
            }
        }
    }

}
