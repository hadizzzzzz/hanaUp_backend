package hadiz.hanaup_backend.api;

import hadiz.hanaup_backend.domain.DailyExpenseReport;
import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.HanaMoneyByCurrencyRepository;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.beforeservice.DailyExpenseReportService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/during-travel")
public class duringApiController {

    @Autowired
    private final DailyExpenseReportService dailyExpenseReportService;

    @Autowired
    private final UserService userService;


    @GetMapping("/daily-report")
    public List<DailyReportResponse> getDailyReport(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        List<DailyReportResponse> responses = new ArrayList<>();

        // 1일차부터 3일차까지 데이터 가져오기
        for (int day = 1; day <= 3; day++) {
            // db에서 각 일차에 대한 데일리 리포트 가져오기
            DailyExpenseReport report = dailyExpenseReportService.getAllByCountryAndDay(country, day);

            // 응답 객체 생성
            DailyReportResponse response = new DailyReportResponse();
            response.setDay(day);
            response.setTotalSpent(report.getTotalSpent()); //totalspent는 외국 돈으로 넣기

            // Breakdown 데이터 설정
            Map<String, Integer> breakdown = new HashMap<>();
            breakdown.put("transport", report.getTransportExpense());
            breakdown.put("food", report.getFoodExpense());
            breakdown.put("hotel", report.getHotelExpense());
            breakdown.put("shopping", report.getShoppingExpense());
            breakdown.put("activities", report.getActivityExpense());

            response.setBreakdown(breakdown);

            // 수수료 0.0175
            int fee = (int) (report.getTotalSpent() * 0.0175);
            response.setFeeSavings(fee);


            // 응답 리스트에 추가
            responses.add(response);
        }

        return responses;
    }

    @GetMapping("/final-report")
    public DailyReportResponse getFinalReport(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {

        // 데일리 리포트는 항상 3일차를 가져옴
        DailyReportResponse response = new DailyReportResponse();

        // db에 저장된 레포트 가져오기
        DailyExpenseReport report = dailyExpenseReportService.getAllByCountryAndDay(country, 4);

        // 총액 설정
        response.setTotalSpent(report.getTotalSpent());

        // Breakdown 데이터 설정
        Map<String, Integer> breakdown = new HashMap<>();
        breakdown.put("transport", report.getTransportExpense());
        breakdown.put("food", report.getFoodExpense());
        breakdown.put("hotel", report.getHotelExpense());
        breakdown.put("shopping", report.getShoppingExpense());
        breakdown.put("activities", report.getActivityExpense());

        response.setBreakdown(breakdown);

        // 수수료 0.0175
        int fee = (int) (report.getTotalSpent() * 0.0175);
        response.setFeeSavings(fee);

        User user = userService.findOne(Long.parseLong(userId));
        user.setTravelState("after");

        // 선택 국가 내용 자금 정보에 넣는 코드
        HanaMoneyByCurrency hanaMoneyByCurrency = new HanaMoneyByCurrency();
        hanaMoneyByCurrency.setBalance((double) report.getTotalSpent());
        hanaMoneyByCurrency.setUser(user);
        hanaMoneyByCurrency.setCountry(report.getCountry());


        return response;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class DailyReportResponse {
        private int day;
        private int totalSpent;
        private Map<String, Integer> breakdown;
        private int feeSavings;
    }


}


