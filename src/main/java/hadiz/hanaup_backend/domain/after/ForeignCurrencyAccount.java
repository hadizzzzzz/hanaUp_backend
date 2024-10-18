package hadiz.hanaup_backend.domain.after;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyDetails;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foreign_currency_account")
public class ForeignCurrencyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountID;

    private String accountNumber;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "foreignCurrencyAccount", cascade = CascadeType.ALL)
    private List<ForeignCurrencyDetails> foreignCurrencyDetailsList = new ArrayList<>();

    // Getters and Setters
}

