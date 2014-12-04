package com.javafiddle.web.services.sessiondata;

/**
 *
 * @author roman
 */
public class SessionData implements ISessionData {
    
    private Long userId;
    private Long currentProjectId;
    
    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getCurrentProjectId() {
        return this.currentProjectId;
    }

    @Override
    public void setCurrentProjectId(Long projectId) {
        this.currentProjectId = projectId;
    }
    
}
