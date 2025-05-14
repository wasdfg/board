package com.board.Notice;

import lombok.Getter;

@Getter
public class CommentCreatedEvent {
    private final Long commentId;
    private final Long parentCommentUserId;
    private final Long postAuthorId;
    private final Long commenterId;
    private final String commentContent;

    public CommentCreatedEvent(Long commentId, Long parentCommentUserId,
                               Long postAuthorId, Long commenterId, String commentContent) {
        this.commentId = commentId;
        this.parentCommentUserId = parentCommentUserId;
        this.postAuthorId = postAuthorId;
        this.commenterId = commenterId;
        this.commentContent = commentContent;
    }
}
