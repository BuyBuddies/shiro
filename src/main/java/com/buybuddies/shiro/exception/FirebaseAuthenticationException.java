package com.buybuddies.shiro.exception;

import org.springframework.security.core.AuthenticationException;

public class FirebaseAuthenticationException extends AuthenticationException {
    public FirebaseAuthenticationException(String msg) {
        super(msg);
    }

    public FirebaseAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}