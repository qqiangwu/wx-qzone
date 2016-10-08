package me.wuqq.core;

/**
 * Created by wuqq on 2016/10/2.
 */
public class BadCredentialException extends Exception {
    public BadCredentialException() {
    }

    public BadCredentialException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return this.getCause() != null?
                "Bad credential: " + this.getCause().getMessage():
                "Bad credential";
    }
}
