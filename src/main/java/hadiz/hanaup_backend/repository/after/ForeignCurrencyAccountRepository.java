package hadiz.hanaup_backend.repository.after;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

    public void delete(ForeignCurrencyAccount foreignCurrencyAccount) {
        if (foreignCurrencyAccount != null) {
            em.remove(foreignCurrencyAccount);
        }
    }

}
