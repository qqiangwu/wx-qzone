package me.wuqq.core;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by wuqq on 16-9-30.
 *
 * Fetch records from QZone.
 *
 */
public interface Fetcher {
    JsonNode fetchMessagesFromOffset(int i) throws BadCredentialException;

    String getTargetQQ();

    int getPageSize();
}
