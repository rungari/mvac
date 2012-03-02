package org.openxdata.mvac.mobile;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import org.openxdata.db.util.Persistent;
import org.openxdata.db.util.StorageListener;
import org.openxdata.mvac.mobile.db.* ;
import org.openxdata.workflow.mobile.model.MWorkItem;

/**
 *
 */
public class WIRDownload implements Persistent, StorageListener {

    private Vector wirSummaries = new Vector(0);

    public WIRDownload() {
    }

    public void write(DataOutputStream stream) throws IOException {
    }

    public void read(DataInputStream stream) throws IOException, InstantiationException, IllegalAccessException {
        System.out.println("+++++++++++++  About to read in workitem ");
	wirSummaries = WFStorage.getWIRSummay(this);//to do u have to clear the workitems on the main screen
        System.out.println("------------------ Workitems in storage :" + wirSummaries.size());
        System.out.println("++++++++++++++++++ Size of stream :" + stream.available());
	int readByte = stream.readInt();
	for (int i = 0; i < readByte; i++) {
	    MWorkItem wir = new MWorkItem();
	    wir.read(stream);
	    WIRSummary summary = new WIRSummary(wir.getDisplayName(),
						wir.getCaseId(), 0);
	    
	    if (!wirSummaries.contains(summary)) {//can be optimised but is it really needed
		WFStorage.saveMWorkItem(wir, this);
		summary.setWirRecId(wir.getRecordId());
		wirSummaries.addElement(summary);
	    }
	}
    }

    public void errorOccured(String errorMessage, Exception e) {
        System.out.println(" Error occured ");
    }

    public Vector getWirSummaries() {
	return wirSummaries;
    }
}
