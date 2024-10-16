package hadiz.hanaup_backend.domain.before;

import hadiz.hanaup_backend.domain.before.TravelSpendingTest;
import jakarta.persistence.*;

@Entity
@Table(name = "travel_cost_prediction")
public class TravelCostPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long predictionID;

    private String country;
    private int travelDuration;
    private String travelType;
    private Double predictedAmount;

    @OneToOne
    @JoinColumn(name = "test_id")
    private TravelSpendingTest travelSpendingTest;

    // Getters and Setters
}

