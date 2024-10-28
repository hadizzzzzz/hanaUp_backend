package hadiz.hanaup_backend.domain.before;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "past_travel_cost_prediction")
@Getter @Setter
public class PastTravelCostPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long predictionID;

    private String country;
    private int travelDuration;
    private String travelType;
    private double predictedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_log_id")
    private TravelLog travelLog;

    // Getters and Setters
}


