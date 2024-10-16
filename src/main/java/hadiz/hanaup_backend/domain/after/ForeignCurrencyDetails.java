package hadiz.hanaup_backend.domain.after;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "foreign_currency_details")
public class ForeignCurrencyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyID;

    private String currencyName;
    private Double balance;
    private Double interestRate;
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private ForeignCurrencyAccount foreignCurrencyAccount;

    // Getters and Setters
}


