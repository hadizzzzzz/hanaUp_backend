package hadiz.hanaup_backend.scheduler;

import hadiz.hanaup_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith("user:")) {
            Long userId = Long.valueOf(expiredKey.split(":")[1]);
            userRepository.deleteById(userId); // 유저 삭제
        }
    }
}
