package me.wuqq.core;

import me.wuqq.domain.Record;

import java.util.List;
import java.util.Map;

/**
 * Created by wuqq on 16-9-30.
 */
public interface Extractor {
    String extractUsername(Map<String, Object> rawData);

    int extractRecordCount(Map<String, Object> rawData);

    List<Record> extractRecords(Map<String, Object> rawData);
}
