package me.wuqq.domain;

import lombok.Value;

import java.util.List;

/**
 * Created by wuqq on 16-9-30.
 */
@Value
public class Comment {
    String creationTime;
    String content;
    String poster;
    List<CommentOnComment> commentOnComments;
}
