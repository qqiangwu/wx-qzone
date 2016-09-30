package me.wuqq.domain;

import lombok.Value;

/**
 * Created by wuqq on 16-9-30.
 */
@Value
public class CommentOnComment {
    String creationTime;
    String content;
    String poster;
}
