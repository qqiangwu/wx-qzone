package me.wuqq.core;

import me.wuqq.util.BadCredentialException;

/**
 * Created by wuqq on 16-9-30.
 *
 * Fetch records from QZone.
 *
 */
public interface Fetcher {
    void fetch(String credential) throws BadCredentialException;
}
