package org.openxdata.mvac.mobile.db;

//~--- non-JDK imports --------------------------------------------------------

import org.openxdata.db.OpenXdataDataStorage;
import org.openxdata.db.util.Storage;
import org.openxdata.db.util.StorageFactory;
import org.openxdata.db.util.StorageListener;
import org.openxdata.model.FormData;
import org.openxdata.model.FormDef;
import org.openxdata.model.StudyDef;
import org.openxdata.model.StudyDefList;
import org.openxdata.model.UserListStudyDefList;
import org.openxdata.workflow.mobile.model.MWorkItem;
import org.openxdata.workflow.mobile.model.MWorkItemList;

//~--- JDK imports ------------------------------------------------------------

import java.util.Vector;
import org.openxdata.mvac.mobile.util.AppUtil;
import org.openxdata.mvac.mobile.util.Constants;
import org.openxdata.mvac.model.LotNumbers;

public class WFStorage_old {
    public final static String WORK_ITEM_STORAGE = "workitem.storage";
    public final static String LOT_NUMBER_STORAGE = "lotnumber.storage";

    public static String getWIRStorageName() {
        return WORK_ITEM_STORAGE + "." + (String)AppUtil.get().getItem(Constants.USERNAME);
    }
    
    private static String getLotStorageName() {
        return LOT_NUMBER_STORAGE + "." + (String)AppUtil.get().getItem(Constants.USERNAME);
    }

    private static Storage getWirStorage(StorageListener listener) {
        Storage storage = StorageFactory.getStorage(getWIRStorageName(), listener);

        return storage;
    }
    
    private static Storage getLotStorage(StorageListener listener) {
        Storage storage = StorageFactory.getStorage(getLotStorageName(), listener);

        return storage;
    }

    public static void saveMWorkItemList(MWorkItemList itemList, StorageListener listener) {
        Storage storage      = getWirStorage(listener);
        Vector  workItemList = getMWorkItemList(listener);
        Vector  workItems    = itemList.getmWorkItems();
        int     size         = workItems.size();

        for (int i = 0; i < size; i++) {
            MWorkItem wir = (MWorkItem) workItems.elementAt(i);

            if ((workItemList == null) ||!workItemList.contains(wir)) {
                storage.save(wir);
            }

            wir = null;
        }

        workItems = null;
        workItemList = null;
        itemList = null;        

    }

    public static void saveMWorkItem(MWorkItem wir, StorageListener listener) {
        Storage storage = getWirStorage(listener);

        storage.save(wir);
    }
    
    public static void saveLotNumbers(LotNumbers lots, StorageListener listener) {
        Storage storage = getLotStorage(listener);

        storage.save(lots);
        
    }
    
    public static LotNumbers getLotNumbers(StorageListener listener) {
        LotNumbers  lots= null;
        Storage storage = getLotStorage(listener);
        //Vector  vector  = storage.read(new MWorkItem().getClass());
        System.out.println("Before reading lots..");
        int numRecs= storage.getNumRecords();
        
        System.out.println("Number of Records in WF =>"+numRecs);
        
        if (numRecs>0) {
            lots = (LotNumbers) storage.readFirst(LotNumbers.class);
        }
        
        
        
        System.out.println("After reading lots..");
        
        if (lots!=null) {
            //
            System.out.println("Gotten Lots=> SO not Null"+lots.getLotNumbers());
            
            
        }else{
            System.out.println("Gotten Lots=>Which are null");
            lots = new LotNumbers();
            lots.setLotNumbers("Refresh to update...,");
        }

        return lots;
    }

    public static Vector getMWorkItemList(StorageListener listener) {
        
        Storage storage = getWirStorage(listener);
        Vector  vector  = storage.read(new MWorkItem().getClass());

        return vector;
    }
    
    public static Vector getSomeMWorkItemList(StorageListener listener) {
        
        Storage storage = getWirStorage(listener);
        Vector  vector  = storage.readSome(new MWorkItem().getClass());

        return vector;
    }

