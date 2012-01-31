/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.communication.model;

import org.kxml2.kdom.Element;

/**
 *
 * @author mutahi
 */
public interface IElementHandler {
    
    public Object handle (Element e);

}
