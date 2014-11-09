/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.javafiddle.core.jpa.Files;
import javax.ejb.Stateless;

/**
 *
 * @author viktor
 */
@Stateful
public class LoginBean implements LoginBeanLocal {
    @PersistenceContext
    private EntityManager em;

    private String message;
    public String getHash(String str) { // функия хэширования (здесь используется для паролей)
        
        MessageDigest md5 ;        
        StringBuffer  hexString = new StringBuffer();
        
        try {            
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(str.getBytes()); 
                        
            byte messageDigest[] = md5.digest();
                        
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }                                                                           
        } 
        catch (NoSuchAlgorithmException e) {                        
            return e.toString();
        }
        
        return hexString.toString();
    }
    // check users with these nickname and pwd
    private boolean tryLogin(String nickname, String pwd) {
       User currentUser = null;
       Long userId = null;
       // list of users with same nick and pwd
       List<User> pass = em.createQuery("select u from User u where u.nickname =:nickname and u.password=:password")
                    .setParameter("nickname", nickname)
                    .setParameter("password", pwd).getResultList();
       if(pass == null || pass.isEmpty()){
            
           return false;
       }
      return true;
    }

    // return and check existing user with these nick and pwd
    @Override
    public User performLogin(String nickname, String password) {
        User user = null;
        //return list of users with the same nickname
         List<User> users = em.createQuery("select u from User u where u.nickname =:nickname")
                    .setParameter("nickname", nickname).getResultList();
       if(users == null || users.size() != 1){
           System.out.println("Login is not unique or no user with this login exists!");
          return user;
       }
       user = users.get(0);
     
       String pwd = getHash(password);
       password = null;
        if(!tryLogin(nickname, pwd)) {
            return null;
        }
        return user;
    }
}
