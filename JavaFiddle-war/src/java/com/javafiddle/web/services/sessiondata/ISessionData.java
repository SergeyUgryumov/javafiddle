package com.javafiddle.web.services.sessiondata;

import java.io.File;
import java.io.Serializable;

/**
 * The only piece of info we need to save is the user's id.
 * @author roman
 */
public interface ISessionData extends Serializable{
    public String SEP = File.separator;
    public Long defaultUserId = 1L;
    public Long defaultProjectId = 2L;
    public Long defaultPackageId = 3L;
    public Long defaultClassId = 4L;
    public String defaultProjectName = "MyFirstProject";
    public String defaultPackageName = "com.javafiddle.main";
    
    public void reset();
    
    public Long getUserId();
    
    public void setUserId(Long userId);
    
    public Long getCurrentProjectId();
    
    public void setCurrentProjectId(Long projectId);
    
    
}
