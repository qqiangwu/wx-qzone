package me.wuqq.impl;

import lombok.val;
import me.wuqq.core.Extractor;
import me.wuqq.domain.Comment;
import me.wuqq.domain.CommentOnComment;
import me.wuqq.domain.Record;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class ExtractorImpl implements Extractor {
    @Override
    public String extractUsername(final Map<String, Object> rawData) {
        return (String) rawData.get("name");
    }

    @Override
    public int extractRecordCount(final Map<String, Object> rawData) {
        return ((Integer) rawData.get("total")).intValue();
    }

    @Override
    public List<Record> extractRecords(final Map<String, Object> rawData) {
        try {
            val msglist = (List<Map<String, Object>>) rawData.get("msglist");

            return msglist.stream()
                    .map(ExtractorImpl::toRecord)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Bad format of msglist received from QZone Server " + e);
        }
    }

    private final static Record toRecord(final Map<String, Object> rawData) {
        val content = (String) rawData.get("content");
        val createTime = (String) rawData.get("createTime");
        val rawComments = (List<Map<String, Object>>) rawData.getOrDefault("commentlist", Collections.emptyList());
        val comments = rawComments.stream()
                .map(ExtractorImpl::toComment)
                .collect(Collectors.toList());

        return new Record(createTime, content, comments);
    }

    private final static Comment toComment(final Map<String, Object> rawData) {
        val content = (String) rawData.get("content");
        val createTime = (String) rawData.get("createTime");
        val poster = (String) rawData.get("name");
        val rawComments = (List<Map<String, Object>>) rawData.getOrDefault("list_3", Collections.emptyList());
        val comments = rawComments.stream()
                .map(ExtractorImpl::toCommentOnComment)
                .collect(Collectors.toList());

        return new Comment(createTime, content, poster, comments);
    }

    private final static CommentOnComment toCommentOnComment(final Map<String, Object> rawData) {
        val content = (String) rawData.get("content");
        val createTime = (String) rawData.get("createTime");
        val poster = (String) rawData.get("name");

        return new CommentOnComment(createTime, content, poster);
    }
}
