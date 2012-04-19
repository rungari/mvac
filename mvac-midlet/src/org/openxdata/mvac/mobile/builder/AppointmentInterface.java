/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.builder;

import org.openxdata.mvac.mobile.model.AppointmentWrapper;

/**
 *
 * @author mutahi
 */
public interface AppointmentInterface {

    public void dataReady(AppointmentWrapper wrapper[]);

    public void emptySet();

}
