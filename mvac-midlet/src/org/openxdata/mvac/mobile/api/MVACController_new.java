package org.openxdata.mvac.mobile.api;

import com.jcraft.jzlib.ZStreamException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import org.openxdata.communication.TransportLayerListener;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.ResponseHeader;
import org.openxdata.model.UserListStudyDefList;
import org.openxdata.mvac.communication.ITransportListener;
import org.openxdata.mvac.communication.MvacTransportLayer;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.WIRDownload;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.BackgroundTask;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.util.DebugLog;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 * @author mutahi
 */
public class MVACController_new implements ITransportListener, TransportLayerListener, StorageListener {

    private static MVACController_new Instance = null;
    private TransportManager transportManager = null;
    private MvacTransportLayer transportlayer;
    private DownloadManager dwnLdMgr;
    private ControllerListener clistener = null;

    private MVACController_new() {
        init();
    }

    public static MVACController_new getInstance() {
        if (Instance == null) {
            Instance = new MVACController_new();
        }
        return Instance;
    }

    private void init() {
        transportManager = new TransportManager("GET", MVACController_new.this);
        transportlayer = new MvacTransportLayer();
        dwnLdMgr = new DownloadManager(transportlayer);
    }

    public void messageSent(Object args) {
        DebugLog.getInstance().log(" @ MVACCONTROLLER . MessageSent . RESPONSE IS :" + (args != null ? args.toString() : "Null"));
        transportManager.closeConnections();
        String response = (String) args;
        if (response.equals("200")) {
            if (clistener != null) {
                clistener.requestComplete(response);
            }
        } else if (response.equals("201")) {
            if (clistener != null) {
                clistener.error(Constants.INVALID_LOGIN);
            }
        }
        clistener = null;
    }

    public void requestFailed(Object error) {
        if (error != null) {
            if (error instanceof ConnectionNotFoundException) {
                System.out.println("Error . Request failed :" + ((ConnectionNotFoundException) error).getMessage());
                if (clistener != null) {
                    clistener.error(Constants.FAILED_CONNECTION);
                }
            } else {
                if (clistener != null) {
                    clistener.error(Constants.FAILED_CONNECTION);
                }
            }
        }
        clistener = null;
    }

    public void userLogin(String username, String password, ControllerListener listener) {
        if (listener != null) {
            clistener = listener;
        }
        Message msg = new Message(Constants.AUTH_URL);
        msg.setParam("uname", username);
        msg.setParam("pwd", password);
        transportManager.sendMessage(msg);
    }

    public void uploaded(Persistent dataOutParams, Persistent dataOut) {

        transportlayer = null;
        transportlayer = new MvacTransportLayer();
        if(dataOutParams != null){
        ResponseHeader rh = (ResponseHeader)dataOutParams ;
        if(!rh.isSuccess()){
            DebugLog.getInstance().log("Upload not a Sucess . Do something ");
        }else{
           if(dataOut != null ) DebugLog.getInstance().log("@Downloaded . Response instance of :" + dataOut.getClass().toString());
        }
        }

        System.out.println("@ uploaded");
        if (clistener != null) {
            /**
             * Clear store
             */
            WFStorage.deleteAllWorkItems(MVACController_new.this, true);
            clistener.showProgress(false, Constants.UPLOADED);
            clistener.requestComplete(Constants.UPLOADED);
        }

        transportlayer = null;
        transportlayer = new MvacTransportLayer();


    }

    public void downloaded(Persistent dataOutParams, Persistent dataOut) {
        transportlayer = null;
        transportlayer = new MvacTransportLayer();
        ResponseHeader rh = (ResponseHeader) dataOutParams;
        if (!rh.isSuccess()) {
            DebugLog.getInstance().log("inside not issuccess");
            return;
        } else {
            DebugLog.getInstance().log("@Downloaded . Response instance of :" + dataOut.getClass().toString());
            if (dataOut instanceof WIRDownload) {
                WIRDownload wIRDownload = (WIRDownload) dataOut;
                Vector wirSummary = wIRDownload.getWirSummaries();
                if (wirSummary != null && wirSummary.size() > 0) {
                    dataOut = null;
                    wIRDownload = null;
                    wirSummary = null;
                    downloadAssociatedForm();
                }
            } else if (dataOut instanceof UserListStudyDefList) {
                handleStudyAndUserDownloaded((UserListStudyDefList) dataOut);
                dataOut = null;
                DebugLog.getInstance().log(" @Downloaded . Return to view ");
                if (clistener != null) {
                    clistener.showProgress(false, Constants.DOWNLOADED);
                    clistener.requestComplete(Constants.DOWNLOADED);
                }
            }
        }
    }

