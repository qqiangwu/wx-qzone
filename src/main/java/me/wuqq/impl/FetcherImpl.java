package me.wuqq.impl;

import lombok.val;
import me.wuqq.core.Archiver;
import me.wuqq.core.Extractor;
import me.wuqq.core.Fetcher;
import me.wuqq.core.Progressor;
import me.wuqq.domain.QZoneMeta;
import me.wuqq.domain.Record;
import me.wuqq.util.BadCredentialException;
import me.wuqq.util.QZoneFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static me.wuqq.util.QZoneFetcher.RECORD_PAGE_SIZE;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class FetcherImpl implements Fetcher {
    @Autowired Archiver mArchiver;
    @Autowired Progressor mProgressor;
    @Autowired Extractor mExtractor;

    QZoneMeta mMetaInfo;
    QZoneFetcher mFetcherImpl;

    @Override
    public void fetch(final String cookie) throws BadCredentialException {
        this.initComponents(cookie);
        this.doFetch();
    }

    private void initComponents(final String cookie) throws BadCredentialException {
        mFetcherImpl = new QZoneFetcher(cookie);
        mMetaInfo = this.fetchMeta();

        mArchiver.init(mMetaInfo);
        mProgressor.init(mMetaInfo.getRecordCount());
    }

    private void doFetch() throws BadCredentialException {
        val recordCount = mMetaInfo.getRecordCount();

        for (int offset = 0; offset < recordCount; offset += RECORD_PAGE_SIZE) {
            val records = this.fetchRecordsFromOffset(offset);

            mProgressor.advance(records.size());
            mArchiver.save(records);
        }
    }

    private QZoneMeta fetchMeta() throws BadCredentialException {
        val data = mFetcherImpl.fetchMessagesFromOffset(0);

        val recordCount = mExtractor.extractRecordCount(data);
        val username = mExtractor.extractUsername(data);

        return new QZoneMeta(recordCount, username, mFetcherImpl.getTargetQQ());
    }

    private List<Record> fetchRecordsFromOffset(final int offset) throws BadCredentialException {
        val rawData = mFetcherImpl.fetchMessagesFromOffset(offset);

        return mExtractor.extractRecords(rawData);
    }
}
