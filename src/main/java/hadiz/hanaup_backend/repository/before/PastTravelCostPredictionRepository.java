package hadiz.hanaup_backend.repository.before;

import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PastTravelCostPredictionRepository {

    private final EntityManager em;

    public void save(PastTravelCostPrediction prediction) {
        if (prediction.getPredictionID() == null) {
            em.persist(prediction);  // 새로운 엔티티일 경우 persist
        } else {
            em.merge(prediction);  // 기존 엔티티일 경우 merge
        }
    }
}
