package com.board.Notice;

import lombok.Getter;

@Getter
public class ReplyCreatedEvent {

    private final Long replyId;               // 댓글 ID
    private final Long parentReplyWriterId;   // 부모 댓글 작성자 ID (대댓글일 경우)
    private final Long questionWriterId;      // 원글(질문) 작성자 ID
    private final Long replyWriterId;         // 현재 댓글 작성자 ID
    private final String replyContent;        // 댓글 내용

    public ReplyCreatedEvent(Long replyId, Long parentReplyWriterId,
                             Long questionWriterId, Long replyWriterId, String replyContent) {
        this.replyId = replyId;
        this.parentReplyWriterId = parentReplyWriterId;
        this.questionWriterId = questionWriterId;
        this.replyWriterId = replyWriterId;
        this.replyContent = replyContent;
    }
}