    private void handleStudyAndUserDownloaded(UserListStudyDefList userListStudyDefList) {
        WFStorage.saveUserListStudyDefList(userListStudyDefList);
    }

    public void downloadAssociatedForm() {

        int storageSize = WFStorage.getWorkItemStorageSize(this);
        if (storageSize > 0) {
            MWorkItem workItem = null;
            try {
                workItem = (MWorkItem) WFStorage.getWorkItem(1, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (workItem != null) {
                FormUtil formUtil = new FormUtil();
                if (formUtil.getFormDef(workItem) == null) {
                    DebugLog.getInstance().log(" <<<<<<<<<<<<<<<<< About to download associated forms >>>>>>>>>>>");
                    downloadForm();
                } else {
                    if (clistener != null) {
                        clistener.showProgress(false, null);
                    }
                    /**
                     * TODO : Show next screen
                     */
                    if (clistener != null) {
                        clistener.requestComplete(Constants.DOWNLOADED);
                    }
                }
            }
            workItem = null;
        }
    }

    public void downloadForm() {

        if (clistener != null) {
            clistener.showProgress(true, "Downloading associated studies...");
        }

        new BackgroundTask() {

            public void performTask() {
                dwnLdMgr.downloadStudies(MVACController_new.this);
            }

            public void taskStarted() {
                DebugLog.getInstance().log("My task is started");
            }
        }.start();

    }

    public void errorOccured(String string, Exception excptn) {
        DebugLog.getInstance().log("Error Occured :" + string);
        if (excptn != null) {
            DebugLog.getInstance().log("My Error-->" + excptn.getMessage());
            excptn.printStackTrace();
            if (excptn instanceof ZStreamException) {
                DebugLog.getInstance().log("Uploaded but have a ZLIB Exection. Overiding the Norm");
            } else if (excptn instanceof ConnectionNotFoundException || excptn instanceof IOException) {
                if (clistener != null) {
                    clistener.error(Constants.FAILED_CONNECTION);
                }
            } else {
                if (clistener != null) {
                    clistener.error(excptn.getMessage());
                }
            }

        } else {
            DebugLog.getInstance().log("My Error with null excp");
            if (clistener != null) {
                clistener.error(Constants.ERROR);
            }
        }

        if (clistener != null) {
            clistener.showProgress(false, Constants.RESUME);
        }

    }

    public void cancelled() {
    }

    public void updateCommunicationParams() {
    }

    /**
     * Flow :
     * 1. Check if there are any items to upload .
     * 2. If items anailable , call ngTask
     * 3. Else return
     */
    public void uploadCompletedItems(ControllerListener listener) {
        if (listener != null) {
            clistener = listener;
        }
        if (WFStorage.getCompleteMWorkItemList(this).size() > 0) {
            if (clistener != null) {
                clistener.showProgress(true, "Uploading data");
            }

            new BackgroundTask() {

                public void performTask() {
                    try {
                        dwnLdMgr.uploadWorkItems(MVACController_new.this);
                    } catch (Exception e) {
                        System.out.println("Error . Failed to upload workitems ." + e.toString());
                    } catch (Error e) {
                        System.out.println("Error . Failed to upload workitems ." + e.toString());
                    }
                }

                public void taskStarted() {
                }
            }.start();
        }else{
            DebugLog.getInstance().log(" @UploadCompleteItems . No Items to upload . Calling complete ");
            if(clistener!=null)clistener.requestComplete(Constants.UPLOADED);
        }
    }

    public void downloadAppointments(ControllerListener listener) {
        if (listener != null) {
            this.clistener = listener;
            this.clistener.showProgress(true, "Downloading Items.. Please wait...");
        }
        new BackgroundTask() {

            public void performTask() {
                try {
                    dwnLdMgr.downloadWorkItems(MVACController_new.this);
                } catch (Error error) {
                    DebugLog.getInstance().log("Error . Failed to download workitems ." + error.toString());
                } catch (Throwable t) {
                    DebugLog.getInstance().log(" Something Error thrown :" + t);
                }
            }

            public void taskStarted() {
                DebugLog.getInstance().log("My task is started");
            }
        }.start();
    }
}
