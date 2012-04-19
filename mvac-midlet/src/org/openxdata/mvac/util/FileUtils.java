
package org.openxdata.mvac.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * Handles all file management tasks
 * 
 * @author githinji
 */
public class FileUtils {

    public static final char END_OF_RECORD = '=';
    public static final char NEWLINE = '\n';
    public static final String EXT_BACKUP = ".bak";
    public static final int FILE_CHUNK = 512; 

    public static String fileSeperator = "/";
    
    private FileUtils() {}
    
    public static Enumeration listDirectory(String fileURL) {
        Enumeration k = null;
        FileConnection fcon = null;
        
        try{
            
            fcon = (FileConnection)Connector.open(fileURL);
            
            k = fcon.list(); 
            
        }catch(Exception e){
            System.out.println("Error: Failed to list directory content. " + e.toString());
        }finally{ 
            try{ 
                if (fcon != null) fcon.close(); 
            }catch(IOException e){}
        }
        return k; 
    }
  
     
    /**
     *
     * 
     * 
     * ORDER OF SEARCH FOR NEXT AVAILABLE DRIVE 
     *  fileconn.dir.photos || fileconn.dir.photos.name
     *  fileconn.dir.music || fileconn.dir.music.name
     *  fileconn.dir.tones || fileconn.dir.tones.name
     *  fileconn.dir.recordings || fileconn.dir.recordings.name
     *  fileconn.dir.graphics || fileconn.dir.graphics.name
     * 
     * 
     * @return list of dirs created with full path valid URL, in order of passing in param dirs. However, index 0 is reserved for the drive letter allocated. 
     * @throws Exception 
     */
    public static String initRoot() throws Exception {
        
        String url = null;
        FileConnection fcon = null;
        try{
            fileSeperator = System.getProperty("file.separator");
            if( Utils.isEmpty(fileSeperator) ){ fileSeperator = "/"; }
            fileSeperator = "/";
            

            //CONFIRM AVAILABLE DRIVE FIRST
            url = "file:///e:/";
            if( !url.endsWith( fileSeperator ) ){
                url += fileSeperator;
            }

            //System.out.println("IN URL: "+url);
            try{
                fcon = (FileConnection)Connector.open( url+"tmp" , Connector.READ_WRITE);
                if( !fcon.exists() || !fcon.isDirectory() ){
                    fcon.mkdir();
                } 
                fcon.delete();
                fcon.close(); 
//                err = "initRoot.set in "+url ;
            }catch(Exception e){
                url = System.getProperty("fileconn.dir.photos"); 
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.photos");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.music");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.tones");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.recordings");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.graphics");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.videos");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.themes");
                if( Utils.isEmpty(url ))  url = System.getProperty("fileconn.dir.private"); //private work directory of the midlet as a last resort
                
                if( !url.startsWith("file:///") ){
                    //System.out.println("+++ URL = "+url );
                    int i =  url.indexOf("c:");
                    if( i < 0 ) i =  url.indexOf("C:");
                    url = "file:///"+url.substring(i);
                }
//                err = "initRoot.defaulting to url: "+url;
                url = url.trim();
                if( !url.endsWith( fileSeperator ) ){
                    url += fileSeperator;
                }
//                    System.out.println("FORCE URL: "+url);
            }
//            err = "initRoot.working with drive: "+url;
            
            try{ fcon.close(); }catch(Exception t){} fcon = null;
//            System.out.println("FINAL URL: "+url);

         

