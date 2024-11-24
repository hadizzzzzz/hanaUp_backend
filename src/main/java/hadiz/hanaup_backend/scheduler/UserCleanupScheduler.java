package hadiz.hanaup_backend.scheduler;

import hadiz.hanaup_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class UserCleanupScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredUsers() {
        Set<String> keys = redisTemplate.keys("user:*");

        for (String key : keys) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl <= 0) { // 만료된 키 확인
                Long userId = Long.valueOf(key.split(":")[1]);
                userRepository.deleteById(userId); // DB에서 삭제
                redisTemplate.delete(key); // Redis에서 키 삭제
            }
        }
    }
}
