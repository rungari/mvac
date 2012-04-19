/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.mvac.util;

/**
 *
 * @author mutahi
 */
public interface ILogger {
    /**
     * Appends the supplied string to the log file on disk
     *
     * @param str the string to be appended to the log
     */
    public void log(String str);

    /**
     * Reads the log from disk and returns the text
     *
     * @return the log as string
     */
    public String fetchLogs();

    /**
     * Delete log files from disk
     */
    public void clearLogs();
}