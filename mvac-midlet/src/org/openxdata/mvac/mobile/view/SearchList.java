/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view;

import org.openxdata.mvac.mobile.model.listmodel.SearchFilterProxyListModel;
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
import com.sun.lwuit.list.DefaultListModel;
import java.util.Hashtable;
import java.util.Vector;
import org.openxdata.communication.TransportLayerListener;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.ResponseHeader;
import org.openxdata.mvac.communication.MvacTransportLayer;
import org.openxdata.mvac.communication.model.SearchObject;
import org.openxdata.mvac.mobile.DownloadManager;
import org.openxdata.mvac.mobile.db.WFStorage;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.workflow.mobile.model.MWorkItemList;

/**
 *
 * @author mutahi
 */
public class SearchList extends Form implements FocusListener , StorageListener, TransportLayerListener,ActionListener , IView{
    
    private List searchResult = null;
    private SearchFilterProxyListModel proxyListModel = null;
    private  TextField field = new TextField("Click here to search", 22);
    private Label searchlbl = new Label("Search:");
    private Command cmdBack = null;
    private SearchObject selectedObject = null;
    private FBProgressIndicator progress;
    private DownloadManager dwnLdMgr;
    private final MvacTransportLayer transportlayer;
    DownloadDialog downloadDialog=null;

    public SearchList(Vector results) {
        super("Search Results");
        super.getTitleComponent().setAlignment(LEFT);
        transportlayer = new MvacTransportLayer();
        dwnLdMgr = new DownloadManager(transportlayer);
        progress = new FBProgressIndicator(this, "Downloading..");
        setLayout(new BorderLayout());
        addCommandListener(this);
        proxyListModel = new SearchFilterProxyListModel(new DefaultListModel(results));
        searchResult = new List(proxyListModel);
        searchResult.addActionListener(this);
        searchResult.setListCellRenderer(new SearchResultRenderer());
        searchResult.setFixedSelection(List.FIXED_NONE_CYCLIC);

        field.setConstraint(TextField.ANY);
        field.setLabelForComponent(searchlbl);
        field.addFocusListener(this);
        field.addDataChangeListener(new DataChangedListener() {

            public void dataChanged(int i, int i1) {
                if(!field.getText().equals("Click here to search")){
                    proxyListModel.filter(field.getText());
                }

            }
        });

        addComponent(BorderLayout.NORTH, field);
        addComponent(BorderLayout.CENTER, searchResult);

        cmdBack = new Command("Back");
        addCommand(cmdBack);
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

    public void actionPerformed(ActionEvent actionEvent) {

        Object srcObject = actionEvent.getSource() ;

        if(srcObject == searchResult){
            selectedObject = (SearchObject)searchResult.getSelectedItem();

            String msg = "Download appointment for " + selectedObject.getChild_name() + " ?";

            downloadDialog = new DownloadDialog(this, msg);
            downloadDialog.showModeless();
        }
        else if(srcObject == cmdBack){
            LWUITSearchForm searchForm = new LWUITSearchForm();
            AppUtil.get().setView(searchForm);
        }
    }

    public Object getScreenObject() {
        return this ;
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void resume(Hashtable args) {

        if(args != null){
            if(args.containsKey(DownloadDialog.OK)){

                String app_ID = selectedObject.getAppointment_ID() ;
                AppUtil.get().putItem(Constants.DOWNLOAD_DATE, "11");
                downloadDialog.dispose();
                download();
                
                //Send request here 

            }else if(args.containsKey(DownloadDialog.BACK)){
                AppUtil.get().setView(this);
            }
        }


        
    }
    
    
    private void download() {
        progress.showModeless();
        System.out.println("My task is abt started");
        new BackgroundTask() {

            public void performTask() {
                dwnLdMgr.downloadWorkItemsByIISID(SearchList.this);
            }

            public void taskStarted() {
                System.out.println("My task is started");
            }
        }.start();


    }

    public void errorOccured(String string, Exception excptn) {
        AppUtil.get().setView(this);
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
            
            AppUtil.get().setView(new GroupList());
        }
        WFStorage.saveMWorkItemList(dataOut, this);
        AppUtil.get().setView(new GroupList());

    }

    public void cancelled() {
    }

    public void updateCommunicationParams() {
    }

}
