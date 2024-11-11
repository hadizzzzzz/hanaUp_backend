package hadiz.hanaup_backend.repository.before;

import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import hadiz.hanaup_backend.domain.before.TravelSpendingTest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TravelSpendingTestRepository {

    private final EntityManager em;

    public void save(TravelSpendingTest test) {
        if (test.getTestID() == null) {
            em.persist(test);  // 새로운 엔티티일 경우 persist
        } else {
            em.merge(test);  // 기존 엔티티일 경우 merge
        }
    }
}
