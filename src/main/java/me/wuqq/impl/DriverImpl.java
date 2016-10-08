package me.wuqq.impl;

import lombok.val;
import me.wuqq.core.*;
import me.wuqq.domain.QZoneMeta;
import me.wuqq.domain.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wuqq on 16-10-8.
 */
@Component
public final class DriverImpl implements Driver {
    @Autowired Archiver mArchiver;
    @Autowired Progressor mProgressor;
    @Autowired Extractor mExtractor;
    @Autowired Fetcher mFetcher;

    QZoneMeta mMetaInfo;

    @Override
    public void fetch() throws BadCredentialException {
        this.initComponents();
        this.doFetch();
    }

    private void initComponents() throws BadCredentialException {
        mMetaInfo = this.fetchMeta();

        mArchiver.init(mMetaInfo);
        mProgressor.init(mMetaInfo.getRecordCount());
    }

    private void doFetch() throws BadCredentialException {
        val recordCount = mMetaInfo.getRecordCount();
        val recordPageSize = mFetcher.getPageSize();

        for (int offset = 0; offset < recordCount; offset += recordPageSize) {
            val records = this.fetchRecordsFromOffset(offset);

            mProgressor.advance(records.size());
            mArchiver.save(records);
        }
    }

    private QZoneMeta fetchMeta() throws BadCredentialException {
        val data = mFetcher.fetchMessagesFromOffset(0);

        val recordCount = mExtractor.extractRecordCount(data);
        val username = mExtractor.extractUsername(data);

        return new QZoneMeta(recordCount, username, mFetcher.getTargetQQ());
    }

    private List<Record> fetchRecordsFromOffset(final int offset) throws BadCredentialException {
        val rawData = mFetcher.fetchMessagesFromOffset(offset);

        return mExtractor.extractRecords(rawData);
    }
}
