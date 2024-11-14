package hadiz.hanaup_backend.repository.after;

import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.domain.after.ForexTech;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ForexTechRepository {

    private final EntityManager em;

    public void save(ForexTech forexTech) {
        if (forexTech.getForexTechID() == null) {
            em.persist(forexTech);  // 새로운 엔티티일 경우 persist
        } else {
            em.merge(forexTech);  // 기존 엔티티일 경우 merge
        }
    }

    public ForexTech findByUserId(Long userId) {
        try {
            return em.createQuery("select f from ForexTech f where f.user.id = :userId", ForexTech.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // 결과가 없을 경우 null 반환
        }
    }
}
