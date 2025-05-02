package com.proyecto3.risk.configurations;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isSameUser(Authentication authentication, String username) {
        if (authentication == null || username == null) {
            return false;
        }
        return authentication.getName().equals(username);
    }
}