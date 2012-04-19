/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.api;

/**
 *
 * @author mutahi
 */
public interface ControllerListener {

    public void requestComplete(Object response) ;

    public void error(Object error) ;

    public void showProgress(boolean visible , Object msg) ;

}
