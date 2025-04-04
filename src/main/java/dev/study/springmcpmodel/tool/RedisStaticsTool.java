package dev.study.springmcpmodel.tool;

import io.modelcontextprotocol.server.McpSyncServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisStaticsTool {

    private final RedisTemplate<String, String> redisTemplate;

    @Tool(name = "find_key_without_ttl", description = "Find Redis key without TTL")
    public List<String> getRedisKeyWithOutTTL() {
        ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(1000).build();
        // TTL이 -1인 경우 만료 시간이 설정되지 않은 키입니다
        return redisTemplate.execute((RedisConnection redisConnection) -> {
            List<String> keyList = new ArrayList<>();
            try (Cursor<byte[]> cursor = redisConnection.keyCommands().scan(scanOptions)) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next());
                    // TTL이 -1인 경우 만료 시간이 설정되지 않은 키입니다
                    Long ttl = redisTemplate.getExpire(key);
                    if (ttl == -1) {
                        keyList.add(key);
                    }
                }
            }
            return keyList;
        }, true);
    }

    @Tool(name = "find_key", description = "Find Redis key")
    public Map<String ,String> getRedisKey(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Map.of();
        }

        return Map.of(key, value);
    }

    @Tool(name = "delete_key", description = "Delete Redis key")
    @Transactional
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
    @Transactional
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
