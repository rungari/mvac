/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.mobile.view;

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
import java.util.Hashtable;
import javax.microedition.midlet.MIDlet;
import org.openxdata.mvac.mobile.api.ControllerListener;
import org.openxdata.mvac.mobile.api.MVACController_new;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IDialogListener;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.user.MVACUserManager;
import org.openxdata.mvac.mobile.view.widgets.DetailsTextField;

/**
 *
 * @author soyfactor
 */
public class LWUITLoginForm extends Form implements IView, IDialogListener, ActionListener, ControllerListener {

    MVACUserManager usermanager = new MVACUserManager();
    private final String usernameLbl = "Username";
    private final String passwordLbl = "Password";
    private Container container;
    private Label lblUsername;
    private Label lblPassword;
    private DetailsTextField txtUsername;
    private DetailsTextField txtPassword;
    private FBProgressIndicator progress;
    private Command cmdExit;
    private Command cmdLogin;

    public LWUITLoginForm(String title) {
        super(title);
        super.getTitleComponent().setAlignment(LEFT);
        initView();
    }

    public Object getScreenObject() {
        return this;
    }

    public void resume(Hashtable args) {

        AppUtil.get().setView(this);
    }

    private void initView() {
        setLayout(new BorderLayout());
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        lblUsername = new Label(usernameLbl);
        lblPassword = new Label(passwordLbl);

        txtUsername = new DetailsTextField(20);

        txtUsername.setConstraint(TextField.ANY);
        txtUsername.setInputMode(Constants.INPUT_LOWERCASE);


        txtPassword = new DetailsTextField();
        txtPassword.setConstraint(TextField.PASSWORD);
        txtPassword.setInputMode(Constants.INPUT_LOWERCASE);
        txtPassword.setText("doriana");
        txtUsername.setText("doriana");

        container.addComponent(lblUsername);
        container.addComponent(txtUsername);

        container.addComponent(lblPassword);
        container.addComponent(txtPassword);
        
        addComponent(BorderLayout.CENTER, container);

        cmdLogin = new Command("Login");
        cmdExit = new Command("Exit");

        addCommand(cmdLogin);
        addCommand(cmdExit);

        addCommandListener(this);

    }

    public void onDialogClose(Object obj) {
        AppUtil.get().setView(this);
    }

    public String getPassword() {
        return txtPassword.getText();
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }



    public void requestComplete(Object response) {
        LWUITMainMenu mainMenu = new LWUITMainMenu();
        AppUtil.get().putItem(Constants.USERNAME, getUsername());
        AppUtil.get().putItem(Constants.PASSWROD, getPassword());
        AppUtil.get().putItem(Constants.NURSENAME, getUsername());
//            System.out.println("Nurse Set to :"+(String)AppUtil.get().getItem(Constants.NURSENAME));
        progress.dispose();

        AppUtil.get().setView(mainMenu);
    }

    public void error(Object error) {
        txtPassword.setText("");
        this.resume(null);
        if (error != null) {
            ErrorAlert errorAlert = new ErrorAlert(this, error.toString());
            errorAlert.show();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cmdLogin) {
            if (correctdetails()) {
                progress = new FBProgressIndicator(this, "Authenticating... Please wait");
                progress.showModeless();
                try {
                    MVACController_new.getInstance().userLogin(getUsername(), getPassword(), this);
                    /**
                     * TEST WHEN SERVER IS DOWN
                     */
//                        AppUtil.get().putItem(Constants.USERNAME, getUsername());
//                        AppUtil.get().putItem(Constants.PASSWROD, getPassword());
//                        AppUtil.get().putItem(Constants.NURSENAME, getUsername());
//                        AppUtil.get().setView(new LWUITMainMenu());
                    /**
                     * END OF TEST BLOCK
                     */
                } catch (Exception e) {
                    GenericAlert genericAlert = new GenericAlert(LWUITLoginForm.this, "Error occured. Try Again");
                    genericAlert.show();
                    e.printStackTrace();
                }

            } else {
            }
        } else if (ae.getSource() == cmdExit) {
            ((MIDlet) AppUtil.get().getItem(Constants.MIDLET)).notifyDestroyed();
        } else if (ae.getSource() == progress.cancel) {
            this.resume(null);
        }

    }

    private boolean correctdetails() {
        String username = getUsername();
        String pwd = getPassword();
        if (username != null && (!username.equals("")) && pwd != null && (!pwd.equals(""))) {
            return true;
        } else {
            return false;
        }
    }

    public void showProgress(boolean visible, Object msg) {
    }
}
