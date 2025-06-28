package com.board.Admin;

import java.time.LocalDateTime;

public interface PostListDto {
    Long getId();
    String getTitle();
    String getWriterNickname();
    LocalDateTime getCreatedDate();
    boolean getDeleted();
    boolean getReported();
}
