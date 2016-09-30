package me.wuqq.core;

import me.wuqq.domain.Credential;

/**
 * Created by wuqq on 16-9-30.
 *
 * Fetch records from QZone.
 *
 */
public interface Fetcher {
    class InvalidCredentialException extends Exception {

    }

    void fetch(Credential credential) throws InvalidCredentialException;
}
