package com.javafiddle.web.services.sessiondata;

/**
 *
 * @author roman
 */
public class SessionData implements ISessionData {
    
    private Long userId;
    
    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}
