/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.mobile.view.renderers;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;


/**
 *
 * @author mutahi
 */
public abstract class DefaultMVACListCellRenderer extends Container implements ListCellRenderer{

    protected int selectedFgColor ;

    public DefaultMVACListCellRenderer() {
        super(new BorderLayout());
        getStyle().setBgColor(0xe1e1e1, true);
        selectedFgColor = 0x000000 ;
        setCellRenderer(true);
    }

    public abstract Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected);

    public Component getListFocusComponent(List list) {
        Label focus = new Label();
//        focus.getStyle().setBgColor(0x9fd056,true);
//        focus.getStyle().setBgTransparency(100);
        return focus ;
    }

    public void addComponent(Object constraints, Component cmp) {
        super.addComponent(constraints, cmp);
    }
}
