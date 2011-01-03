/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.modules.workflows.server.dao;

import org.openxdata.modules.workflows.model.shared.DBSpecStudyMap;
import org.openxdata.server.dao.BaseDAO;

/**
 *
 * @author kay
 */

public interface DBSpecStudyMapDAO extends BaseDAO<DBSpecStudyMap> {

    public DBSpecStudyMap getMap(String caseID, String taskID);

}
