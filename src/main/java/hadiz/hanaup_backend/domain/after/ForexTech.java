package hadiz.hanaup_backend.domain.after;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "forex_tech")
@Getter @Setter
public class ForexTech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forexTechID;  // 환테크 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 사용자 ID (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hana_money_id", nullable = false)
    private HanaMoneyByCurrency hanaMoney;  // 하나머니 ID (Foreign Key)

    private Double targetExchangeRate;  // 목표 환율
    private Double currentExchangeRate;  // 현재 환율

    private String currencyID;
    private Double Balance;

    private boolean isActive; // 계좌 활성 상태
}
