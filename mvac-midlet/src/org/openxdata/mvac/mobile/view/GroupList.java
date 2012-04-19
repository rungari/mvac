/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

import org.openxdata.mvac.mobile.model.listmodel.GroupFilterProxyListModel;
import org.openxdata.mvac.mobile.model.AppointmentWrapper;
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
import java.io.IOException;
import java.util.Hashtable;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.mobile.builder.AppointmentGroupBuilder;
import org.openxdata.mvac.mobile.builder.AppointmentInterface;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.renderers.GroupListCellRenderer;
import org.openxdata.mvac.util.DebugLog;

/**
 *
 * @author soyfactor
 */
public class GroupList extends Form implements IView, StorageListener, ActionListener, FocusListener, AppointmentInterface {

    private AppointmentWrapper[] apps;
    private List list;
    private TextField field = null;
    private Label searchlbl = new Label();
    private Image searchIcon = null;
    private AppListModel appListModel;
    private GroupFilterProxyListModel proxyModel;
    private Command back;
    private Container searchCnt = null;
    private FBProgressIndicator progress = null;

    public GroupList() {
        super("Children Due");
        super.getTitleComponent().setAlignment(LEFT);
        getTitleStyle().setFgColor(0x7AE969, true);
        AppointmentGroupBuilder.getInstance().build(this);        

    }
    

    private void initView() {

        back = new Command("Back", 1);
        addCommand(back);
        addCommandListener(this);

        appListModel = new AppListModel(apps);
        proxyModel = new GroupFilterProxyListModel(appListModel);
        list = new List(proxyModel);
        list.addActionListener(this);
        list.setListCellRenderer(new GroupListCellRenderer());
        list.setFixedSelection(List.FIXED_NONE);
        if (list.size() > 0) {
            list.setSelectedIndex(0, true);
        }
        setFocused(list);


        setLayout(new BorderLayout());


        try {
            searchIcon = Image.createImage("/search.png");
        } catch (IOException exception) {
            System.out.println(" ERROR . Failed to create Image" + exception.getMessage());
        }

        initSearchCont();

        addComponent(BorderLayout.NORTH, searchCnt);
        addComponent(BorderLayout.CENTER, list);
    }

    private void initSearchCont() {
        searchCnt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        if (searchIcon != null) {
            searchlbl.setIcon(searchIcon);
        }
        searchlbl = new Label();
        if (searchIcon != null) {
            searchlbl.setIcon(searchIcon);
        } else {
            searchlbl.setText("Search :");
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


        searchCnt.addComponent(searchlbl);
        searchCnt.addComponent(field);


    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {
        if (args != null && args.containsKey(Constants.OK_RESUME)) {
            AppointmentsDownloadDialog downloadDialog = new AppointmentsDownloadDialog(new LWUITMainMenu());
            downloadDialog.show();
        } else if (args != null && args.containsKey(Constants.ERROR)) {
            AppUtil.get().setView(new LWUITMainMenu());
        } else {
            AppUtil.get().setView(this);
        }

    }

    public void errorOccured(String string, Exception excptn) {
        DebugLog.getInstance().log(" ERROR . " + (string != null ? string : " SOME ERROR OCCURED"));
    }

    public void actionPerformed(ActionEvent ae) {
        Object src = ae.getSource();
        if (src == list) {
            AppointmentWrapper apwr = (AppointmentWrapper) list.getSelectedItem();
            AppUtil.get().putItem(Constants.APWR, apwr);
            AppList myList = new AppList(apwr);
            AppUtil.get().setView(myList);
        } else {
            Command cmd = ae.getCommand();
            if (cmd == back) {
                LWUITMainMenu mainMenu = new LWUITMainMenu();
                AppUtil.get().setView(mainMenu);
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

    public void dataReady(AppointmentWrapper[] wrapper) {
        if (wrapper != null) {
            apps = wrapper;
            initView();
            
        } else {
            ErrorAlert errorAlert = new ErrorAlert(this, " Something went wrong");
            errorAlert.show();
        }

    }

    public void emptySet() {
        /**
         * Show alert and redirect to download dialog
         */
        GenericAlert genericAlert = new GenericAlert(this, " No Appointments available . Download first ");
        genericAlert.show();
    }
}
