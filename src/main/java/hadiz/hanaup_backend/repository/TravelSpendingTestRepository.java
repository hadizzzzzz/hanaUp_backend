package hadiz.hanaup_backend.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TravelSpendingTestRepository {

    private final EntityManager em;
}
