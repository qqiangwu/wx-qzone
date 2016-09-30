package me.wuqq.domain;

import lombok.Value;

import java.util.List;

/**
 * Created by wuqq on 16-9-30.
 */
@Value
public class Record {
    String creationTime;
    String content;
    List<Comment> comments;
}
