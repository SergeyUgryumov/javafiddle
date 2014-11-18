package com.javafiddle.web.services.data;

import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.Tree;
import java.util.HashMap;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;

/**
 * Contains information about the project.<br/>
 * The structure of "files" is the following: <br/>
 * <i>Integer</i> - id of the file in the tree.<br/>
 * <i>Long</i> - timestamp.<br/>
 * <i>String</i> - content of the file.<br/>
 */
@SessionScoped
public class SessionData implements ISessionData {
    //These 3 pieces of data refer to old revisions system. 
    Tree tree;
    IdList idList;
    TreeMap<Integer, TreeMap<Long, String>> files;
    //Here comes the new version.
    
    public SessionData() {
        reset();
    }

    private void reset() {
        idList = new IdList();
        tree = new Tree();
        files = new TreeMap<>();
    }
    
    @Override
    public void resetData() {
        reset();
    }
    
    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    @Override
    public IdList getIdList() {
        return idList;
    }

    @Override
    public void setIdList(IdList idList) {
        this.idList = idList;
    }
    /**
     * 
     * @return 
     */
    @Override
    public TreeMap<Integer, TreeMap<Long, String>> getFiles() {
        return files;
    }

    @Override
    public void setFiles(TreeMap<Integer, TreeMap<Long, String>> files) {
        this.files = files;
    }
}
