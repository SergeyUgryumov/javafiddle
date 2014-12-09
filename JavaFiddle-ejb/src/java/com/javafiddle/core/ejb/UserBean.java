package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.User;
import java.io.File;
import java.util.List;
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
    public User getUserByUsername(String name) {
        return (User) em.createQuery("select u from User u where u.nickname =:name")
                .setParameter("name", name)
                .getSingleResult();
    }
    /**
     * 
     * @param name
     * @param password
     * @param email
     * @return the id of the newly added user
     */
    public Long addUser(String name,String password, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        if (name.equals("guest")) {
            String newName = "guest" + File.separator + this.generateRandomSuffix();
            user.setNickname(newName);
            System.out.println("UserBean.addUser():guest:" +user.toString());
            em.persist(user);
            user = this.getUserByUsername(newName);
            System.out.println("UserBean.addUser():guest:" +user.toString());
            em.remove(user);
            user.setNickname("guest" + File.separator +user.getId());
            em.persist(user);
            System.out.println("UserBean.addUser():guest:" +user.toString());
            return user.getId();
        }
        else {
            user.setNickname(name);
            em.persist(user);
            user = this.getUserByUsername(name);
            return user.getId();
        }
    }
    public List<Long> getListOfUsers() {
        return em.createQuery("select u.id from User u")
                .getResultList();
    }
    
    public Long addNewGuestUser() {
        return this.addUser("guest", null, "guest@javafiddle.org");
    }
    
    private String generateRandomSuffix() {
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char randChar = (char) (Math.round((float)Math.random()*1000) % 26 + 'a');
            suffix.append(randChar);
        }
        return suffix.toString();
    }
}
