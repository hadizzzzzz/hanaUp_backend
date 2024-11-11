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
    private Double buyexchangerate; //구매한 환율 (과거 환율, 10일 전 데이터?)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
}

