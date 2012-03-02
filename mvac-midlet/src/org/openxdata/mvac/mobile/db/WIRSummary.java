package org.openxdata.mvac.mobile.db;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.openxdata.db.util.AbstractRecord;
import org.openxdata.db.util.Serializer;
import org.openxdata.db.util.StorageListener;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 */
public class WIRSummary extends AbstractRecord {

    private String name;
    private String caseId;
    private int wirRecId;

    public WIRSummary() {
    }

    public WIRSummary(String name, String caseId, int wirRecId) {
	this.name = name;
	this.caseId = caseId;
	this.wirRecId = wirRecId;
    }

    public void write(DataOutputStream stream) throws IOException {
	stream.writeUTF(name);
	stream.writeUTF(caseId);
	stream.writeInt(wirRecId);
    }

    public void read(DataInputStream stream) throws IOException, InstantiationException, IllegalAccessException {
	name = stream.readUTF();
	caseId = stream.readUTF();
	wirRecId = stream.readInt();
    }

    public String getCaseId() {
	return caseId;
    }

    public void setCaseId(String caseId) {
	this.caseId = caseId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getWirRecId() {
	return wirRecId;
    }

    public void setWirRecId(int wirRecId) {
	this.wirRecId = wirRecId;
    }

    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final WIRSummary other = (WIRSummary) obj;
	if ((this.caseId == null) ? (other.caseId != null) : !this.caseId.equals(other.caseId))
	    return false;
	return true;
    }

    public int hashCode() {
	int hash = 7;
	hash = 37 * hash + (this.caseId != null ? this.caseId.hashCode() : 0);
	return hash;
    }

    public static Vector buildFromStorage(StorageListener listener) {
        System.out.println(" @ WIR Summary . buildFromStorage ");
	Vector list = new Vector();
	RecordStore recStorage = null;
	try {
	    recStorage = RecordStore.openRecordStore(WFStorage.getWIRStorageName(), true);
	   int numRecords = recStorage.getNumRecords();
	    if (numRecords > 0) {
		RecordEnumeration recEnum = recStorage.enumerateRecords(null, null, true);
		while (recEnum.hasNextElement()) {
		    int id = recEnum.nextRecordId();
		    MWorkItem wir = (MWorkItem) Serializer.deserialize(recStorage.getRecord(id), new MWorkItem().getClass());
		    WIRSummary wIRSummary = new WIRSummary(wir.getDisplayName(), wir.getCaseId(), id);
		    if(wir.getDataRecId() != -1) 
			wIRSummary.setName("*"+wIRSummary.getName());
		    
		    list.addElement(wIRSummary);
		}
	    }
	} catch (Exception ex) {
	    throw new RuntimeException("Error occure while loading the workitem from Record Store");
	} finally {
	    if (recStorage != null) {
		try {
		    recStorage.closeRecordStore();
		} catch (RecordStoreException ex) {
		}
	    }
	}
	return list;
    }
}
