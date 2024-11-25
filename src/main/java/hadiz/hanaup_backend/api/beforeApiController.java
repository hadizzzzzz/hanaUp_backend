package hadiz.hanaup_backend.api;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.TravelSpendingTestDTO.AnswerDTO;
import hadiz.hanaup_backend.service.ExchangeRateService;
import hadiz.hanaup_backend.service.HanaMoneyByCurrencyService;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.beforeservice.PastTravelCostService;
import hadiz.hanaup_backend.service.beforeservice.TravelSpendingTestService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://hanaup.vercel.app", "http://localhost:5173"})
@RequestMapping("/api/before-travel")
@RequiredArgsConstructor
public class beforeApiController {

    @Autowired
    private final TravelSpendingTestService travelSpendingTestService;

    @Autowired
    private final PastTravelCostService pastTravelCostService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final HanaMoneyByCurrencyService hanaMoneyByCurrencyService;

    @Autowired
    private final ExchangeRateService exchangeRateService;


    @PostMapping("/type-test")
    @Transactional
    public ResponseEntity<TypeTestResponse> handleTypeTest(@RequestBody TypeTestRequest request) throws Exception {

        List<AnswerDTO> answers = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : request.getAnswers().entrySet()) { // request.getAnswers()로 수정
            String indicator = entry.getKey(); // 예: "E/I", "F/T" 등
            List<String> responses = entry.getValue();

            for (String response : responses) {
                AnswerDTO answer = new AnswerDTO();
                answer.setIndicator(indicator);

                // 점수 규칙에 따라 score 설정 (예: E/F -> +1, I/T -> -1)
                if (response.equals("E") || response.equals("F") || response.equals("ME") || response.equals("J")) {
                    answer.setScore(1);
                } else {
                    answer.setScore(-1);
                }

                answers.add(answer);
            }
        }

        // 입력된 answers를 기반으로 결과 유형을 계산함
        String resultType = travelSpendingTestService.calculateMbti(answers);


        // 예상 비용 계산 String country, int duration 원화 반환
        double estimatedCost = pastTravelCostService.predictTravelCost(
                request.getDestination(), Integer.parseInt(request.getDuration()));

        int estimatedCostInt = (int) estimatedCost;
        // 응답 생성
        TypeTestResponse response = new TypeTestResponse();
        response.setResultType(resultType);
        response.setEstimatedCost(estimatedCostInt);
        response.setCurrency(request.getCurrency());

        User user = userService.findOne(Long.parseLong(request.userId));

        HanaMoneyByCurrency hanaMoneyByCurrency  = new HanaMoneyByCurrency();
        hanaMoneyByCurrency.setUser(user);
        hanaMoneyByCurrency.setCurrencyID(request.currency);
        hanaMoneyByCurrency.setCountry(request.destination);

        // 환율 찾기
        LocalDate today = LocalDate.now();
        List<ExchangeRateService.ExchangeRateDto> todayRates = exchangeRateService.getExchangeRatesForDate(today);

        ExchangeRateService.ExchangeRateDto nowRate = todayRates.stream()
                .filter(rate -> rate.getCurrCD().equalsIgnoreCase(request.currency))
                .findFirst()
                .orElse(null);

        double balance = estimatedCost / nowRate.getBasicRate();


        hanaMoneyByCurrency.setBalance(balance);

        hanaMoneyByCurrencyService.saveOrUpdateHanaMoney(hanaMoneyByCurrency);



        //여행 상태 변경
        user.setTravelState("during");

        return ResponseEntity.ok(response);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TypeTestRequest {
        private String userId;
        private String destination;
        private String currency;
        private String duration;
        private Map<String, List<String>> answers;

        // Getters and Setters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TypeTestResponse {
        private String resultType;
        private int estimatedCost;
        private String currency;

        // Getters and Setters
    }


    @PostMapping("/estimate-cost")
    @Transactional
    public ResponseEntity<TravelCostResponse> estimateTravelCost(@RequestBody TravelCostRequest request) throws Exception {

        User user = userService.findOne(Long.parseLong(request.userId));


        HanaMoneyByCurrency hanaMoneyByCurrency  = new HanaMoneyByCurrency();
        hanaMoneyByCurrency.setUser(user);
        hanaMoneyByCurrency.setCurrencyID(request.currency);
        hanaMoneyByCurrency.setCountry(request.destination);



        //여행 상태 변경
        user.setTravelState("during");

        // 원화로 반환
        double estimatedCost = pastTravelCostService.predictTravelCost(
                request.getDestination(), Integer.parseInt(request.getDuration()));

        int estimatedCostInt = (int) estimatedCost;

        // 응답 생성
        TravelCostResponse response = new TravelCostResponse();
        response.setEstimatedCost(estimatedCostInt);
        response.setCurrency(request.getCurrency());


        // 환율 찾기
        LocalDate today = LocalDate.now();
        List<ExchangeRateService.ExchangeRateDto> todayRates = exchangeRateService.getExchangeRatesForDate(today);

        ExchangeRateService.ExchangeRateDto nowRate = todayRates.stream()
                .filter(rate -> rate.getCurrCD().equalsIgnoreCase(request.currency))
                .findFirst()
                .orElse(null);

        double balance = estimatedCost / nowRate.getBasicRate();

        hanaMoneyByCurrency.setBalance(balance);

        hanaMoneyByCurrencyService.saveOrUpdateHanaMoney(hanaMoneyByCurrency);

        return ResponseEntity.ok(response);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TravelCostRequest {
        private String userId;
        private String destination;
        private String currency;
        private String duration;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TravelCostResponse {
        private int estimatedCost;
        private String currency;
    }
}
