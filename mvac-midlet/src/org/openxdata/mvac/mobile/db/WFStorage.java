package org.openxdata.mvac.mobile.db;


import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.openxdata.db.OpenXdataDataStorage;
import org.openxdata.db.util.Storage;
import org.openxdata.db.util.StorageFactory;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.FormData;
import org.openxdata.model.FormDef;
import org.openxdata.model.StudyDef;
import org.openxdata.model.StudyDefList;
import org.openxdata.model.UserListStudyDefList;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.mobile.util.WFDateSorter;
import org.openxdata.mvac.mobile.model.LotNumbers;
import org.openxdata.mvac.util.DebugLog;
import org.openxdata.workflow.mobile.model.MWorkItem;
import org.openxdata.workflow.mobile.model.MWorkItemDataList;
import org.openxdata.workflow.mobile.model.MWorkItemList;

public class WFStorage {
    public final static String WORK_ITEM_STORAGE = "workitem.storage";
    public final static String LOT_NUMBER_STORAGE = "lotnumber.storage";

    public static String getWIRStorageName() {
        return WORK_ITEM_STORAGE + "." + (String)AppUtil.get().getItem(Constants.USERNAME);
    }

    private static String getLotStorageName() {
        return LOT_NUMBER_STORAGE + "." + (String)AppUtil.get().getItem(Constants.USERNAME);
    }

     public static Vector getWIRSummay(StorageListener listener){
	return WIRSummary.buildFromStorage(listener);
     }
     
     public static MWorkItem getWorkItem(int recordId, StorageListener listener){
	Storage wirStorage = getWirStorage(listener);
	MWorkItem workitem = (MWorkItem) wirStorage.read(recordId, new MWorkItem().getClass());
	return  workitem;
     }

     public static int getWorkItemStorageSize(StorageListener storageListener){
         return getWirStorage(storageListener).getNumRecords();
     }
     


    public static Storage getWirStorage(StorageListener listener) {
        Storage storage = StorageFactory.getStorage(getWIRStorageName(),
                listener);
        return storage;
    }

    private static Storage getLotStorage(StorageListener listener) {
        Storage storage = StorageFactory.getStorage(getLotStorageName(), listener);
        return storage;
    }

    

    public static void saveMWorkItemList(MWorkItemList itemList,
                                         StorageListener listener) {
        Storage storage = getWirStorage(listener);
        Vector workItemList = getMWorkItemList(listener);//TODO This might cause out of memory issues
        //Read try not to reload already load workItems
        Vector workItems = itemList.getmWorkItems();
        int size = workItems.size();
        for (int i = 0; i < size; i++) {
            MWorkItem wir = (MWorkItem) workItems.elementAt(i);
            if (workItemList == null || !workItemList.contains(wir))
                storage.save(wir);
        }
    }

    public static void saveMWorkItem(MWorkItem wir, StorageListener listener) {
        Storage storage = getWirStorage(listener);
        storage.save(wir);
    }

    public static Vector getMWorkItemList(StorageListener listener) {
        Storage storage = getWirStorage(listener);
        Vector vector = storage.read(new MWorkItem().getClass());
        return vector;
    }

    public static void saveUserListStudyDefList(UserListStudyDefList userStudyList) {

        OpenXdataDataStorage.saveUsers(userStudyList.getUsers());

        StudyDefList studyDefList = userStudyList.getStudyDefList();
        
        userStudyList = null;
        OpenXdataDataStorage.saveStudyList(studyDefList);

        Vector studies = studyDefList.getStudies();
        System.out.println(" Number of user studies :" + studies.size());
        studyDefList = null;
        for (int i = 0; i < studies.size(); i++) {
            StudyDef studyDef = (StudyDef) studies.elementAt(i);
            OpenXdataDataStorage.saveStudy(studyDef);
            studyDef = null;
        }
        studies.removeAllElements();
        studies = null;

//        return userStudyList.totalForms();
    }

    public static synchronized RecordEnumeration getWorkItemEnum() {
        RecordEnumeration re = null;
        RecordStore rs = null;
        try {
             rs = RecordStore.openRecordStore(WFStorage.getWIRStorageName(), true);
            if(rs != null){
                re = rs.enumerateRecords(null, new WFDateSorter(), false);
            }
        } catch (RecordStoreException ex) {
            System.out.println(" ERROR : Exception thrown when loading store ." + ex.getMessage());
        }finally{
            if(rs != null ) try {
                rs.closeRecordStore();
            } catch (RecordStoreNotOpenException ex) {
                DebugLog.getInstance().log(" @getWorkItemEnum . Exception thrown . " + ex.getMessage());
            } catch (RecordStoreException ex) {
                DebugLog.getInstance().log(" @getWorkItemEnum . Exception thrown . " + ex.getMessage());
            }
        }
        return re ;
    }

    /**
     * Maintain only one record in store
     * Clear store each time before adding record
     * @param lots
     * @param listener
     */
    public static void saveLotNumbers(LotNumbers lots, StorageListener listener) {
        Storage storage = getLotStorage(listener);
        if(storage.getNumRecords() > 0){
            storage.delete();
        }
        if(storage.save(lots)){
            System.out.println("Successfully saved ");
        }

    }

