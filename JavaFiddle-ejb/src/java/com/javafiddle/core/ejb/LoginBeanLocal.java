package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.User;
import javax.ejb.Local;

/**
 *
 * @author viktor
 */
@Local
public interface LoginBeanLocal {
    public User performLogin(String nickname, String password);
}
