package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author viktor
 */
@Stateful
public class LoginBean implements LoginBeanLocal {
    @PersistenceContext
    private EntityManager em;

    private String message;
    public String getHash(String str) { // hashing passwords
        
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
    // check users with these nickname and password
    private boolean tryLogin(String nickname, String pwd) {
       // list of users with same nick and password, which have been typed
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
       /* User my = null;
        my = new User();
        my.setNickname("viktor");
        my.setPassword(getHash("123"));
        my.setEmail("mail");
        //анализирует, является ли объект новой записью для базы данных, и если нет - генерирует 
        em.persist(my);
        List<User> example = em.createQuery("select u.nickname from User u").getResultList();
        System.out.println("Result " + example);
        */
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
            System.out.println("Access denied for " +  nickname);
            return null;
        }
        System.out.println("Access permission for " +  nickname);
        return user;
    }
}
