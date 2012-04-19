/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view.widgets;

import com.sun.lwuit.Command;
import com.sun.lwuit.TextField;

/**
 *
 * @author mutahi
 */
public class DetailsTextField extends TextField{

    public DetailsTextField() {
        super();
    }

    public DetailsTextField(String text){
        super(text);
    }


    public DetailsTextField(int columns) {
        super(columns);
    }

    public DetailsTextField(String text , int column){
        super(text, column);
    }

   protected Command installCommands(Command clear, Command t9) {
       return super.installCommands(t9, clear);
   }




}
