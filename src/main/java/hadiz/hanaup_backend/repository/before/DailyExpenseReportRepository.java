package hadiz.hanaup_backend.repository.before;

import hadiz.hanaup_backend.domain.DailyExpenseReport;
import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyExpenseReportRepository {

    private final EntityManager em;

    public DailyExpenseReport findByCountryAndDay(String country, int day) {
        String jpql = "SELECT d FROM DailyExpenseReport d WHERE d.country = :country AND d.day = :day";
        return em.createQuery(jpql, DailyExpenseReport.class)
                .setParameter("country", country)
                .setParameter("day", day)
                .getSingleResult(); // 필요시 getResultList()로 복수 결과 처리 가능
    }

    public void saveCustom(DailyExpenseReport dailyExpenseReport) {
        if (dailyExpenseReport.getCountry() == null) {
            em.persist(dailyExpenseReport);
        } else {
            em.merge(dailyExpenseReport);
        }
    }
}
