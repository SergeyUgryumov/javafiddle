package com.javafiddle.revisions;

import com.javafiddle.web.templates.ClassTemplate;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.TreeFile;
import java.util.Date;
import java.util.TreeMap;

/**
 * Revisions class is supposed to be a version control system. <br/>
 * Will be replaced with git. 
 */
public class Revisions {
    IdList idList;
    TreeMap<Integer, TreeMap<Long, String>> files;

    public Revisions(IdList idList, TreeMap<Integer, TreeMap<Long, String>> files) {
        this.idList = idList;
        this.files = files;
    }
    
    public void addFileRevision(TreeFile file, IdList idList) {
        addFileRevision(file.getId(), 0, new ClassTemplate(file, idList).getValue());
    }
    /**
     * 
     * @param id
     * @param timeStamp
     * @param value
     * @return 
     */
    public int addFileRevision(int id, long timeStamp, String value) {
        if (!idList.isFile(id))
            return 400;
        
        if (!files.containsKey(id))
            files.put(id, new TreeMap<Long, String>());
        else {
            long old = idList.getFile(id).getTimeStamp();
            String oldText = files.get(id).get(old);
            if (oldText.hashCode() == value.hashCode())
                return 304;
        }
        long result = timeStamp == 0 ? new Date().getTime() : timeStamp;
        files.get(id).put(result, value);
        idList.getFile(id).setTimeStamp(result);
        return 200;
    }
}
