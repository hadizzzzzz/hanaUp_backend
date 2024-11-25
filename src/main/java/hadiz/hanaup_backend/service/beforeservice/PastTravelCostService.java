package hadiz.hanaup_backend.service.beforeservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PastTravelCostService {

    @Autowired
    private final CPIDataService cpiDataService; // CPI 데이터를 가져오는 서비스 가정

    /**
     * 과거 결제 데이터를 기반으로 여행 경비를 예측하는 메서드
     * @return 예상 여행 경비
     */
    @Transactional
    public double predictTravelCost(String country, int duration) {


        // 여행 국가와 여행 기간 등의 정보를 기반으로 CPI 데이터를 가져옴
        Double pastcpi = cpiDataService.getCpiForCountry("korea");
        Double futurecpi = cpiDataService.getCpiForCountry(country);

        Double cpiFactor = futurecpi / pastcpi;
        System.out.println("cpiFactor = " + cpiFactor);

        // 과거 여행 비용 데이터 (일)
        Double pastExpenses = 223984.0; // 한국 여행에 하루 22만 3984원 소비
        System.out.println("pastExpenses = " + pastExpenses);


        // CPI 가중치를 적용하여 최종 예측 금액을 계산
        double predictedCost = pastExpenses * cpiFactor * duration;
        System.out.println("predictedCost = " + predictedCost);


        return predictedCost;
    }



}
