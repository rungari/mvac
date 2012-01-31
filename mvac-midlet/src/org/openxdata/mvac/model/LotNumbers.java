/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.openxdata.db.util.PersistentHelper;
import org.openxdata.db.util.Record;

/**
 *
 * @author gmimano
 */
public class LotNumbers implements Record{
    private String lotNumbers;
    private Integer recordId= new Integer(-1);
    boolean isnew;

    public void setRecordId(int i) {
        this.recordId=new Integer(i);
    }

    public int getRecordId() {
        return recordId.intValue();
    }

    public boolean isNew() {
        return isnew;
    }

    public void write(DataOutputStream stream) throws IOException {
        PersistentHelper.writeUTF(stream, lotNumbers);
        PersistentHelper.writeInteger(stream, recordId);
    }

    public void read(DataInputStream stream) throws IOException, InstantiationException, IllegalAccessException {
        this.lotNumbers=PersistentHelper.readUTF(stream);
        this.recordId = PersistentHelper.readInteger(stream);
    }

    public void setLotNumbers(String lotNumbers) {
        this.lotNumbers = lotNumbers;
    }

    public String getLotNumbers() {
        return lotNumbers;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }
    
    
    
    
    
    
    

    

    
    
    
    
    
    
}
