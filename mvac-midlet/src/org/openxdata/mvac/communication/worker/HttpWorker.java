/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.communication.worker;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import mike.messaging.communication.worker.IWorker;
import org.openxdata.mvac.communication.TransportManager;
import org.openxdata.mvac.communication.model.Message;
import org.openxdata.mvac.util.DebugLog;

/**
 *
 * @author openmrs
 */
public class HttpWorker implements IWorker {
    TransportManager tm;
    Message msg;
    String requestType;
    
    HttpConnection httpconnection;
    DataInputStream datainputstream;
    StringBuffer stringbuffer;

    public HttpWorker(TransportManager tm, Message msg,String requestType) {
        this.tm = tm;
        this.msg = msg;
        this.requestType=requestType;
    }
    
    
    

    public void run() {
        //throw new UnsupportedOperationException("Not supported yet.");
        
        httpconnection = null;
        datainputstream = null;
        stringbuffer = new StringBuffer();
        String reqString = msg.requestString();
        
        reqString = reqString.substring(0, reqString.length()-1);
        DebugLog.getInstance().log(" @HttpWorker . Request String is : " + reqString);
        try {
            
            httpconnection = (HttpConnection) Connector.open(reqString);
            httpconnection.setRequestMethod(requestType);
        
            datainputstream = new DataInputStream(httpconnection.openInputStream());

            int i;
            while ((i = datainputstream.read()) != -1) {
                stringbuffer.append((char) i);
            }
        } catch (IOException ex) {
            DebugLog.getInstance().log("Exception thrown :" + ex.toString());
//            ex.printStackTrace();
            tm.errorOccured(ex);

        }finally{
                    try {
                if (httpconnection != null) {
                    httpconnection.close();
                }
                if (datainputstream != null) {
                    datainputstream.close();
                }
            } catch (IOException ioexception1) {
                ioexception1.printStackTrace();
            }

            
        }
        DebugLog.getInstance().log("@HttpWorker . About to send response ." + stringbuffer);
        tm.finishedSend(stringbuffer.toString());

    }

    public void setTransportManager(TransportManager tm) {
        this.tm = tm;
    }
    
    public void closeConnections(){
        
                try {
            if (httpconnection != null) {
                httpconnection.close();
            }
            if (datainputstream != null) {
                datainputstream.close();
            }
        } catch (IOException ioexception1) {
            ioexception1.printStackTrace();
        }

        
    }
    
    
    
    
}