    public static FormDef getFormDefForWorkItem(MWorkItem wir) {
        StudyDef study = OpenXdataDataStorage.getStudy(wir.getStudyId());
        if (study == null)
            return null;
        return study.getForm(wir.getFormId());
    }

    public static FormData getOrCreateFormData(MWorkItem wir, FormDef def) {
        FormData formData = getFormData(wir, false);
        if (formData == null)
            formData = creatAndSaveFormData(wir.getStudyId(), def);
        formData.setDef(def);
        return formData;
    }

    /**
     * Returns the formdata for this workItem or null if no formdata is
     * associated to this workitem
     *
     * @param wir
     *            Workitem Record
     * @return Formdata associated to the passed workitem
     */
    public static FormData getFormData(MWorkItem wir, boolean addFormDef) {
        if (wir.getDataRecId() == -1)
            return null;
        FormData formData = OpenXdataDataStorage.getFormData(wir.getStudyId(),
                wir.getFormId(), wir.getDataRecId());

        if (addFormDef && formData != null)
            formData.setDef(getFormDefForWorkItem(wir));
        return formData;
    }

    private static FormData creatAndSaveFormData(int studyId, FormDef formDef) {
        FormData formData = new FormData(formDef);
        boolean saveFormData = OpenXdataDataStorage.saveFormData(studyId,
                formData);
        if (saveFormData) {
            formData.setDef(formDef);
            return formData;
        } else
            throw new RuntimeException("Unable to save form data");

    }

    public static Vector getMWorkItemListWithData(StorageListener listener) {
        Vector resp = new Vector() ;
        for(Enumeration e = getCompleteMWorkItemList(listener).elements() ; e.hasMoreElements() ;){
            int index = Integer.parseInt(e.nextElement().toString());
            if(index > -1){
                resp.addElement(getWorkItem(index, listener));
            }
        }
        return resp ;
    }

    public static Vector getCompleteMWorkItemList(StorageListener listener){
        Vector resp = new Vector();
        try {
            RecordStore rs = RecordStore.openRecordStore(WFStorage.getWIRStorageName(), true);
            if(rs != null){
                RecordEnumeration re = rs.enumerateRecords(null, null, false);
                for(; re.hasNextElement();){
                    int index = re.nextRecordId() ;
                    MWorkItem mwi = getWorkItem(index, listener);
                    if(completed(mwi)){
                        resp.addElement(Integer.toString(index));
                    }
                }
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
        return resp ;
    }


    public static void deleteCompleteWorkItems(StorageListener listener,
                                               boolean inclueEmpty) {

        Vector workItemList = getMWorkItemList(listener);
        int size = workItemList.size();
        for (int i = 0; i < size; i++) {
            MWorkItem wir = (MWorkItem) workItemList.elementAt(i);
            if (inclueEmpty)
                deleteWorkItem(wir, listener);
            else if (completed(wir))
                deleteWorkItem(wir, listener);
        }
    }

    public static boolean completed(MWorkItem wir) {
        if (wir.getDataRecId() == -1)
            return false;
        return getFormData(wir, true).isRequiredAnswered();
    }

    public static void deleteWorkItem(MWorkItem wir, StorageListener listener) {
        if (wir.getDataRecId() != -1)
            OpenXdataDataStorage.deleteFormData(wir.getStudyId(), getFormData(
                    wir, false));
        Storage storage = getWirStorage(listener);
        storage.delete(wir);
    }

    public static MWorkItemDataList toMWIRDatallist(MWorkItem mwir){
            MWorkItemDataList mwirDatalist = new MWorkItemDataList();

            return mwirDatalist;
    }

    public static Vector getSomeMWorkItemList(StorageListener listener) {

        Storage storage = getWirStorage(listener);
       Vector  vector  = storage.readSome(new MWorkItem().getClass());
        return vector;
    }

    public static LotNumbers getLotNumbers(StorageListener listener) {
        LotNumbers  lots= null;
        Storage storage = getLotStorage(listener);
        int numRecs= storage.getNumRecords();
        if (numRecs>0) {
            Vector resp = storage.read(LotNumbers.class);
            for(Enumeration e = resp.elements() ; e.hasMoreElements();){
                lots = (LotNumbers)e.nextElement();
            }
        }
        return lots;
    }


    public static void deleteAllWorkItems(StorageListener listener, boolean inclueEmpty) {
        Vector workItemList = getMWorkItemList(listener);
        if(workItemList!=null){
            
                int size = workItemList.size();
                for (int i = 0; i < size; i++) {
                    MWorkItem wir = (MWorkItem) workItemList.elementAt(i);
                    //deleteWorkItem(wir, listener);
                    deleteFormDataForWir(wir, listener);
                }
                DebugLog.getInstance().log(" @deleteworkItems . About to delete wir store . Size before : " + getWirStorage(listener).getNumRecords());
               Storage storage = getWirStorage(listener);
               storage.close();
               storage.deleteStore();

               DebugLog.getInstance().log(" @deleteworkitems . After deleting store . size of store is :" + getWirStorage(listener).getNumRecords());

                
            
        }

    }

    public static void deleteFormDataForWir(MWorkItem wir, StorageListener listener) {
        if (wir.getDataRecId() != -1) {
            OpenXdataDataStorage.deleteFormData(wir.getStudyId(), getFormData(wir, false));
        }


    }



}
