/*package hadiz.hanaup_backend.scheduler;

import hadiz.hanaup_backend.repository.UserRepository;
import hadiz.hanaup_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private UserService userService;

    *//**
     * RedisMessageListenerContainer를 사용하여 Redis 키 만료 이벤트 리스너 초기화
     *
     * @param listenerContainer Redis 메시지 리스너 컨테이너
     *//*
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    *//**
     * Redis key expired 이벤트 발생 시 호출됨
     *
     * @param message 만료된 key 정보
     * @param pattern 이벤트 패턴 (__keyevent@*__:expired)
     *//*
    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        System.out.println("Key expired: " + expiredKey + ", Pattern: " + new String(pattern));

        // 특정 prefix로 시작하는 key 처리
        if (expiredKey.startsWith("user:")) {
            try {
                Long userId = Long.valueOf(expiredKey.split(":")[1]);
                userService.deleteUser(userId); // 유저 삭제
                System.out.println("Deleted user with ID: " + userId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid key format: " + expiredKey);
            }
        }
    }
}*/

