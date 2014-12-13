/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.web.beans;

import com.javafiddle.core.jpa.User;
import com.javafiddle.web.services.sessiondata.ISessionData;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author viktor
 */
@Named
@SessionScoped
public class CurUserBean implements Serializable {
    @Inject
    private ISessionData sd;
    
    private Long id;
    private String name;
    
    public CurUserBean() {
    }

    public void setCurUserId(Long id) {
        this.id = id;
        sd.setUserId(id);
    }
    public Long getCurUserId() {
        return id;
    }
    public void setCurUserName(String name) {
        this.name = name;
    }
    public String getCurUserName() {
        return name;
    }
}
