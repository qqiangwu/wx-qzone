package me.wuqq.core;

import com.fasterxml.jackson.databind.JsonNode;
import me.wuqq.domain.Record;

import java.util.List;

/**
 * Created by wuqq on 16-9-30.
 */
public interface Extractor {
    String extractUsername(JsonNode rawData);

    int extractRecordCount(JsonNode rawData);

    List<Record> extractRecords(JsonNode rawData);
}
