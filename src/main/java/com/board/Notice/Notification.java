package com.board.Notice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;
    
    /*받는 유저 ID*/
    private Long receiverId;
    
    private String content;
    
    /*댓글 알림일지 대댓글알림일지 일단 구분하기 위해 */
    private String type;
    
    /*읽음 표시*/
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