    public static int saveUserListStudyDefList(UserListStudyDefList userStudyList) {
        OpenXdataDataStorage.saveUsers(userStudyList.getUsers());

        StudyDefList studyDefList = userStudyList.getStudyDefList();

        OpenXdataDataStorage.saveStudyList(studyDefList);

        // controller.setStudyList(studyDefList);
        Vector studies = studyDefList.getStudies();

        for (int i = 0; i < studies.size(); i++) {
            StudyDef studyDef = (StudyDef) studies.elementAt(i);

            OpenXdataDataStorage.saveStudy(studyDef);

            studyDef = null;
        }
        studyDefList = null;
        studies.removeAllElements(); studies = null;

        return userStudyList.totalForms();
    }

    public static FormDef getFormDefForWorkItem(MWorkItem wir) {
        StudyDef study = OpenXdataDataStorage.getStudy(wir.getStudyId());

        if (study == null) {
            return null;
        }

        return study.getForm(wir.getFormId());
    }

    public static FormData getOrCreateFormData(MWorkItem wir, FormDef def) {
        FormData formData = getFormData(wir, false);

        if (formData == null) {
            formData = creatAndSaveFormData(wir.getStudyId(), def);
        }

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
        if (wir.getDataRecId() == -1) {
            return null;
        }

        FormData formData = OpenXdataDataStorage.getFormData(wir.getStudyId(), wir.getFormId(), wir.getDataRecId());

        if (addFormDef && (formData != null)) {
            formData.setDef(getFormDefForWorkItem(wir));
        }

        return formData;
    }

    private static FormData creatAndSaveFormData(int studyId, FormDef formDef) {
        FormData formData     = new FormData(formDef);
        boolean  saveFormData = OpenXdataDataStorage.saveFormData(studyId, formData);

        if (saveFormData) {
            formData.setDef(formDef);

            return formData;
        } else {
            throw new RuntimeException("Unable to save form data");
        }
    }

    public static Vector getMWorkItemListWithData(StorageListener listener) {
        Vector workItemList = getMWorkItemList(listener);
        Vector wirWithData  = new Vector(3);
        int    size         = workItemList.size();

        for (int i = 0; i < size; i++) {
            MWorkItem wir = (MWorkItem) workItemList.elementAt(i);

            if (wir.getDataRecId() != -1) {
                wirWithData.addElement(wir);
            }
        }

        return wirWithData;
    }
    
    public static void deleteAllWorkItems(StorageListener listener, boolean inclueEmpty) {
        Vector workItemList = getMWorkItemList(listener);
        if(workItemList!=null){
            int    size         = workItemList.size();

            for (int i = 0; i < size; i++) {
                MWorkItem wir = (MWorkItem) workItemList.elementAt(i);

                //deleteWorkItem(wir, listener);
                deleteFormDataForWir(wir, listener);

            }

            Storage storage = getWirStorage(listener);

            storage.deleteStore();
            
        }
        
    }

    public static void deleteCompleteWorkItems(StorageListener listener, boolean inclueEmpty) {
        Vector workItemList = getMWorkItemList(listener);
        int    size         = workItemList.size();

        for (int i = 0; i < size; i++) {
            MWorkItem wir = (MWorkItem) workItemList.elementAt(i);

            if (inclueEmpty) {
                deleteWorkItem(wir, listener);
            } else if (completed(wir)) {
                deleteWorkItem(wir, listener);
            }
        }
    }

    public static boolean completed(MWorkItem wir) {
        if (wir.getDataRecId() == -1) {
            return false;
        }

        return getFormData(wir, true).isRequiredAnswered();
    }

    public static void deleteWorkItem(MWorkItem wir, StorageListener listener) {
        if (wir.getDataRecId() != -1) {
            OpenXdataDataStorage.deleteFormData(wir.getStudyId(), getFormData(wir, false));
        }

        Storage storage = getWirStorage(listener);

        storage.delete(wir);
    }
    
    public static void deleteFormDataForWir(MWorkItem wir, StorageListener listener) {
        if (wir.getDataRecId() != -1) {
            OpenXdataDataStorage.deleteFormData(wir.getStudyId(), getFormData(wir, false));
        }

        
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
