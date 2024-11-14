package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.TravelLogRepository;
import hadiz.hanaup_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOne(Long memberId){
        return userRepository.findById(memberId);
    }

    @Transactional //readonly false
    public Long join(User user){
        userRepository.save(user);
        return user.getUserID();
    }
}
