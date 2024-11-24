package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public User findOne(Long memberId){
        return userRepository.findById(memberId);
    }

    @Transactional //readonly false
    public Long join(User user){
        userRepository.save(user);
        return user.getUserID();
    }

    @Transactional
    public void saveUserWithExpiration(User user) {
        redisTemplate.opsForValue().set(
                "user:" + user.getUserID(),
                user,
                Duration.ofHours(1) // 1시간 후 자동 삭제
        );
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
