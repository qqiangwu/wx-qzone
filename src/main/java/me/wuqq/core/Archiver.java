package me.wuqq.core;

import me.wuqq.domain.QZoneMeta;
import me.wuqq.domain.Record;

import java.util.List;

/**
 * Created by wuqq on 16-9-30.
 *
 * Archive records
 *
 */
public interface Archiver {
    void init(QZoneMeta meta);
    void save(List<Record> records);
}
