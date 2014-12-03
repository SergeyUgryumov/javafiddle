package com.javafiddle.web.services.sessiondata;

/**
 * The only piece of info we need to save is the user's id.
 * @author roman
 */
public interface ISessionData {
    
    public Long getUserId();
    
    public void setUserId(Long userId);
}
