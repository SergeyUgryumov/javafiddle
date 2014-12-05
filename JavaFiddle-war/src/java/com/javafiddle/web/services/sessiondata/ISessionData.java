package com.javafiddle.web.services.sessiondata;

import java.io.Serializable;

/**
 * The only piece of info we need to save is the user's id.
 * @author roman
 */
public interface ISessionData extends Serializable{
    
    public void reset();
    
    public Long getUserId();
    
    public void setUserId(Long userId);
    
    public Long getCurrentProjectId();
    
    public void setCurrentProjectId(Long projectId);
}
