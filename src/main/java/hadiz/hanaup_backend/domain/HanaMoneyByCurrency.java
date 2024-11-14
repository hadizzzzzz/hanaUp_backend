package hadiz.hanaup_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "hana_money_by_currency")
@Getter @Setter
public class HanaMoneyByCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hanaMoneyID;

    private String currencyID;
    private String country;
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
}

