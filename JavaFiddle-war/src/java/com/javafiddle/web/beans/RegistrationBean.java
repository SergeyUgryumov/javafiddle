/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.web.beans;

import com.javafiddle.core.ejb.JFProjectBean;
import com.javafiddle.core.ejb.RegistrationBeanLocal;
import com.javafiddle.core.ejb.UserBean;
import com.javafiddle.core.jpa.JFProject;
import com.javafiddle.core.jpa.User;
import com.javafiddle.web.services.sessiondata.ISessionData;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.SessionScoped;
/**
 *
 * @author viktor
 */

@Named
@RequestScoped
@ManagedBean
public class RegistrationBean implements Serializable{
    @Inject
    private RegistrationBeanLocal ejbRegistrationBean;
   
    @Inject
    private ISessionData sd;
    
    @EJB
    private JFProjectBean projectBean;
    
    @EJB
    private UserBean userBean;
    
    private User user;
    private String nickname;
    private String password;
    private String email;

    public void addNewUser(){
        System.out.println("Try to register: login: " + nickname + ", email: " 
                + email + ", password: " + password);
        boolean error = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.nickname.isEmpty()){    
            context.addMessage("registerErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Username field is not filled", "Some field input failed"));
            error = true;
        }
        if (this.email.isEmpty()){
            context.addMessage("registerErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Email field is not filled", "Some field input failed"));
            error = true;
        }
        if (this.password.isEmpty()){
            context.addMessage("registerErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Password field is not filled", "Some field input failed"));
            error = true;
        }
        if (!error){
            user = ejbRegistrationBean.addNewUser(this.nickname, this.password, this.email);
            System.out.println(user.toString());
            if (user == null) {
                context.addMessage("registerErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        ejbRegistrationBean.getMessage(), ejbRegistrationBean.getMessage()));
            }
            Long projId = projectBean.addNewDefaultProject(user.getId());
            sd.setCurrentProjectId(projId);
        }
        //System.out.println("ADD NEW USER");
    }
    
    
    public RegistrationBean() {
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
        
}