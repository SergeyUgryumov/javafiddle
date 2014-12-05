package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.User;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roman
 */
@Stateless
@LocalBean
@Named(value = "userBean")
public class UserBean {
    @PersistenceContext
    EntityManager em;
    
    public UserBean() {}
    
    public User getUserById(Long id) {
        return em.find(User.class, id);
    }
}
