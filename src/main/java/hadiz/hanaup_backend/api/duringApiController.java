package hadiz.hanaup_backend.api;

import hadiz.hanaup_backend.domain.DailyExpenseReport;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.service.UserService;
import hadiz.hanaup_backend.service.beforeservice.DailyExpenseReportService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://hanaup.vercel.app", "http://localhost:5173"})
@RequiredArgsConstructor
@RequestMapping("/api/during-travel")
public class duringApiController {

    @Autowired
    private final DailyExpenseReportService dailyExpenseReportService;

    @Autowired
    private final UserService userService;


    @GetMapping("/daily-report")
    @Transactional
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
            int fee = (int) (report.getTotalSpent_won() * 0.0175);
            response.setFeeSavings(fee);


            // 응답 리스트에 추가
            responses.add(response);
        }

        return responses;
    }

    @GetMapping("/final-report")
    @Transactional
    public FinalReportResponse getFinalReport(
            @RequestParam("userId") String userId,
            @RequestParam("country") String country) {


        FinalReportResponse response = new FinalReportResponse();

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
        int fee = (int) (report.getTotalSpent_won() * 0.0175);
        response.setFeeSavings(fee);

        User user = userService.findOne(Long.parseLong(userId));
        user.setTravelState("after");


        return response;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyReportResponse {
        private int day;
        private int totalSpent;
        private Map<String, Integer> breakdown;
        private int feeSavings;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FinalReportResponse {
        private int totalSpent;
        private Map<String, Integer> breakdown;
        private int feeSavings;
    }


}


