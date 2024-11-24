package hadiz.hanaup_backend.repository;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HanaMoneyByCurrencyRepository {

    private final EntityManager em;

    @Transactional
    public List<HanaMoneyByCurrency> findAllByUser(User user) {
        return em.createQuery("SELECT h FROM HanaMoneyByCurrency h WHERE h.user = :user", HanaMoneyByCurrency.class)
                .setParameter("user", user)
                .getResultList();
    }

    // 유저별로 찾은 목록에 더해서, 나라로 특정 하나머니 검색
    @Transactional
    public HanaMoneyByCurrency findHanaMoneyByCountry(List<HanaMoneyByCurrency> hanaMoneyList, String country) {
        return hanaMoneyList.stream()
                .filter(h -> country.equals(h.getCountry()))
                .findFirst() // 첫 번째 매칭되는 요소를 반환
                .orElse(null); // 매칭되는 요소가 없을 경우 null 반환
    }

    @Transactional
    public void saveCustom(HanaMoneyByCurrency hanaMoneyByCurrency) {
        if (hanaMoneyByCurrency.getHanaMoneyID() == null) {
            em.persist(hanaMoneyByCurrency);
        } else {
            em.merge(hanaMoneyByCurrency);
        }
    }

    @Transactional
    public void delete(HanaMoneyByCurrency hanaMoneyByCurrency) {
        if (hanaMoneyByCurrency != null) {
            em.remove(hanaMoneyByCurrency);
        }
    }
}
