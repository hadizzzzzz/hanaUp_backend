package hadiz.hanaup_backend.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.domain.after.ForexTech;
import hadiz.hanaup_backend.service.ExchangeRateService;
import hadiz.hanaup_backend.service.HanaMoneyByCurrencyService;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.afterservice.ForeignCurrencyAccountService;
import hadiz.hanaup_backend.service.afterservice.ForexTechService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin(origins = {"https://hanaup.vercel.app", "http://localhost:5173"})
@RequiredArgsConstructor
@RequestMapping("/api/after-travel")
public class afterApiController {

    @Autowired
    private final HanaMoneyByCurrencyService hanaMoneyByCurrencyService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ForexTechService forexTechService;

    @Autowired
    private final ForeignCurrencyAccountService foreignCurrencyAccountService;

    @Autowired
    private final ExchangeRateService exchangeRateService;

    @GetMapping("/investment-info")
    @Transactional
    public InvestmentResponse getInvestmentInfo(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        User user = userService.findOne(Long.parseLong(userId));
        HanaMoneyByCurrency userpick = hanaMoneyByCurrencyService.getHanaMoneyByCountry(user, country);

        // 여행 후에 남은 금액으로 변경
        double remainCost = 0;
        if (country.equals("Thailand")){
            remainCost = 1242.54;
        }
        if (country.equals("Malaysia")){
            remainCost = 321.19;
        }
        if (country.equals("China")){
            remainCost = 519.91;
        }
        if (country.equals("Taiwan")){
            remainCost = 1165.23;
        }
        if (country.equals("UK")){
            remainCost = 56.7;
        }
        if (country.equals("Australia")){
            remainCost = 110.38;
        }
        if (country.equals("Philippines")){
            remainCost = 2107.04;
        }
        if (country.equals("Europe")){
            remainCost = 67.88;
        }
        if (country.equals("USA")){
            remainCost = 71.86;
        }
        if (country.equals("Japan")){
            remainCost = 5548.1;
        }

        userpick.setBalance(remainCost);


        // 예시 데이터 설정
        InvestmentResponse response = new InvestmentResponse();
        response.setBalance(userpick.getBalance()); // 잔액
        response.setCountry(country);

        // 금리 설정 (국가에 따라 일본 금리와 한국 금리 구분)
        if (country.equals("Thailand")) {
            response.setInterestRate(2.5);
        }
        if (country.equals("Malaysia")){
            response.setInterestRate(3);
        }
        if (country.equals("China")){
            response.setInterestRate(3.1);
        }
        if (country.equals("Taiwan")){
            response.setInterestRate(2);
        }
        if (country.equals("UK")){
            response.setInterestRate(5);
        }
        if (country.equals("Australia")){
            response.setInterestRate(4.35);
        }
        if (country.equals("Philippines")){
            response.setInterestRate(6.25);
        }
        if (country.equals("Europe")){
            response.setInterestRate(3.4);
        }
        if (country.equals("USA")){
            response.setInterestRate(5);
        }
        if (country.equals("Japan")){
            response.setInterestRate(0.25);
        }

        // 투자 유형 설정

        if ("Japan".equals(country) || "Thailand".equals(country) || "Malaysia".equals(country) ||
        "China".equals(country) || "Taiwan".equals(country)){
            response.setInvestmentType("환테크");
        }
        else {
            response.setInvestmentType("외화 예금");
        }

        return response;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvestmentResponse {
        private double balance;
        private String country;
        private double interestRate;
        private String investmentType;
    }


    @GetMapping("/exchange-rate")
    @Transactional
    public ExchangeRateResponse getExchangeRate(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) throws Exception {

        // 오늘의 날짜 가져오기 (예시로 현재 날짜 사용)
        LocalDate today = LocalDate.now();

        // 국가에 맞는 통화 코드 가져오기
        String currency = getCurrencyCodeForCountry(country);

        // 오늘의 환율 가져오기
        List<ExchangeRateService.ExchangeRateDto> todayRates = exchangeRateService.getExchangeRatesForDate(today);

        ExchangeRateService.ExchangeRateDto todayRate = todayRates.stream()
                .filter(rate -> rate.getCurrCD().equalsIgnoreCase(currency))
                .findFirst()
                .orElse(null);

        // 응답 객체 생성 및 설정
        ExchangeRateResponse response = new ExchangeRateResponse();

        response.setTodayExchangeRate(todayRate.getBasicRate());



        return response;
    }

    private String getCurrencyCodeForCountry(String country) {
        // 국가에 따른 통화 코드를 매핑하는 간단한 예시
        switch (country) {
            case "Japan":
                return "JPY";
            case "Thailand":
                return "THB";
            case "Malaysia":
                return "MYR";
            case "China":
                return "CNY";
            case "Taiwan":
                return "TWD";
            case "USA":
                return "USD";
            case "Europe":
                return "EUR";
            case "UK":
                return "GBP";
            case "Australia":
                return "AUD";
            case "Philippines":
                return "PHP";
            default:
                return "USD"; // 기본 통화 (예시)
        }
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeRateResponse {
        private double todayExchangeRate;
    }

    @PostMapping("/forextech")
    @Transactional
    public ForexTechResponse handleForexTech(@RequestBody ForexTechRequest request) {


        // 예시로 총 금액을 계산 (현재 금액에 요청된 금액을 더하는 로직을 단순화함)
        User user = userService.findOne(Long.parseLong(request.userId));
        HanaMoneyByCurrency userpick = hanaMoneyByCurrencyService.getHanaMoneyByCountry(user, request.country);
        ForexTech forexTech = forexTechService.createForexTech(Long.valueOf(request.userId), userpick.getBalance());
        double totalAmount = forexTechService.autoRecharge(forexTech, request.amount);


        // 응답 생성
        ForexTechResponse response = new ForexTechResponse();
        response.setCountry(request.getCountry());
        response.setTotalAmount(totalAmount);

        userpick.setBalance(totalAmount);

        return response;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForexTechRequest {
        private String userId;
        private String country;
        private double amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForexTechResponse {
        private String country;
        private double totalAmount;
    }

    @GetMapping("/interest-rate")
    @Transactional
    public InterestRateResponse getInterestRate(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        // 예시 금리 데이터를 설정
        InterestRateResponse response = new InterestRateResponse();
        response.setCountry(country);

        // 예시 금리 값 설정
        if ("USA".equalsIgnoreCase(country) || "UK".equalsIgnoreCase(country)) {
            response.setOneMonthRate(2.27);
            response.setSixMonthRate(3.79);
            response.setOneYearRate(5.00);
        }
        else if ("Australia".equalsIgnoreCase(country)) {
            response.setOneMonthRate(1.98);
            response.setSixMonthRate(3.3);
            response.setOneYearRate(4.35);
        }

        else if ("Philippines".equalsIgnoreCase(country)) {
            response.setOneMonthRate(2.84);
            response.setSixMonthRate(4.73);
            response.setOneYearRate(6.25);
        }
        else if ("Europe".equalsIgnoreCase(country)) {
            response.setOneMonthRate(1.55);
            response.setSixMonthRate(2.58);
            response.setOneYearRate(3.4);
        }

        else {
            // 기본 예시 데이터 설정
            response.setOneMonthRate(1.00);
            response.setSixMonthRate(2.00);
            response.setOneYearRate(3.00);
        }

        return response;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterestRateResponse {
        private String country;

        @JsonProperty("1개월")
        private double oneMonthRate;

        @JsonProperty("6개월")
        private double sixMonthRate;

        @JsonProperty("1년")
        private double oneYearRate;
    }

    @PostMapping("/makesavings")
    @Transactional
    public MakeSavingsResponse makeSavings(@RequestBody MakeSavingsRequest request) {

        ForeignCurrencyAccount account = foreignCurrencyAccountService.createAccount(Long.valueOf(request.userId), request.amount, request.month);
        account.setCountry(request.getCountry());
        account.setCurrencyID(getCurrencyCodeForCountry(request.country));


        // 예시 원금 및 이자율 설정
        double originalAmount = account.getFirstBalance();
        BigDecimal finalAmount = foreignCurrencyAccountService.calculateInterest(Long.valueOf(request.userId), request.country);

        // 응답 생성
        MakeSavingsResponse response = new MakeSavingsResponse();
        response.setCountry(request.getCountry());
        response.setOriginalAmount(originalAmount);

        User user = userService.findOne(Long.valueOf(request.userId));

        HanaMoneyByCurrency hanamoney = hanaMoneyByCurrencyService.getHanaMoneyByCountry(user, request.country);
        hanaMoneyByCurrencyService.deleteHanaMoney(hanamoney);

        return response;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MakeSavingsRequest {
        private String userId;
        private String country;
        private double amount;
        private int month;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MakeSavingsResponse {
        private String country;
        private double originalAmount;
//        private BigDecimal interestAmount;
//        private BigDecimal finalAmount;
    }

    @GetMapping("/savings-info")
    public SavingsInfoResponse savingsInfo(@RequestParam("userId") String userId,
                                           @RequestParam("country") String country){
        ForeignCurrencyAccount account = foreignCurrencyAccountService.findOne(Long.valueOf(userId));

        SavingsInfoResponse response = new SavingsInfoResponse();
        response.setCountry(country);
        response.setOriginalAmount(account.getFirstBalance());
        response.setInterestAmount(account.getInterest());
        response.setFinalAmount(account.getLastBalance());

        return response;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SavingsInfoResponse {
        private String country;
        private double originalAmount;
        private BigDecimal interestAmount;
        private BigDecimal finalAmount;
    }

    @PostMapping("/deletesavings")
    @Transactional
    public DeleteSavingsResponse deleteSavings(@RequestBody DeleteSavingsRequest request) {

        ForeignCurrencyAccount account = foreignCurrencyAccountService.findOne(Long.valueOf(request.userId));
        BigDecimal interestAmount = account.getInterest();
        BigDecimal finalAmount = account.getLastBalance();


        // 응답 생성
        DeleteSavingsResponse response = new DeleteSavingsResponse();
        response.setCountry(request.getCountry());
        response.setInterestAmount(interestAmount);
        response.setFinalAmount(finalAmount);

        foreignCurrencyAccountService.delete(account);

        return response;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteSavingsRequest {
        private String userId;
        private String country;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteSavingsResponse {
        private String country;
        private BigDecimal interestAmount;
        private BigDecimal finalAmount;
    }

}
