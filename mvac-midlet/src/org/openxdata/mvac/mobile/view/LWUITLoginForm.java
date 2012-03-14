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
import com.sun.lwuit.plaf.Border;
import java.util.Hashtable;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.openxdata.mvac.communication.ITransportListener;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.mobile.MvacMidlet;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.view.api.IDialogListener;
import org.openxdata.mvac.mobile.util.view.api.IView;
import org.openxdata.mvac.mobile.view.user.MVACUserManager;


/**
 *
 * @author soyfactor
 */
public class LWUITLoginForm extends Form implements IView,IDialogListener,ActionListener,ITransportListener{
    MVACUserManager usermanager = new MVACUserManager();

    private final String usernameLbl = "Username";
    private final String passwordLbl = "Password";

    private Container container;

    private Label lblUsername;
    private Label lblPassword;

    private TextField txtUsername;
    private TextField txtPassword;
    private TransportManager tm;
    private FBProgressIndicator progress;



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
        //Button butt = new Button(null)
       tm=new TransportManager("GET", this);
        setLayout(new BorderLayout());


        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));


        lblUsername = new Label(usernameLbl);
        lblPassword = new Label(passwordLbl);

        txtUsername = new TextField(20);

        txtUsername.setConstraint(TextField.ANY);
        txtUsername.setInputMode(Constants.INPUT_LOWERCASE);
        

        txtPassword = new TextField();
        txtPassword.setConstraint(TextField.PASSWORD);
        txtPassword.setInputMode(Constants.INPUT_LOWERCASE);
        txtPassword.setText("doriana");
        txtUsername.setText("doriana");

        container.addComponent(lblUsername);
        container.addComponent(txtUsername);

        container.addComponent(lblPassword);
        container.addComponent(txtPassword);

        

        addComponent(BorderLayout.CENTER , container);

        
        
        addCommand(new Command("Login") {
            public void actionPerformed(ActionEvent evt) {
                //authenticate user
                //display main menu
//                AppUtil.get().putItem(Constants.USERNAME,"admin");
//                AppUtil.get().putItem(Constants.PASSWROD,"admin");
//                LWUITMainMenu mainMenu = new LWUITMainMenu();
//                AppUtil.get().setView(mainMenu);
                if(correctdetails()){
                    //messageSent("200");
                    progress = new FBProgressIndicator(this, "Authenticating... Please wait");
                    progress.showModeless();
                    Message msg = new Message(Constants.AUTH_URL) ;
                    msg.setParam("uname", getUsername());
                    msg.setParam("pwd", getPassword());
                    try{
                       tm.sendMessage(msg);
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
                    }catch(Exception e){
                        GenericAlert genericAlert = new GenericAlert(LWUITLoginForm.this, "Error occured. Try Again");
                        genericAlert.show();
                    }
                    
                }else{
                    
                }
                
                
                
                

                // Put code here

            }

            private boolean correctdetails() {
                String username=getUsername();
                String pwd = getPassword();
                if(username!=null&&username!=""&&pwd!=null&&pwd!=""){
                    
                    return true;
                }else{
                    return false;
                }
            }
        });

        addCommand(new Command("Exit"){
            public void actionPerformed(ActionEvent ae){
                
                    ((MIDlet) AppUtil.get().getItem(Constants.MIDLET)).notifyDestroyed();
                
            }
        });
        

    }

    public void onDialogClose(Object obj) {
        //process the response from the Dialog
        //show the next Item
        AppUtil.get().setView(this);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getCommand() == progress.cancel) {
            //transportlayer.
            this.resume(null);

        }

        
    }

    public String getPassword(){
        return txtPassword.getText();
    }

    public String getUsername(){
        return txtUsername.getText();
    }

    public void dialogReturned(Dialog dialog, boolean yesNo) {
    }

    public void messageSent(Object args) {
        System.out.println(" Response form server :" + args.toString());
        tm.closeConnections();
        String response = (String)args;
        if (response.equals("200")) {
            //success. Save user ans password. Proceed
            LWUITMainMenu mainMenu = new LWUITMainMenu();
            AppUtil.get().putItem(Constants.USERNAME, getUsername());
            AppUtil.get().putItem(Constants.PASSWROD, getPassword());
            AppUtil.get().putItem(Constants.NURSENAME, getUsername());
//            System.out.println("Nurse Set to :"+(String)AppUtil.get().getItem(Constants.NURSENAME));
            progress.dispose();
                    
            AppUtil.get().setView(mainMenu);
        }else if(response.equals("201")){
            //failed. alert user that login failed. return to login screen
//            txtUsername.setText("");
            txtPassword.setText("");
            this.resume(null);
            ErrorAlert errorAlert = new ErrorAlert(LWUITLoginForm.this, "Enter valid username and password");
            errorAlert.show();
        }else{
            //someting wrong!
        }
    }

    public void requestFailed(Object error) {
        if(error!=null){
            /**
             * TODO : Display error alert
             */

            if(error instanceof ConnectionNotFoundException){
                System.out.println("Error . Request failed :" + ((ConnectionNotFoundException)error).getMessage());
                /**
                 * TODO : Change the error message 
                 */
                ErrorAlert errorA = new ErrorAlert(this, "Failed to connect to server");
                errorA.show();
            }else {
                ErrorAlert errorA = new ErrorAlert(this, "Failed to connect to server");
                errorA.show();
            }
        }
    }


    

}
