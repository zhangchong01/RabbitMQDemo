package com.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author ZhangChong created on 2019/09/07
 **/
@Slf4j
@Service
public class BusinessService {
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 6000L, multiplier = 2))
    public void processMessage(Map msgBody) throws Exception {
        log.info("processMessage:{}", msgBody);
        int i = 1 / 0;
    }
}
