package hadiz.hanaup_backend.service.beforeservice;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.domain.before.PastTravelCostPrediction;
import hadiz.hanaup_backend.repository.PastTravelCostPredictionRepository;
import hadiz.hanaup_backend.repository.TravelLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PastTravelCostService {

    @Autowired
    private final PastTravelCostPredictionRepository pastTravelCostPredictionRepository;
    @Autowired
    private final TravelLogRepository travelLogRepository;
    @Autowired
    private final CPIDataService cpiDataService; // CPI 데이터를 가져오는 서비스 가정

    /**
     * 과거 결제 데이터를 기반으로 여행 경비를 예측하는 메서드
     * @param userId 사용자 ID
     * @param travelLogId 여행 로그 ID
     * @return 예상 여행 경비
     */
    @Transactional
    public double predictTravelCost(Long userId, Long travelLogId, String country, int duration) {

        PastTravelCostPrediction prediction = new PastTravelCostPrediction();
        prediction.setCountry(country);

        // 사용자의 과거 여행 로그를 가져옴
        List<TravelLog> travelLogs = findLog(userId);

        TravelLog foundTravelLog = null;
        for (TravelLog t : travelLogs) {
            if (t.getLogID().equals(travelLogId)) {
                foundTravelLog = t;  // 원하는 travelLogId에 해당하는 TravelLog를 찾음
                break;  // 찾았으므로 루프 종료
            }
        }


        // 여행 국가와 여행 기간 등의 정보를 기반으로 CPI 데이터를 가져옴
        Double pastcpi = cpiDataService.getCpiForCountry(foundTravelLog.getDestination());
        Double futurecpi = cpiDataService.getCpiForCountry(prediction.getCountry());

        Double cpiFactor = futurecpi / pastcpi;
        System.out.println("cpiFactor = " + cpiFactor);

        // 과거 여행 비용 데이터 (일)
        Double pastExpenses = foundTravelLog.getTotalSpent() / foundTravelLog.getDuration();
        System.out.println("pastExpenses = " + pastExpenses);


        // CPI 가중치를 적용하여 최종 예측 금액을 계산
        double predictedCost = pastExpenses * cpiFactor * duration;
        System.out.println("predictedCost = " + predictedCost);

        // 예측 금액을 저장
        prediction.setPredictedAmount(predictedCost);
        pastTravelCostPredictionRepository.save(prediction);

        return predictedCost;
    }


    public List<TravelLog> findLog(Long memberId){
        return travelLogRepository.findByUserId(memberId);
    }


}
