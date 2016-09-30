package me.wuqq.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import me.wuqq.core.Archiver;
import me.wuqq.core.Extractor;
import me.wuqq.core.Fetcher;
import me.wuqq.core.Progressor;
import me.wuqq.domain.Credential;
import me.wuqq.domain.QZoneMeta;
import me.wuqq.domain.Record;
import me.wuqq.util.QQUtils;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static me.wuqq.util.QQUtils.RECORD_PAGE_SIZE;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class FetcherImpl implements Fetcher {
    @Autowired Archiver mArchiver;
    @Autowired Progressor mProgressor;
    @Autowired OkHttpClient mHttpClient;
    @Autowired Extractor mExtractor;
    @Autowired ObjectMapper mMapper;

    @Override
    public void fetch(final Credential credential) throws InvalidCredentialException {
        val qzoneMeta = this.fetchMeta(credential);

        mArchiver.init(qzoneMeta);
        mProgressor.init(qzoneMeta.getRecordCount());

        val recordCount = qzoneMeta.getRecordCount();

        for (int offset = 0; offset < recordCount; offset += RECORD_PAGE_SIZE) {
            val records = this.fetchRecords(credential, offset);

            mProgressor.advance(records.size());
            mArchiver.save(records);
        }

        mProgressor.done();
    }

    private QZoneMeta fetchMeta(final Credential credential) throws InvalidCredentialException {
        val data = this.rawFetch(credential, 0);

        val recordCount = mExtractor.extractRecordCount(data);
        val username = mExtractor.extractUsername(data);

        return new QZoneMeta(recordCount, username, credential.getQq());
    }

    private List<Record> fetchRecords(final Credential credential, final int offset) throws InvalidCredentialException {
        val rawData = this.rawFetch(credential, offset);

        return mExtractor.extractRecords(rawData);
    }

    private Map<String, Object> rawFetch(final Credential credential, final int offset) throws InvalidCredentialException {
        val content = QQUtils.rawFetch(mHttpClient, credential, offset);

        try {
            return mMapper.readValue(content, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Unknow issues. Please report it to the author");
        }
    }
}
