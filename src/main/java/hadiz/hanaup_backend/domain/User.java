package hadiz.hanaup_backend.domain;

import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import hadiz.hanaup_backend.domain.before.TravelSpendingTest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users")  // 테이블 이름을 'users'로 변경
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String email;
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TravelLog> travelLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TravelSpendingTest> spendingTests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PastTravelCostPrediction> pastPredictions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ForeignCurrencyAccount> foreignCurrencyAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HanaMoneyByCurrency> hanaMoneyByCurrencies = new ArrayList<>();
}
