package hadiz.hanaup_backend.repository;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HanaMoneyByCurrencyRepository {

    private final EntityManager em;

    public List<HanaMoneyByCurrency> findAllByUser(User user) {
        return em.createQuery("SELECT h FROM HanaMoneyByCurrency h WHERE h.user = :user", HanaMoneyByCurrency.class)
                .setParameter("user", user)
                .getResultList();
    }

    public void saveCustom(HanaMoneyByCurrency hanaMoneyByCurrency) {
        if (hanaMoneyByCurrency.getHanaMoneyID() == null) {
            em.persist(hanaMoneyByCurrency);
        } else {
            em.merge(hanaMoneyByCurrency);
        }
    }
}
