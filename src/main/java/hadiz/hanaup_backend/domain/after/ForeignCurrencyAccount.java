package hadiz.hanaup_backend.domain.after;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "foreign_currency_account")
@Getter @Setter
public class ForeignCurrencyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountID;

    private LocalDate expirationDate = LocalDate.of(2024, 12, 31);
    private int period;
    private boolean isActive; // 계좌 활성 상태

    private String currencyID;
    private Double firstBalance;
    private BigDecimal interest;
    private BigDecimal lastBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hana_money_id", nullable = false)
    private HanaMoneyByCurrency hanaMoney;  // 하나머니 ID (Foreign Key)

    // '미국'은 객체를 하나 미리 생성해서 박아두기
}

