package hadiz.hanaup_backend.repository;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TravelLogRepository {
    private final EntityManager em;

    public List<TravelLog> findByUserId(Long userId) {
        return em.createQuery("select t from TravelLog t where t.user.userID = :userId", TravelLog.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public TravelLog findOne(Long travelId){
        return em.find(TravelLog.class, travelId);
    }

    public void save(TravelLog travelLog) {
        if (travelLog.getLogID() == null) {
            em.persist(travelLog);
        } else {
            em.merge(travelLog);
        }
    }
}

