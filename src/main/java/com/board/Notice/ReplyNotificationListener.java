package com.board.Notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReplyNotificationListener {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleReplyCreatedEvent(ReplyCreatedEvent event) {
        Long recipientId = null;


        if (event.getParentReplyWriterId() != null &&
                !event.getParentReplyWriterId().equals(event.getReplyWriterId())) {
            recipientId = event.getParentReplyWriterId();
        }

        else if (!event.getQuestionWriterId().equals(event.getReplyWriterId())) {
            recipientId = event.getQuestionWriterId();
        }

        if (recipientId == null) return;

        try {
            Map<String, Object> notice = new HashMap<>();
            notice.put("replyId", event.getReplyId());
            notice.put("from", event.getReplyWriterId());
            notice.put("message", event.getReplyContent());
            notice.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(notice);
            redisTemplate.opsForList().leftPush("user:notice:" + recipientId, json);
            redisTemplate.expire("user:notice:" + recipientId, Duration.ofDays(7));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
