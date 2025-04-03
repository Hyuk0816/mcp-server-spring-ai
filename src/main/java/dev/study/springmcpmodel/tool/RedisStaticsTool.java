package dev.study.springmcpmodel.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisStaticsTool {

    private final RedisTemplate<String, String> redisTemplate;

    @Tool(name = "find_key_without_ttl", description = "Find Redis key without TTL")
    public Map<String, Object> getRedisKeyWithOutTTL() {
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(1000).build();
        List<String> keys = redisTemplate.execute((RedisConnection redisConnection) -> {
            List<String> keyList = new ArrayList<>();
            try (Cursor<byte[]> cursor = redisConnection.keyCommands().scan(scanOptions)) {
                while (cursor.hasNext()) {
                    keyList.add(new String(cursor.next()));
                }
            }
            return keyList;
        }, true);

        return Map.of(
                "jsonrpc", "2.0",
                "result", Objects.requireNonNull(keys)
        );
    }

    @Tool(name = "find_key", description = "Find Redis key")
    public Map<String, Object> getRedisKey(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Map.of(
                    "jsonrpc", "2.0",
                    "error", "Key not found"
            );
        }

        return Map.of(
                "jsonrpc", "2.0",
                "result", String.format("Key: %s, Value: %s", key, value)
        );
    }

    @Tool(name = "delete_key", description = "Delete Redis key")
    public boolean deleteRedisKey(String key) {
        return redisTemplate.delete(key);
    }

    @Tool(name = "delete_keys", description = "Delete Redis keys")
    public int deleteRedisKeys(List<String> keys) {
        int deletedCount = 0;
        for (String key : keys) {
            if (redisTemplate.delete(key)) {
                deletedCount++;
            }
        }
        return deletedCount;
    }

    @Tool(name = "create_key", description = "Create Redis key")
    public boolean createRedisKey(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            // 로깅 추가 가능
            return false;
        }
    }
}
