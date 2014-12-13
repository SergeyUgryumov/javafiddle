package com.javafiddle.web.beans;

import com.javafiddle.core.jpa.User;
import com.javafiddle.core.ejb.LoginBeanLocal;
import com.javafiddle.core.ejb.UserBean;
import com.javafiddle.core.ejb.UserManagerLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author viktor
 */
@Named
@SessionScoped
public class LoginBean implements Serializable{
    @Inject
    private LoginBeanLocal ejbLoginBean;
    @Inject
    private CurUserBean curUserBean;
    @EJB
    private UserBean userBean;
    /**
     * Creates a new instance of LoginBean
     */
    private User user;
    private String nickname;
    private String password;
    
    public LoginBean() {
    }
    
    // Caused by pushing Log in
    public void login(){
        System.out.println("Try to log in: login: " + nickname +", password: "+ password); // It works
        
        boolean error = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.nickname.isEmpty() || this.password.isEmpty()){
            context.addMessage("loginErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some field is empty", "Some field input failed"));
            error = true;           
        }
        if (!error){
            this.user = ejbLoginBean.performLogin(nickname, password);
            if (this.user != null){               
                this.curUserBean.setCurUserName(this.user.getNickname());
                this.curUserBean.setCurUserId(userBean.getUserByUsername(this.user.getNickname()).getId());
            }
            else {
                context.addMessage("loginErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "incorrect username or password", 
                        "incorrect username or password")); //It's a magic yo!
            }
        }
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
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Long getId(){
        return user.getId();
    }
    
}
