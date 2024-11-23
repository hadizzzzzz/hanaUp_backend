package hadiz.hanaup_backend.service.beforeservice;

import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import hadiz.hanaup_backend.repository.before.PastTravelCostPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PastTravelCostService {

    @Autowired
    private final PastTravelCostPredictionRepository pastTravelCostPredictionRepository;

    @Autowired
    private final CPIDataService cpiDataService; // CPI 데이터를 가져오는 서비스 가정

    /**
     * 과거 결제 데이터를 기반으로 여행 경비를 예측하는 메서드
     * @return 예상 여행 경비
     */
    @Transactional
    public double predictTravelCost(String country, int duration) {

        PastTravelCostPrediction prediction = new PastTravelCostPrediction();
        prediction.setCountry(country);

        // 사용자의 과거 여행 로그를 가져옴
        /*List<TravelLog> travelLogs = findLog(userId);

        TravelLog foundTravelLog = null;
        for (TravelLog t : travelLogs) {
            if (t.getLogID().equals(travelLogId)) {
                foundTravelLog = t;  // 원하는 travelLogId에 해당하는 TravelLog를 찾음
                break;  // 찾았으므로 루프 종료
            }
        }*/


        // 여행 국가와 여행 기간 등의 정보를 기반으로 CPI 데이터를 가져옴
        Double pastcpi = cpiDataService.getCpiForCountry("korea");
        Double futurecpi = cpiDataService.getCpiForCountry(prediction.getCountry());

        Double cpiFactor = futurecpi / pastcpi;
        System.out.println("cpiFactor = " + cpiFactor);

        // 과거 여행 비용 데이터 (일)
        Double pastExpenses = 223984.0; // 한국 여행에 하루 22만 3984원 소비
        System.out.println("pastExpenses = " + pastExpenses);


        // CPI 가중치를 적용하여 최종 예측 금액을 계산
        double predictedCost = pastExpenses * cpiFactor * duration;
        System.out.println("predictedCost = " + predictedCost);

        // 예측 금액을 저장
        prediction.setPredictedAmount(predictedCost);
        pastTravelCostPredictionRepository.save(prediction);

        return predictedCost;
    }



}
