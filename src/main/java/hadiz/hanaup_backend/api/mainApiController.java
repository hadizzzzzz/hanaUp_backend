package hadiz.hanaup_backend.api;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.service.ExchangeRateService;
import hadiz.hanaup_backend.service.HanaMoneyByCurrencyService;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.afterservice.ForeignCurrencyAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = {"https://hanaup.vercel.app", "http://localhost:5173"})
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class mainApiController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ForeignCurrencyAccountService foreignCurrencyAccountService;

    @Autowired
    private HanaMoneyByCurrencyService hanaMoneyByCurrencyService;

    @Autowired
    private ExchangeRateService exchangeRateService;


    @GetMapping("/travel-status")
    @Transactional
    public TravelStatusResponse travelStatus(@RequestParam(value = "userId", required = false) Long userId) {
        User user;

        if (userId == null) {
            // 새로운 유저 생성 로직
            user = new User();
            user.setTravelState("before");
            userService.join(user);

            // 기본 여행 정보 생성 (일본)
            HanaMoneyByCurrency japanTravel = new HanaMoneyByCurrency();
            japanTravel.setUser(user);
            japanTravel.setCountry("Japan");
            japanTravel.setCurrencyID("JPY");
            japanTravel.setBalance(5548.10); // 기본 잔액 예시값 설정 (엔화 / 5만원)
            hanaMoneyByCurrencyService.saveOrUpdateHanaMoney(japanTravel);

            // 기본 여행 정보 생성 (미국)
            HanaMoneyByCurrency usaTravel = new HanaMoneyByCurrency();
            usaTravel.setUser(user);
            usaTravel.setCountry("USA");
            usaTravel.setCurrencyID("USD");
            usaTravel.setBalance(71.86); // 기본 잔액 예시값 설정 (달러 / 10만원)
            hanaMoneyByCurrencyService.saveOrUpdateHanaMoney(usaTravel);
        } else {
            // 기존 유저 조회 로직
            user = userService.findOne(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found for ID: " + userId);
            }
        }
        // userId와 user의 ID를 반환하도록 수정
        return new TravelStatusResponse(user.getTravelState(), String.valueOf(user.getUserID()));
    }

    @Data
    @AllArgsConstructor
    static class TravelStatusResponse{
        private String travelStatus;
        private String uid;
    }

    @GetMapping("/fund-info")
    @Transactional
    public FundInfoResponse FundInfo(@RequestParam("userId") Long id) throws Exception {
        // 유저 정보 조회
        User user = userService.findOne(id);

        // 오늘 날짜와 1일 전 날짜
        LocalDate today = LocalDate.now();
        LocalDate oneDaysAgo = today.minusDays(1);

        // 환율 정보 가져오기
        List<ExchangeRateService.ExchangeRateDto> todayRates = exchangeRateService.getExchangeRatesForDate(today);
        List<ExchangeRateService.ExchangeRateDto> beforeRates = exchangeRateService.getExchangeRatesForDate(oneDaysAgo);

        // DB에서 유저의 모든 CountryFund 조회 및 매핑
        List<FundInfoResponse.CountryFund> countryFunds = hanaMoneyByCurrencyService.getAllHanaMoneyByUser(user)
                .stream()
                .map(hanaMoney -> {
                    String currencyID = hanaMoney.getCurrencyID();

                    // 오늘의 환율 찾기
                    ExchangeRateService.ExchangeRateDto nowRate = todayRates.stream()
                            .filter(rate -> rate.getCurrCD().equalsIgnoreCase(currencyID))
                            .findFirst()
                            .orElse(null);

                    // 어제의 환율 찾기
                    ExchangeRateService.ExchangeRateDto beforeRate = beforeRates.stream()
                            .filter(rate -> rate.getCurrCD().equalsIgnoreCase(currencyID))
                            .findFirst()
                            .orElse(null);

                    // 환율 트렌드 결정
                    String trend;
                    if (nowRate != null && beforeRate != null) {
                        if (Double.compare(nowRate.getBasicRate(), beforeRate.getBasicRate()) >= 0) {
                            trend = "up";
                        } else {
                            trend = "down";
                        }
                    } else {
                        trend = "up"; // null 값이 있을 경우 기본값 설정
                    }

                    // CountryFund 객체 반환
                    return new FundInfoResponse.CountryFund(
                            hanaMoney.getCountry(),
                            hanaMoney.getCurrencyID(),
                            hanaMoney.getBalance(),
                            new FundInfoResponse.ExchangeRate(
                                    nowRate != null ? BigDecimal.valueOf(nowRate.getBasicRate()) : null,
                                    beforeRate != null ? BigDecimal.valueOf(beforeRate.getBasicRate()) : null,
                                    trend
                            )
                    );
                })
                .collect(Collectors.toList());

        // Foreign Savings 정보 확인 및 반환
        ForeignCurrencyAccount foreignSavings = foreignCurrencyAccountService.findOne(id); // foreignSavings 생성 로직 예시
        if (foreignSavings != null) {
            FundInfoResponse.ForeignSavings savings = new FundInfoResponse.ForeignSavings(
                    foreignSavings.getLastBalance(),
                    foreignSavings.getCountry(),
                    foreignSavings.getCurrencyID()
            );
            return new FundInfoResponse(savings, countryFunds);
        }

        return new FundInfoResponse(countryFunds);
    }




    @Data
    @AllArgsConstructor
    static class FundInfoResponse {
        private ForeignSavings foreignSavings;
        private List<CountryFund> countryFunds;
        private double remainMoney;

        // CountryFund 정의
        @Data
        @AllArgsConstructor
        public static class CountryFund {
            private String country;
            private String currency;
            private double balance;
            private ExchangeRate exchangeRate;
        }

        // ExchangeRate 정의
        @Data
        @AllArgsConstructor
        public static class ExchangeRate {
            private BigDecimal rate;
            private BigDecimal previousRate;
            private String trend; // "up" 또는 "down"
        }

        // ForeignSavings 정의 (ExchangeRate 제거)
        @Data
        @AllArgsConstructor
        public static class ForeignSavings {
            private BigDecimal totalAmount;
            private String country;
            private String currency;
        }


        // remainMoney 계산 포함하는 생성자
        public FundInfoResponse(ForeignSavings foreignSavings, List<CountryFund> countryFunds) {
            this.foreignSavings = foreignSavings;
            this.countryFunds = countryFunds;

            // remainMoney 계산: exchangeRate * balance 합산 (1 단위 외화 기준 변환)
            this.remainMoney = countryFunds.stream()
                    .filter(fund -> fund.getExchangeRate().getRate() != null) // 유효한 환율만 포함
                    .mapToDouble(fund -> {
                        double rate = fund.getExchangeRate().getRate().doubleValue(); // 환율 가져오기
                        String currency = fund.getCurrency(); // 통화 코드 가져오기

                        // 특정 통화의 1 단위 기준 환율 계산
                        if ("JPY".equals(currency)) {
                            rate /= 100; // 일본 엔화는 100 단위 기준이므로 1 단위로 변경
                        }
                        // 다른 통화는 기본 1 단위 기준 환율 사용
                        return fund.getBalance() * rate;
                    })
                    .sum();
        }

        // CountryFunds만 초기화하는 생성자
        public FundInfoResponse(List<CountryFund> countryFunds) {
            this(null, countryFunds);
        }

    }

    @DeleteMapping("/delete-user")
    @Transactional
    public String deleteUser(@RequestParam("userId") Long userId) {
        User user = userService.findOne(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for ID: " + userId);
        }

        // 사용자 삭제
        userService.deleteUser(userId);

        return "User and associated data deleted successfully.";
    }


}

