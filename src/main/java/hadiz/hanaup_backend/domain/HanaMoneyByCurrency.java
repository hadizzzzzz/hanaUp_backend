package hadiz.hanaup_backend.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hana_money_by_currency")
public class HanaMoneyByCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hanaMoneyID;

    private String currencyID;
    private Double balance;
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
}

