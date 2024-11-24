package hadiz.hanaup_backend.repository;

import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        if (user.getUserID() == null) {
            em.persist(user);  // 새로운 엔티티일 경우 persist() 사용
        } else {
            em.merge(user);    // 이미 존재하는 엔티티일 경우 merge() 사용
        }
    }

    public User findById(Long userId) {
        return em.find(User.class, userId);
    }

    public void deleteById(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM User").executeUpdate();
    }
}
