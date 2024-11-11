package hadiz.hanaup_backend.repository.after;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ForeignCurrencyAccountRepository {

    private final EntityManager em;

    public void save(ForeignCurrencyAccount account) {
        if (account.getAccountID() == null) {
            em.persist(account);  // 새로운 엔티티일 경우 persist
        } else {
            em.merge(account);  // 기존 엔티티일 경우 merge
        }
    }

    // userId로 ForeignCurrencyAccount 조회, 하나만 반환
    public ForeignCurrencyAccount findByUserId(Long userId) {
        try {
            return em.createQuery("select f from ForeignCurrencyAccount f where f.user.id = :userId", ForeignCurrencyAccount.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // 결과가 없을 경우 null 반환
        }
    }


}
