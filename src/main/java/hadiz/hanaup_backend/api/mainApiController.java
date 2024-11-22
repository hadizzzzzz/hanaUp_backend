package hadiz.hanaup_backend.api;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.repository.HanaMoneyByCurrencyRepository;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.afterservice.ForeignCurrencyAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hadiz.hanaup_backend.ExchangeRateUtils.getExchangeRates;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class mainApiController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ForeignCurrencyAccountService foreignCurrencyAccountService;

    @Autowired
    private HanaMoneyByCurrencyRepository hanaMoneyByCurrencyRepository;


    @GetMapping("/travel-status")
    public TravelStatusResponse travelStatus(@RequestParam(value = "id", required = false) Long id) {
        User user;

        if (id == null) {
            // 새로운 유저 생성 로직
            user = new User();
            Long newId = userService.join(user); // 새로운 유저 생성 및 가입
            user.setTravelState("before");

            // 기본 여행 정보 생성 (일본)

            // japan.setTotalspent
            HanaMoneyByCurrency japanTravel = new HanaMoneyByCurrency();
            japanTravel.setUser(user);
            japanTravel.setCountry("Japan");
            japanTravel.setCurrencyID("JPY");
            japanTravel.setBalance(5548.10); // 기본 잔액 예시값 설정 (엔화 / 5만원)
            hanaMoneyByCurrencyRepository.saveCustom(japanTravel);

            // 기본 여행 정보 생성 (미국)

            // usa.setTotalspent
            HanaMoneyByCurrency usaTravel = new HanaMoneyByCurrency();
            usaTravel.setUser(user);
            usaTravel.setCountry("USA");
            usaTravel.setCurrencyID("USD");
            usaTravel.setBalance(71.86); // 기본 잔액 예시값 설정 (달러 / 10만원)
            hanaMoneyByCurrencyRepository.saveCustom(usaTravel);
        } else {
            // 기존 유저 조회 로직
            user = userService.findOne(id);
        }
        return new TravelStatusResponse(user.getTravelState(), String.valueOf(user.getUserID()));
    }

    @Data
    @AllArgsConstructor
    static class TravelStatusResponse{
        private String travelStatus;
        private String uid;
    }

    @GetMapping("/fund-info/{id}")
    public FundInfoResponse FundInfo(@PathVariable("id") Long id) {
        // 유저 정보 조회
        User user = userService.findOne(id);

        // 오늘 날짜
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 1일 전 날짜
        LocalDate tenDaysAgo = today.minusDays(1);
        String formattedDateTenDaysAgo = tenDaysAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Map<String, BigDecimal> nowRates = getExchangeRates(formattedDate);
        Map<String, BigDecimal> beforeRates = getExchangeRates(formattedDateTenDaysAgo);

        // DB에서 모든 CountryFund 조회
        List<FundInfoResponse.CountryFund> countryFunds = hanaMoneyByCurrencyRepository.findAllByUser(user)
                .stream()
                .map(hanaMoney -> {
                    BigDecimal nowRate;
                    BigDecimal beforeRate;

                    // 문자열 비교에서 .equals() 사용
                    if ("TWD".equals(hanaMoney.getCurrencyID())) {
                        nowRate = BigDecimal.valueOf(42.2);
                        beforeRate = BigDecimal.valueOf(42.2);
                    } else if ("PHP".equals(hanaMoney.getCurrencyID())) {
                        nowRate = BigDecimal.valueOf(23.7);
                        beforeRate = BigDecimal.valueOf(23.7);
                    } else {
                        nowRate = nowRates.get(hanaMoney.getCurrencyID());
                        beforeRate = beforeRates.get(hanaMoney.getCurrencyID());
                    }

                    String trend;

                    // nowRate와 beforeRate 비교하여 trend 결정
                    if (nowRate != null && beforeRate != null) {
                        if (nowRate.compareTo(beforeRate) >= 0) {
                            trend = "up";
                        } else {
                            trend = "down";
                        }
                    } else {
                        trend = "up"; // null 값이 있을 경우 기본값 설정
                    }

                    return new FundInfoResponse.CountryFund(
                            hanaMoney.getCountry(),
                            hanaMoney.getCurrencyID(),
                            hanaMoney.getBalance(),
                            new FundInfoResponse.ExchangeRate(nowRate, beforeRate, trend)
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

        // CountryFunds만 초기화하는 생성자
        public FundInfoResponse(List<CountryFund> countryFunds) {
            this.countryFunds = countryFunds;
        }
    }


}

