package me.wuqq.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import me.wuqq.core.Archiver;
import me.wuqq.domain.QZoneMeta;
import me.wuqq.domain.Record;
import okio.BufferedSink;
import okio.Okio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class ArchiverImpl implements Archiver {
    BufferedSink mOutput;
    boolean mHasStarted = false;

    @Autowired ObjectMapper mMapper;

    @PreDestroy
    @SneakyThrows
    public void destroy() {
        if (mOutput != null) {
            mOutput.writeUtf8("]");
            mOutput.close();
            mOutput = null;
        }
    }

    @Override
    public void init(final QZoneMeta meta) {
        val fileName = meta.getQq() + ".json";

        try {
            mOutput = Okio.buffer(Okio.sink(new File(fileName)));
            mOutput.writeUtf8("[");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create archive file");
        }
    }

    @Override
    public void save(final List<Record> records) {
        try {
            for (val record : records) {
                if (mHasStarted) {
                    mOutput.writeUtf8(",");
                } else  {
                    mHasStarted = true;
                }
                mOutput.writeUtf8(mMapper.writeValueAsString(record));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to archive record");
        }
    }
}
