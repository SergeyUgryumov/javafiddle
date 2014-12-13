package com.javafiddle.web.services.sessiondata;

import javax.enterprise.context.SessionScoped;

/**
 *
 * @author roman
 */
@SessionScoped
public class SessionData implements ISessionData {
    
    private Long userId;
    private Long currentProjectId;
    
    public SessionData() {}
    
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

    @Override
    public void reset() {
        this.currentProjectId = 0L;
        this.userId = 0L;
    }
    
}