//            System.out.println("DIRS: "+objs.toString() );
        }catch(Exception e){
//            throw new Exception( err+" | "+e.toString() );
            throw e;
        }finally{
            try{ fcon.close(); }catch(Exception e){}
        }
        return url;
    }

    /**
     * 
     * @param url
     * @return 
     */
    public static String validateDirURL(String url ){
        return url.endsWith( fileSeperator ) ? url  : (url + fileSeperator);
    }

    /**
     * 
     * @param url
     * @return 
     */
    public static boolean fileExistsAndNotEmpty(String url) {
//        if( logger != null ) logger.log("Entered: FileUtils.fileExistsAndNotEmpty()");
        
        boolean ok = false;
        FileConnection fcon = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( fcon.exists() ){
                if( fcon.isDirectory() ) {
                    ok = fcon.directorySize( false ) > 0;
                } 
                else 
                    ok = fcon.fileSize() > 0;
            }
        }catch(IOException e){
            System.out.println("Error: Failed to check if file exists. " + e.toString());
        }finally{ 
            try{ 
                if (fcon != null) fcon.close(); 
            }catch(IOException e){}
        }
        
        return ok;
    }

    /**
     * 
     * @param url
     * @return 
     */
    public static boolean directoryExists(String url ){
//        if(logger != null ) logger.log("Check DIR exists. " + url );
        boolean ok = false;
        FileConnection fcon = null;
        FileConnection fcon2 = null;
        OutputStream outs = null;
        
        try{
            url = validateDirURL(url);
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            
            url += "tmp"; 
            fcon2 = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            
            //be sure 
            fcon2.openDataOutputStream(); 
            
            if( fcon.exists() && fcon.isDirectory() ) ok = true; 
            
        }catch(Exception e){
        }catch(Error e){ 
        }finally{ 
            try{  if (fcon != null) fcon.close();  }catch(Exception e){} fcon = null; 
            try{ if (fcon2 != null) fcon.close();  }catch(Exception e){} fcon2 = null; 
            try{ if (outs != null) outs.close();  }catch(Exception e){} outs = null; 
            
//            if( logger != null ) logger.log("DIR exists = " + ok );
        }
        
        return ok;
    }
    /**
     * 
     * @param url
     * @throws IOException 
     */
    public static void createDir(String url) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.createDir()");
        
        FileConnection fcon = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( !fcon.exists() ){
                fcon.mkdir();
            }
            else if( !fcon.isDirectory() ){//moi: MAKE SENSE OF IT
                fcon.mkdir(); 
            }
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (fcon != null) fcon.close();  }catch(IOException e){} fcon = null; 
        }
    }

    /**
     * 
     * @param url
     * @param overwrite
     * @throws IOException 
     */
    public static void createFile(String url, boolean overwrite) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.createFile()");
        
        FileConnection fcon = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( !fcon.exists() ){
                fcon.create();
            }
            else {
                if( fcon.isDirectory() ) //moi: MAKE SENSE OF IT
                    fcon.create();
                else
                    if(overwrite) fcon.truncate(0);
            }
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (fcon != null) fcon.close();  }catch(IOException e){} fcon = null; 
        }
    }

    /**
     * 
     * @param url
     * @throws IOException 
     */
    public static void deleteDir(String url) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.deleteDir()");
        
        FileConnection fcon = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( fcon.exists() ) fcon.delete();
        }catch(IOException e){
            throw e;
        }finally{
            try{  if (fcon != null) fcon.close();  }catch(IOException e){} fcon =null; 
        }
    }
   
    /**
     * 
     * @param url
     * @throws IOException 
     */
    public static void deleteFile(String url ) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.deleteFile()");
        
        FileConnection fcon = null;
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( fcon.exists() ) fcon.delete();
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (fcon != null) fcon.close();  }catch(IOException e){} fcon = null; 

        }

    }
    
    /**
     * 
     * @param fcon
     * @throws IOException 
     */
    public static void backupFile(FileConnection fcon ) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.backupFile()");
        
        FileConnection tmp = null;
        OutputStream tmpout = null;
        InputStream fin = null;
        byte[] bb = null;
        
        try{
            String url = fcon.getURL() + EXT_BACKUP; 
            
            tmp = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( !tmp.exists() ) 
                tmp.create();
            else 
                tmp.truncate(0);
            
            fin = fcon.openInputStream();
            tmpout = tmp.openOutputStream();
            
            bb = new byte[ FILE_CHUNK ];
            int blen = -1;
            while( (blen = fin.read(bb)) != -1 ){
                tmpout.write(bb, 0, blen);
                tmpout.flush(); 
            }
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (fin != null) fin.close(); } catch(IOException e){} fin = null; 
            try{ if (tmpout != null) tmpout.close(); } catch(IOException e){} tmpout = null; 
            try{ if (tmp != null) tmp.close(); } catch(IOException e){} tmp = null; 
        }
    }

    /**
     * 
     * @param fcon
     * @param seek
     * @param length
     * @return
     * @throws IOException 
     */
    public static byte[] read(FileConnection fcon, long seek, int length) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.read()");
        
        if( seek < 0 || length < 0 ) return null;
        
        InputStream ins = null;
        byte[] bb = null;
        
        try{
            ins = fcon.openInputStream();
            bb = new byte[ length ];
            ins.skip(seek);
            ins.read(bb);
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (ins != null) ins.close(); } catch(IOException t){} ins = null; 
        }
        
        return bb;
    }

    /**
     * 
     * @param url
     * @return
     * @throws IOException 
     */
    public static byte[] readAll(String url) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.readAll()");
        
        FileConnection fcon = null;
        InputStream ins = null;
        ByteArrayOutputStream baos = null;
        byte[] bb = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( !fcon.exists() ) {
                fcon.create();
                fcon.close();
                return null; 
            }
            ins = fcon.openInputStream();
            baos = new ByteArrayOutputStream();

            bb = new byte[ FILE_CHUNK ];
            int blen = -1;
            while( (blen = ins.read(bb)) != -1 ){
                baos.write(bb, 0, blen);
                baos.flush();
            }
            
        }catch(IOException e){
            throw e;
        }finally{ 
            try{ if (ins != null) ins.close(); } catch(IOException t){} ins = null; 
            try{ if (fcon != null) fcon.close(); } catch(IOException t){} fcon  = null; 
        }
        
        return baos != null ? baos.toByteArray() : null;
    }

    /**
     * 
     * @param fcon
     * @param bytes
     * @throws IOException 
     */
    public static void writeAppend(FileConnection fcon, byte[] bytes) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.writeAppend()");
        
        OutputStream outs = null;
        
        try{
            outs = fcon.openOutputStream( fcon.fileSize() );
            outs.write(bytes);
            outs.flush(); 
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (outs != null) outs.close(); }catch(IOException t){} outs = null; 
        }
    }

    /**
     * 
     * @param url
     * @param bytes
     * @throws IOException 
     */
    public static void writeOverwrite(String url, byte[] bytes) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.writeOverwrite()");
        
        FileConnection fcon = null;
        OutputStream outs = null;
        
        try{
            fcon = (FileConnection)Connector.open(url, Connector.READ_WRITE );
            if( !fcon.exists() ){
                fcon.create();
            }
            else{
                if( fcon.isDirectory() ){ //moi: WHAT SENSE HERE ???? 
                    fcon.create();
                }else{
                    fcon.truncate(0);
                }
            }
            
            outs = fcon.openOutputStream();
            outs.write(bytes);
            outs.flush();
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (outs != null) outs.close(); }catch(IOException t){} outs = null; 
            try{ if (fcon != null) fcon.close(); }catch(IOException t){} fcon = null; 
        }
    }

    /**
     * purely sequential on a by EOR basis
     *
     * @param ins 
     * @param EOF 
     * @return
     * @throws IOException
     */
    public static byte[] readNewLine(InputStream ins, int EOF ) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.readNewLine()");
        
        ByteArrayOutputStream baos = null;
        
        try{
            baos = new ByteArrayOutputStream();
            int cha = -1;
            while( (cha = ins.read()) != -1 ){
                if( cha == EOF ){
                    break;
                }
                baos.write(cha);
            }
            baos.flush();
        }catch(IOException e){
            throw e;
        }finally{
        }
        return baos != null ? baos.toByteArray() : null;
    }

    /**
     *
     * @param fcon
     * @param b
     * @param EOF 
     * @throws Exception
     */
    public static void writeAppendNewLine(FileConnection fcon, byte[] bytes, int EOF) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.writeAppendNewLine()");
        
        OutputStream outs = null;
        
        try{
            outs = fcon.openOutputStream( fcon.fileSize() );
            outs.write(bytes);
            outs.flush();
            outs.write(EOF);
            outs.flush();
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (outs != null) outs.close(); }catch(Exception t){} outs = null; 
        }
    }

    /**
     *
     * @param fcon
     * @param b
     * @param seek
     * @param oldbytes
     * @param delete 
     * @throws Exception
     */
    public static void writeUpdateRec(FileConnection fcon, byte[] bytes, long seek, int oldbytes, boolean delete) throws IOException {
//        if( logger != null ) logger.log("Entered: FileUtils.writeUpdateRec()");
        
        if( !delete && bytes == null ) throw new IOException( "Bytes to write = NULL. Nothing to do");
        
        FileConnection ftemp = null;
        InputStream ins = null;
        OutputStream outs = null;
        byte[] bb = null;
        
        try{
            //MOVE DATA AFTER BYTES TO TEMP
            ftemp = (FileConnection)Connector.open( fcon.getURL()+EXT_BACKUP, Connector.READ_WRITE );
            if( !ftemp.exists() ) ftemp.create();
            else ftemp.truncate(0);

            long after = (seek + oldbytes /*+ 1EOR*/); 
            ins = fcon.openInputStream();
            ins.skip( after );
            
            outs = ftemp.openOutputStream();

            bb = new byte[ FILE_CHUNK ];
            int blen = -1;
            while( (blen = ins.read(bb)) != -1 ){
                outs.write(bb, 0, blen);
                outs.flush();
                bb = new byte[ FILE_CHUNK ]; 
            }
            bb = null;
            outs.close();
            ins.close();
        
            fcon.truncate(seek);

            //WRITE NEW DATA
            if( !delete ){
                outs = fcon.openOutputStream( fcon.fileSize() );
                outs.write(bytes);
                outs.flush();
                outs.close(); 
            }

            //WRITE BACK TEMP DATA
            ins = ftemp.openInputStream();

            outs = fcon.openOutputStream( fcon.fileSize() );

            bb = new byte[ FILE_CHUNK ];
            blen = -1;
            while( (blen = ins.read(bb)) != -1 ){
                outs.write(bb, 0, blen);
                outs.flush();
            }            
        }catch(IOException e){
            throw e;
        }finally{
            try{ if (outs != null) outs.close(); }catch(Exception t){} outs = null; 
            try{ if (ftemp != null) ftemp.close(); }catch(Exception t){} ftemp = null; 
            try{ if (ins != null) ins.close(); }catch(Exception t){} ins = null; 
        }
    }
}
