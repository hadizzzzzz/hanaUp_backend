package hadiz.hanaup_backend.domain.before;

import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "travel_spending_test")
public class TravelSpendingTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testID;

    private LocalDateTime testDate;
    private String spendingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // 'user' 예약어를 피하기 위해 'user_id'로 변경
    private User user;

    @OneToOne(mappedBy = "travelSpendingTest", fetch = FetchType.LAZY)
    private TravelCostPrediction travelCostPrediction;

    // Getters and Setters
}

