package com.example.URL.Shortener.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.Random;


@Service
public class UrlService {
    private final StringRedisTemplate redis;
    private final String baseUrl;
    private final Random random = new Random();


    public UrlService(StringRedisTemplate redis, @Value("${app.base-url}") String baseUrl) {
        this.redis = redis;
        this.baseUrl = baseUrl;
    }


    public String createShort(String originalUrl) {
// try to generate unique 6-char code
        for (int i = 0; i < 6; i++) {
            String code = randomCode(6);
            String key = keyFor(code);
            Boolean exists = redis.hasKey(key);
            if (exists == null || !exists) {
                redis.opsForValue().set(key, originalUrl);
// set visit counter
                redis.opsForValue().set(counterKey(code), "0");
                return baseUrl + "/s/" + code;
            }
        }
// fallback: use timestamp-based code
        String code = String.valueOf(System.currentTimeMillis()).substring(6);
        redis.opsForValue().set(keyFor(code), originalUrl);
        redis.opsForValue().set(counterKey(code), "0");
        return baseUrl + "/s/" + code;
    }


    public String getOriginal(String code) {
        return redis.opsForValue().get(keyFor(code));
    }


    public void increaseCounter(String code) {
        redis.opsForValue().increment(counterKey(code));
    }


    private String randomCode(int length) {
        String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }


    private String keyFor(String code) { return "short:" + code; }
    private String counterKey(String code) { return "short:counter:" + code; }
}