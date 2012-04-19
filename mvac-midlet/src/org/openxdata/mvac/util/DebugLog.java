package org.openxdata.mvac.util;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;



/**
 * Required for debugging purposes;
 * 
 * @author mutahi
 */
public class DebugLog implements ILogger {
    private static DebugLog logger = null;
    private static FileConnection file = null;
    private static String LOG_FILE_PATH = null;

    /** Set to <code>false</code> if logging should be disabled. */
    public static final boolean ENABLED = true;

    static {
        logger = new DebugLog();

        if (ENABLED) {

            try {
                
                LOG_FILE_PATH = FileUtils.initRoot() + "applog.txt";

                System.out.println(LOG_FILE_PATH);
                file = (FileConnection) Connector.open(LOG_FILE_PATH);
                if (!file.exists()) file.create();
            } catch (IOException ex) {
                System.out.println("IOE Failed to create debug log. " + ex.toString());
                ex.printStackTrace();
            } catch (Exception ex) {
                System.out.println("E Failed to create debug log. " + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    private DebugLog() {
    }

    public static DebugLog getInstance() {
        return logger;
    }

    /**
     * 
     */
    public void closeLogger() {

        if (!ENABLED) {
            return;
        }

        try {
            file.close();
        } catch (Exception t) {
        }
        file = null;
    }

    /**
     * @param str
     */
    public void log(String str) {

        if (!ENABLED) {
            return;
        }

        System.out.println(str);

        if (file == null) return;

        try {
            str += "\n";
            FileUtils.writeAppend(file, str.getBytes());
        } catch (Exception e) {
            System.out.println("Error: Failed to write debug log file");
        } 
    }

    public String fetchLogs() {

        if (!ENABLED) {
            return null;
        }

        byte[] logData = null;
        
        try {
            logData = FileUtils.readAll(LOG_FILE_PATH);
        } catch (Exception e) {
            System.out.println("Error: Failed to open log file");
        }
        
        return new String(logData);
    }

    public void clearLogs() {
    }

}
