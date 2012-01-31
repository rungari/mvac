/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.mvac.communication.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author mutahi
 */
public class HttpConnector {
    
    private String url = "";

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String sendHttpPOST(byte[] obj) throws IOException {

       // url = Globals.registerUrl;
        HttpConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;
        StringBuffer sb = new StringBuffer();

        try {
            // Open an HTTP Connection object
            httpConn = (HttpConnection) Connector.open(url);
            // Setup HTTP Request to POST
            httpConn.setRequestMethod(HttpConnection.POST);

            httpConn.setRequestProperty("User-Agent",
                    "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
            httpConn.setRequestProperty("Accept_Language", "en-US");
            //Content-Type is must to pass parameters in POST Request
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            os = httpConn.openOutputStream();

            os.write(obj);

            //os.flush();

            // Read Response from the Server


            is = httpConn.openDataInputStream();
            int chr;
            while ((chr = is.read()) != -1) {
                sb.append((char) chr);
            }

            ////System.out.println("Server Response :" + sb.toString());

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (httpConn != null) {
                httpConn.close();
            }
        }
        return sb.toString();

    }

}