package com.example.younet.login.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    // key, value 값 redis 저장
    public void setValueWithTTL(String key, Object value, long timeout, TimeUnit unit) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, timeout, unit);
    }

    // key값으로 value 가져오기
    public Object getValue(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);

        return object;
    }

    // key값으로 value 삭제
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}