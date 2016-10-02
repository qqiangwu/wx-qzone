package me.wuqq.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.val;
import me.wuqq.core.Extractor;
import me.wuqq.domain.Comment;
import me.wuqq.domain.CommentOnComment;
import me.wuqq.domain.Record;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class ExtractorImpl implements Extractor {
    @Override
    public String extractUsername(final JsonNode rawData) {
        return rawData.path("name").asText();
    }

    @Override
    public int extractRecordCount(final JsonNode rawData) {
        return rawData.path("total").asInt();
    }

    @Override
    public List<Record> extractRecords(final JsonNode rawData) {
        try {
            val msglist = toStream(rawData.get("msglist"));

            return msglist
                    .map(ExtractorImpl::toRecord)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Bad format of msglist received from QZone Server " + e);
        }
    }

    private static Stream<JsonNode> toStream(final JsonNode node) {
        if (node instanceof ArrayNode) {
            val array = (ArrayNode) node;

            return StreamSupport.stream(array.spliterator(), false);
        } else {
            return Stream.empty();
        }
    }

    private final static Record toRecord(final JsonNode rawData) {
        val content = rawData.get("content").asText();
        val createTime = rawData.get("createTime").asText();
        val rawComments = toStream(rawData.get("commentlist"));
        val comments = rawComments
                .map(ExtractorImpl::toComment)
                .collect(Collectors.toList());

        return new Record(createTime, content, comments);
    }

    private final static Comment toComment(final JsonNode rawData) {
        val content = rawData.get("content").asText();
        val createTime = rawData.get("createTime").asText();
        val poster = rawData.get("name").asText();
        val rawComments = toStream(rawData.get("list_3"));
        val comments = rawComments
                .map(ExtractorImpl::toCommentOnComment)
                .collect(Collectors.toList());

        return new Comment(createTime, content, poster, comments);
    }

    private final static CommentOnComment toCommentOnComment(final JsonNode rawData) {
        val content = rawData.get("content").asText();
        val createTime = rawData.get("createTime").asText();
        val poster = rawData.get("name").asText();

        return new CommentOnComment(createTime, content, poster);
    }
}
