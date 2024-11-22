package hadiz.hanaup_backend.domain.before;

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
    private double predictedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}


