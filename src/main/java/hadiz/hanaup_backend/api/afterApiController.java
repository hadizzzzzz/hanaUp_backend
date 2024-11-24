package hadiz.hanaup_backend.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hadiz.hanaup_backend.ExchangeRateUtils;
import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.repository.HanaMoneyByCurrencyRepository;
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
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/after-travel")
public class afterApiController {

    @Autowired
    private final HanaMoneyByCurrencyRepository hanaMoneyByCurrencyRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ForexTechService forexTechService;

    @Autowired
    private final ForeignCurrencyAccountService foreignCurrencyAccountService;



    @GetMapping("/investment-info")
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
    @Transactional
    public InvestmentResponse getInvestmentInfo(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        User user = userService.findOne(Long.parseLong(userId));
        List<HanaMoneyByCurrency> allByUser = hanaMoneyByCurrencyRepository.findAllByUser(user);
        HanaMoneyByCurrency userpick = hanaMoneyByCurrencyRepository.findHanaMoneyByCountry(allByUser, country);


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
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
    @Transactional
    public ExchangeRateResponse getExchangeRate(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        // 오늘의 날짜 가져오기 (예시로 현재 날짜 사용)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(new Date());

        // 국가에 맞는 통화 코드 가져오기
        String currencyCode = getCurrencyCodeForCountry(country);

        // 오늘의 환율 가져오기
        BigDecimal todayExchangeRate = ExchangeRateUtils.getExchangeRates(today).getOrDefault(currencyCode, BigDecimal.valueOf(0));

        // 주별 환율 데이터 가져오기 및 특정 국가에 대한 데이터 필터링
        Map<String, Map<String, BigDecimal>> allWeeklyRates = ExchangeRateUtils.getWeeklyExchangeRates();
        Map<String, Map<String, Double>> formattedWeeklyRates = formatWeeklyRates(allWeeklyRates, currencyCode);

        // 응답 객체 생성 및 설정
        ExchangeRateResponse response = new ExchangeRateResponse();

        if ("Taiwan".equals(country)){
            response.setTodayExchangeRate(42.2);
        }
        else{
            response.setTodayExchangeRate(todayExchangeRate.doubleValue());
            response.setWeeklyExchangeRates(formattedWeeklyRates);
        }


        return response;
    }

    private String getCurrencyCodeForCountry(String country) {
        // 국가에 따른 통화 코드를 매핑하는 간단한 예시
        switch (country) {
            case "Japan":
                return "JPY(100)";
            case "Thailand":
                return "THB";
            case "Malaysia":
                return "MYR";
            case "China":
                return "CNH";
            case "USA":
                return "USD";
            case "Europe":
                return "EUR";
            case "UK":
                return "GBP";
            case "Australia":
                return "AUD";
            default:
                return "USD"; // 기본 통화 (예시)
        }
    }

    private Map<String, Map<String, Double>> formatWeeklyRates(Map<String, Map<String, BigDecimal>> allRates, String currencyCode) {
        Map<String, Map<String, Double>> formattedRates = new LinkedHashMap<>();
        int weekCounter = 1;

        for (Map.Entry<String, Map<String, BigDecimal>> entry : allRates.entrySet()) {
            String weekKey = "week" + weekCounter++;
            Map<String, BigDecimal> dailyRates = entry.getValue();
            Map<String, Double> filteredDailyRates = new LinkedHashMap<>();

            for (Map.Entry<String, BigDecimal> rateEntry : dailyRates.entrySet()) {
                if (rateEntry.getKey().startsWith(currencyCode)) {
                    // 날짜 부분만 추출하여 key로 사용
                    String dateKey = rateEntry.getKey().substring(currencyCode.length() + 1);
                    filteredDailyRates.put(dateKey, rateEntry.getValue().doubleValue());
                }
            }

            if (!filteredDailyRates.isEmpty()) {
                formattedRates.put(weekKey, filteredDailyRates);
            }
        }

        return formattedRates;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeRateResponse {
        private double todayExchangeRate;
        private Map<String, Map<String, Double>> weeklyExchangeRates;
    }

    @PostMapping("/forextech")
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
    @Transactional
    public ForexTechResponse handleForexTech(@RequestBody ForexTechRequest request) {


        // 예시로 총 금액을 계산 (현재 금액에 요청된 금액을 더하는 로직을 단순화함)
        User user = userService.findOne(Long.parseLong(request.userId));
        List<HanaMoneyByCurrency> allByUser = hanaMoneyByCurrencyRepository.findAllByUser(user);
        HanaMoneyByCurrency userpick = hanaMoneyByCurrencyRepository.findHanaMoneyByCountry(allByUser, request.country);

        forexTechService.createForexTech(Long.valueOf(request.userId), userpick.getBalance());
        double totalAmount = forexTechService.autoRecharge(Long.valueOf(request.userId), request.amount);


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
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
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
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
    @Transactional
    public MakeSavingsResponse makeSavings(@RequestBody MakeSavingsRequest request) {

        ForeignCurrencyAccount account = foreignCurrencyAccountService.createAccount(Long.valueOf(request.userId), request.amount, request.month);

        // 예시 원금 및 이자율 설정
        double originalAmount = account.getFirstBalance();


        // 응답 생성
        MakeSavingsResponse response = new MakeSavingsResponse();
        response.setCountry(request.getCountry());
        response.setOriginalAmount(originalAmount);

        List<HanaMoneyByCurrency> allByUser = hanaMoneyByCurrencyRepository.findAllByUser(userService.findOne(Long.valueOf(request.userId)));
        HanaMoneyByCurrency hanamoney = hanaMoneyByCurrencyRepository.findHanaMoneyByCountry(allByUser, request.country);
        hanaMoneyByCurrencyRepository.delete(hanamoney);

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

    @PostMapping("/deletesavings")
    @CrossOrigin(origins = "https:/hanaup.vercel.app")
    @Transactional
    public DeleteSavingsResponse deleteSavings(@RequestBody DeleteSavingsRequest request) {

        ForeignCurrencyAccount account = foreignCurrencyAccountService.findOne(Long.valueOf(request.userId));

        BigDecimal finalAmount = foreignCurrencyAccountService.calculateInterest(Long.valueOf(request.userId));
        BigDecimal interestAmount = account.getInterest();

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
