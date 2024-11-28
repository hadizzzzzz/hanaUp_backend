package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //private final RedisTemplate<String, Object> redisTemplate;


    public User findOne(Long memberId){
        return userRepository.findById(memberId);
    }

    @Transactional //readonly false
    public Long join(User user){
        userRepository.save(user);
        return user.getUserID();
    }

    /*@Transactional
    public void saveUserWithExpiration(User user) {

        join(user);

        String key = "user:" + user.getUserID();
        System.out.println("Generated Key: " + key);

        redisTemplate.opsForValue().set(
                key,
                user,
                Duration.ofHours(1) // 1시간 후 자동 삭제
        );
    }*/

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
